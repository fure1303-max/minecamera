package com.minecamera.client.camera;

import com.minecamera.client.media.ClientPhotoTextureCache;
import com.minecamera.client.media.LoadedPhotoTexture;
import com.minecamera.film.FilmRecord;
import com.minecamera.film.FilmService;
import com.minecamera.logic.CameraReviewSelectionPolicy;
import com.minecamera.logic.CameraReviewStrip;
import com.minecamera.media.LocalMediaCatalog;
import com.minecamera.media.MineCameraPaths;
import com.minecamera.media.ResolvedMedia;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

final class CameraReviewService {
	private final ClientPhotoTextureCache photoTextures;

	CameraReviewService(ClientPhotoTextureCache photoTextures) {
		this.photoTextures = photoTextures;
	}

	CameraReviewState emptyState() {
		return CameraReviewState.empty(text("preview.empty"));
	}

	CameraReviewState refresh(MinecraftClient client, PlayerEntity player, String latestCapturedMediaId, String currentSelectedMediaId, boolean preferLatestCapture) {
		List<FilmRecord> films = collectReviewFilms(player);
		if (films.isEmpty()) {
			return emptyState();
		}

		String selectedMediaId = CameraReviewSelectionPolicy.resolveSelectedMediaId(
			films,
			latestCapturedMediaId,
			currentSelectedMediaId,
			preferLatestCapture
		);
		return new CameraReviewState(films, selectedMediaId, buildFrameState(client, films, selectedMediaId));
	}

	CameraReviewState refreshIfStale(
		MinecraftClient client,
		PlayerEntity player,
		CameraReviewState currentState,
		String latestCapturedMediaId,
		boolean preferLatestCapture
	) {
		List<FilmRecord> refreshedFilms = collectReviewFilms(player);
		if (refreshedFilms.equals(currentState.films())) {
			return currentState;
		}
		if (refreshedFilms.isEmpty()) {
			return emptyState();
		}

		String selectedMediaId = CameraReviewSelectionPolicy.resolveSelectedMediaId(
			refreshedFilms,
			latestCapturedMediaId,
			currentState.selectedMediaId(),
			preferLatestCapture
		);
		return new CameraReviewState(refreshedFilms, selectedMediaId, buildFrameState(client, refreshedFilms, selectedMediaId));
	}

	Text statusText(CameraReviewState state) {
		if (state.isEmpty()) {
			return text("status.no_preview_film");
		}
		return text("status.preview_index", state.frameState().index(), state.frameState().total());
	}

	Text footerText(CameraReviewState state) {
		if (state.frameState().total() <= 0) {
			return state.frameState().message();
		}
		return text("preview.review_footer", state.frameState().index(), state.frameState().total(), state.frameState().message());
	}

	private CameraReviewFrameState buildFrameState(MinecraftClient client, List<FilmRecord> films, String currentSelectedMediaId) {
		if (films.isEmpty()) {
			return CameraReviewFrameState.empty(text("preview.empty"));
		}

		String selectedMediaId = CameraReviewSelectionPolicy.resolveSelectedMediaId(films, null, currentSelectedMediaId, false);
		int selectedIndex = indexOfSelectedMediaId(films, selectedMediaId);
		FilmRecord selectedFilm = films.get(selectedIndex);

		if (selectedFilm.mediaId() == null || selectedFilm.mediaId().isBlank()) {
			return CameraReviewFrameState.message(text("preview.missing_media_id"), selectedIndex + 1, films.size());
		}

		ResolvedMedia resolvedMedia = findResolvedMedia(client, selectedFilm.mediaId());
		if (resolvedMedia == null) {
			return CameraReviewFrameState.message(text("preview.not_indexed"), selectedIndex + 1, films.size());
		}
		if (!resolvedMedia.available()) {
			return CameraReviewFrameState.message(text("preview.local_missing"), selectedIndex + 1, films.size());
		}

		LoadedPhotoTexture texture = photoTextures.resolve(client, selectedFilm.mediaId()).orElse(null);
		if (texture == null) {
			return CameraReviewFrameState.message(text("preview.load_failed", "texture"), selectedIndex + 1, films.size());
		}

		return CameraReviewFrameState.texture(
			texture,
			text("preview.meta", texture.width(), texture.height(), captureSourceText(selectedFilm.captureSource())),
			selectedIndex + 1,
			films.size()
		);
	}

	private static ResolvedMedia findResolvedMedia(MinecraftClient client, String mediaId) {
		try {
			return new LocalMediaCatalog(MineCameraPaths.fromRunDirectory(client.runDirectory.toPath()).root())
				.find(mediaId)
				.orElse(null);
		} catch (IOException exception) {
			return null;
		}
	}

	private static List<FilmRecord> collectReviewFilms(PlayerEntity player) {
		if (player == null) {
			return List.of();
		}

		List<FilmRecord> films = new ArrayList<>();
		for (int slot = 0; slot < player.getInventory().size(); slot++) {
			ItemStack stack = player.getInventory().getStack(slot);
			if (FilmService.isExposed(stack)) {
				films.add(FilmService.read(stack));
			}
		}
		return CameraReviewStrip.orderedExposedFilms(films);
	}

	private static int indexOfSelectedMediaId(List<FilmRecord> films, String mediaId) {
		for (int index = 0; index < films.size(); index++) {
			if (Objects.equals(films.get(index).mediaId(), mediaId)) {
				return index;
			}
		}
		return films.isEmpty() ? -1 : 0;
	}

	private static MutableText captureSourceText(String source) {
		return switch (source) {
			case "tripod" -> text("source.tripod");
			default -> text("source.handheld");
		};
	}

	private static MutableText text(String key, Object... args) {
		return Text.translatable("screen.minecamera." + key, args);
	}
}
