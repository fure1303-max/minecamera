package com.minecamera.client.mixin;

import com.minecamera.client.camera.MineCameraModeController;
import com.minecamera.client.capture.MineCameraCaptureCoordinator;
import com.minecamera.client.capture.MineCameraCaptureRenderContext;
import com.minecamera.logic.CameraRigSnapshot;
import com.minecamera.logic.CameraViewportOverridePolicy;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
	@Inject(method = "updateCamera", at = @At("TAIL"))
	private void minecamera$applyOffscreenCameraOverride(RenderTickCounter tickCounter, CallbackInfo ci) {
		GameRenderer renderer = (GameRenderer) (Object) this;
		CameraRigSnapshot snapshot = CameraViewportOverridePolicy.selectSnapshot(
			MineCameraCaptureRenderContext.activeSnapshot(),
			MineCameraModeController.currentWorldViewportSnapshot(renderer.getClient())
		);
		if (snapshot == null) {
			return;
		}
		Camera camera = renderer.getCamera();
		CameraAccessor accessor = (CameraAccessor) (Object) camera;
		accessor.minecamera$setPos(snapshot.position());
		accessor.minecamera$setRotation(snapshot.yaw(), snapshot.pitch());
	}

	@Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
	private void minecamera$overrideOffscreenFov(Camera camera, float tickProgress, boolean changingFov, CallbackInfoReturnable<Float> cir) {
		GameRenderer renderer = (GameRenderer) (Object) this;
		CameraRigSnapshot snapshot = CameraViewportOverridePolicy.selectSnapshot(
			MineCameraCaptureRenderContext.activeSnapshot(),
			MineCameraModeController.currentWorldViewportSnapshot(renderer.getClient())
		);
		if (snapshot != null) {
			cir.setReturnValue(snapshot.verticalFovDegrees());
		}
	}

	@Inject(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/GameRenderer;renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V",
			shift = At.Shift.AFTER
		)
	)
	private void minecamera$captureBeforeHud(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
		GameRenderer renderer = (GameRenderer) (Object) this;
		MineCameraModeController.refreshViewfinderPreview(renderer.getClient(), tickCounter);
		MineCameraCaptureCoordinator.capturePendingFrame(renderer.getClient(), tickCounter);
	}

	@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
	private void minecamera$suppressHandForOffscreenCapture(float tickProgress, boolean bobView, Matrix4f matrix, CallbackInfo ci) {
		if (MineCameraCaptureRenderContext.shouldSuppressHandRendering()) {
			ci.cancel();
		}
	}
}
