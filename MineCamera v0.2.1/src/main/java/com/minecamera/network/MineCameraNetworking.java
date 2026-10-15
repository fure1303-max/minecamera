package com.minecamera.network;

import com.minecamera.film.CaptureSettings;
import com.minecamera.film.FilmContentType;
import com.minecamera.film.FilmRecord;
import com.minecamera.film.FilmService;
import com.minecamera.logic.CameraFocalLengthPolicy;
import com.minecamera.logic.FocusMode;
import com.minecamera.logic.ResolutionValidator;
import com.minecamera.logic.TripodPoseUpdatePolicy;
import com.minecamera.media.DisplayThumbnailPolicy;
import com.minecamera.media.ServerDisplayMediaStore;
import com.minecamera.world.block.TripodBlock;
import com.minecamera.world.block.entity.TripodBlockEntity;
import java.io.IOException;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class MineCameraNetworking {
	private static final long THUMBNAIL_PLAYER_COOLDOWN_MILLIS = 100L;
	private static final long THUMBNAIL_MEDIA_COOLDOWN_MILLIS = 2_000L;
	private static final DisplayThumbnailRequestLimiter THUMBNAIL_REQUEST_LIMITER = new DisplayThumbnailRequestLimiter(
		THUMBNAIL_PLAYER_COOLDOWN_MILLIS,
		THUMBNAIL_MEDIA_COOLDOWN_MILLIS
	);
	private static final String ERROR_INVALID_CAPTURE = "invalid_capture";
	private static final String ERROR_FILM_CHANGED = "film_changed";
	private static final String ERROR_INVENTORY_FULL = "inventory_full";
	private static final String ERROR_MEDIA_STORE = "media_store_failed";

	private MineCameraNetworking() {
	}

	public static void initialize() {
		PayloadTypeRegistry.playC2S().register(UpdateTripodPosePayload.ID, UpdateTripodPosePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(CommitPhotoCapturePayload.ID, CommitPhotoCapturePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PublishDisplayThumbnailPayload.ID, PublishDisplayThumbnailPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(RequestDisplayThumbnailPayload.ID, RequestDisplayThumbnailPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(CommitPhotoCaptureResultPayload.ID, CommitPhotoCaptureResultPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(DisplayThumbnailPayload.ID, DisplayThumbnailPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(UpdateTripodPosePayload.ID, (payload, context) ->
			context.server().execute(() -> applyTripodPose(context.player(), payload))
		);
		ServerPlayNetworking.registerGlobalReceiver(CommitPhotoCapturePayload.ID, (payload, context) ->
			context.server().execute(() -> commitPhotoCapture(context.player(), payload))
		);
		ServerPlayNetworking.registerGlobalReceiver(PublishDisplayThumbnailPayload.ID, (payload, context) ->
			context.server().execute(() -> publishDisplayThumbnail(context.player(), payload))
		);
		ServerPlayNetworking.registerGlobalReceiver(RequestDisplayThumbnailPayload.ID, (payload, context) ->
			context.server().execute(() -> sendDisplayThumbnail(context.player(), payload.mediaId()))
		);
	}

	private static void commitPhotoCapture(ServerPlayerEntity player, CommitPhotoCapturePayload payload) {
		String validationError = validateCapture(player, payload);
		if (validationError != null) {
			sendCommitResult(player, payload.mediaId(), false, validationError);
			return;
		}

		try {
			if (!ServerDisplayMediaStore.fromServer(player.getEntityWorld().getServer()).write(payload.mediaId(), payload.thumbnailBytes())) {
				sendCommitResult(player, payload.mediaId(), false, ERROR_MEDIA_STORE);
				return;
			}
		} catch (IOException exception) {
			sendCommitResult(player, payload.mediaId(), false, ERROR_MEDIA_STORE);
			return;
		}

		FocusMode focusMode = FocusMode.valueOf(payload.focusMode());
		FilmRecord record = FilmRecord.exposed(
			FilmContentType.PHOTO,
			payload.mediaId(),
			new CaptureSettings(payload.width(), payload.height(), payload.focalLength(), payload.aperture(), focusMode),
			player.getName().getString(),
			payload.capturedAtEpochSeconds(),
			payload.captureSource()
		);

		PlayerInventory inventory = player.getInventory();
		ItemStack blankFilm = inventory.getStack(payload.filmSlot());
		int emptySlot = FilmService.requiresSeparateResultStack(blankFilm.getCount()) ? inventory.getEmptySlot() : -1;
		ItemStack exposedFilm = FilmService.createExposedFilm(blankFilm, record);
		if (exposedFilm != blankFilm) {
			inventory.setStack(emptySlot, exposedFilm);
		}
		inventory.markDirty();
		player.currentScreenHandler.sendContentUpdates();
		sendCommitResult(player, payload.mediaId(), true, "");
	}

	private static String validateCapture(ServerPlayerEntity player, CommitPhotoCapturePayload payload) {
		if (!DisplayThumbnailPolicy.isValidMediaId(payload.mediaId())
			|| DisplayThumbnailPolicy.inspectJpeg(payload.thumbnailBytes()).isEmpty()
			|| !ResolutionValidator.validate(payload.width(), payload.height(), ResolutionValidator.SOFT_EDGE_LIMIT).valid()
			|| payload.focalLength() != CameraFocalLengthPolicy.clamp(payload.focalLength())
			|| !Double.isFinite(payload.aperture())
			|| payload.aperture() < 0.1D
			|| payload.aperture() > 64.0D
			|| !isValidFocusMode(payload.focusMode())
			|| payload.capturedAtEpochSeconds() <= 0L
			|| payload.captureSource() == null
			|| payload.captureSource().isBlank()) {
			return ERROR_INVALID_CAPTURE;
		}

		PlayerInventory inventory = player.getInventory();
		if (payload.filmSlot() < 0 || payload.filmSlot() >= inventory.size()) {
			return ERROR_FILM_CHANGED;
		}
		ItemStack stack = inventory.getStack(payload.filmSlot());
		if (!FilmService.isFilm(stack) || !FilmService.isBlank(stack)) {
			return ERROR_FILM_CHANGED;
		}
		if (FilmService.requiresSeparateResultStack(stack.getCount()) && inventory.getEmptySlot() < 0) {
			return ERROR_INVENTORY_FULL;
		}
		return null;
	}

	private static boolean isValidFocusMode(String value) {
		try {
			FocusMode.valueOf(value);
			return true;
		} catch (IllegalArgumentException | NullPointerException exception) {
			return false;
		}
	}

	private static void publishDisplayThumbnail(ServerPlayerEntity player, PublishDisplayThumbnailPayload payload) {
		if (!holdsMatchingFilm(player, payload.mediaId()) || DisplayThumbnailPolicy.inspectJpeg(payload.thumbnailBytes()).isEmpty()) {
			return;
		}
		try {
			ServerDisplayMediaStore.fromServer(player.getEntityWorld().getServer()).write(payload.mediaId(), payload.thumbnailBytes());
		} catch (IOException ignored) {
		}
	}

	private static boolean holdsMatchingFilm(ServerPlayerEntity player, String mediaId) {
		return matchesFilm(player.getMainHandStack(), mediaId) || matchesFilm(player.getOffHandStack(), mediaId);
	}

	private static boolean matchesFilm(ItemStack stack, String mediaId) {
		if (!FilmService.isExposed(stack)) {
			return false;
		}
		FilmRecord record = FilmService.read(stack);
		return mediaId != null && mediaId.equals(record.mediaId());
	}

	private static void sendDisplayThumbnail(ServerPlayerEntity player, String mediaId) {
		if (!DisplayThumbnailPolicy.isValidMediaId(mediaId)
			|| !THUMBNAIL_REQUEST_LIMITER.tryAcquire(player.getUuid(), mediaId, System.currentTimeMillis())) {
			return;
		}
		byte[] bytes = new byte[0];
		try {
			bytes = ServerDisplayMediaStore.fromServer(player.getEntityWorld().getServer()).read(mediaId).orElse(bytes);
		} catch (IOException ignored) {
		}
		ServerPlayNetworking.send(player, new DisplayThumbnailPayload(mediaId, bytes));
	}

	private static void sendCommitResult(ServerPlayerEntity player, String mediaId, boolean success, String error) {
		ServerPlayNetworking.send(player, new CommitPhotoCaptureResultPayload(mediaId, success, error));
	}

	private static void applyTripodPose(ServerPlayerEntity player, UpdateTripodPosePayload payload) {
		World world = player.getEntityWorld();
		BlockState state = world.getBlockState(payload.tripodPos());
		boolean tripodTarget = state.getBlock() instanceof TripodBlock;
		boolean hasCamera = tripodTarget && state.get(TripodBlock.HAS_CAMERA);
		double squaredDistance = player.squaredDistanceTo(Vec3d.ofCenter(payload.tripodPos()));
		if (!TripodPoseUpdatePolicy.canApply(tripodTarget, hasCamera, squaredDistance)) {
			return;
		}

		if (world.getBlockEntity(payload.tripodPos()) instanceof TripodBlockEntity tripodBlockEntity) {
			if (tripodBlockEntity.setCameraPose(payload.cameraYaw(), payload.cameraPitch())) {
				tripodBlockEntity.markUpdated();
			}
		}
	}
}
