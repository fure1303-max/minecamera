package com.minecamera.client.network;

import com.minecamera.client.capture.MineCameraCaptureCoordinator;
import com.minecamera.client.media.ClientDisplayMediaSync;
import com.minecamera.film.FilmRecord;
import com.minecamera.film.FilmService;
import com.minecamera.network.CommitPhotoCaptureResultPayload;
import com.minecamera.network.DisplayThumbnailPayload;
import com.minecamera.registry.MineCameraBlocks;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public final class ClientMineCameraNetworking {
	private ClientMineCameraNetworking() {
	}

	public static void initialize() {
		ClientPlayNetworking.registerGlobalReceiver(CommitPhotoCaptureResultPayload.ID, (payload, context) ->
			context.client().execute(() -> MineCameraCaptureCoordinator.onCommitResult(payload))
		);
		ClientPlayNetworking.registerGlobalReceiver(DisplayThumbnailPayload.ID, (payload, context) ->
			context.client().execute(() -> ClientDisplayMediaSync.accept(context.client(), payload))
		);
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!world.isClient() || !world.getBlockState(hitResult.getBlockPos()).isOf(MineCameraBlocks.PHOTO_DISPLAY_FRAME)) {
				return ActionResult.PASS;
			}
			if (!FilmService.isExposed(player.getStackInHand(hand))) {
				return ActionResult.PASS;
			}
			FilmRecord record = FilmService.read(player.getStackInHand(hand));
			ClientDisplayMediaSync.publishIfAvailable(MinecraftClient.getInstance(), record.mediaId());
			return ActionResult.PASS;
		});
	}
}
