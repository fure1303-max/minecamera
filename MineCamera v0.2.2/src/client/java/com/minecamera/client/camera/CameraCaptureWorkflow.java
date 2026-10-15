package com.minecamera.client.camera;

import com.minecamera.client.capture.MineCameraCaptureRequest;
import com.minecamera.film.FilmService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

final class CameraCaptureWorkflow {
	record CaptureAttempt(Text status, MineCameraCaptureRequest request) {
		boolean accepted() {
			return request != null;
		}
	}

	CaptureAttempt prepareRequest(MinecraftClient client, CameraModeRuntimeState runtime, CameraSnapshotProvider snapshotProvider) {
		PlayerEntity player = client.player;
		if (player == null) {
			return failure("screen.minecamera.status.player_missing");
		}
		int blankFilmSlot = findFirstBlankFilmSlot(player);
		if (blankFilmSlot < 0) {
			return failure("screen.minecamera.status.need_blank_film");
		}

		ItemStack blankFilm = player.getInventory().getStack(blankFilmSlot);
		if (FilmService.requiresSeparateResultStack(blankFilm.getCount()) && player.getInventory().getEmptySlot() < 0) {
			return failure("screen.minecamera.status.need_empty_slot");
		}

		var snapshot = snapshotProvider.build(client);
		if (snapshot == null) {
			return failure("screen.minecamera.status.player_missing");
		}
		return new CaptureAttempt(Text.translatable("screen.minecamera.status.capturing"), new MineCameraCaptureRequest(blankFilmSlot, snapshot));
	}

	private static CaptureAttempt failure(String translationKey) {
		return new CaptureAttempt(Text.translatable(translationKey), null);
	}

	private static int findFirstBlankFilmSlot(PlayerEntity player) {
		for (int slot = 0; slot < player.getInventory().size(); slot++) {
			ItemStack stack = player.getInventory().getStack(slot);
			if (FilmService.isFilm(stack) && FilmService.isBlank(stack)) {
				return slot;
			}
		}
		return -1;
	}

	@FunctionalInterface
	interface CameraSnapshotProvider {
		com.minecamera.logic.CameraRigSnapshot build(MinecraftClient client);
	}
}
