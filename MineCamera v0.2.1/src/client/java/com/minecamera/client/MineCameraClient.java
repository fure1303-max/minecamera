package com.minecamera.client;

import com.minecamera.client.camera.MineCameraModeController;
import com.minecamera.client.network.ClientMineCameraNetworking;
import com.minecamera.client.render.PhotoDisplayFrameBlockEntityRenderer;
import com.minecamera.registry.MineCameraBlockEntities;
import com.minecamera.item.CameraClientHooks;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;

public final class MineCameraClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CameraClientHooks.openHeldCamera = MineCameraModeController::openHandheld;
		CameraClientHooks.openMountedCamera = MineCameraModeController::openTripod;
		MineCameraModeController.initialize();
		ClientMineCameraNetworking.initialize();
		BlockEntityRendererRegistry.register(
			MineCameraBlockEntities.PHOTO_DISPLAY_FRAME,
			(context) -> new PhotoDisplayFrameBlockEntityRenderer(context)
		);
	}
}
