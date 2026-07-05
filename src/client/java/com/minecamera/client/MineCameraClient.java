package com.minecamera.client;

import com.minecamera.client.camera.MineCameraModeController;
import com.minecamera.client.capture.MineCameraCaptureCoordinator;
import com.minecamera.client.render.PhotoDisplayFrameBlockEntityRenderer;
import com.minecamera.registry.MineCameraBlockEntities;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;

public final class MineCameraClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MineCameraModeController.initialize();
		BlockEntityRendererRegistry.register(
			MineCameraBlockEntities.PHOTO_DISPLAY_FRAME,
			(context) -> new PhotoDisplayFrameBlockEntityRenderer(context)
		);
	}
}
