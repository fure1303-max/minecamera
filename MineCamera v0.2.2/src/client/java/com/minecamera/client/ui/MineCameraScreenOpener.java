package com.minecamera.client.ui;

import com.minecamera.client.camera.MineCameraModeController;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public final class MineCameraScreenOpener {
	private MineCameraScreenOpener() {
	}

	public static void openHeldCamera(Hand hand) {
		MineCameraModeController.openHandheld(hand);
	}

	public static void openMountedCamera(Hand hand, BlockPos pos) {
		MineCameraModeController.openTripod(hand, pos);
	}
}
