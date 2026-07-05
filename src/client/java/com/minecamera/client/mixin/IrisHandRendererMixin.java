package com.minecamera.client.mixin;

import com.minecamera.client.capture.MineCameraCaptureRenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.pathways.HandRenderer", remap = false)
abstract class IrisHandRendererMixin {
	@Inject(method = { "renderSolid", "renderTranslucent" }, at = @At("HEAD"), cancellable = true, remap = false)
	private void minecamera$suppressIrisHandRenderer(CallbackInfo ci) {
		if (MineCameraCaptureRenderContext.shouldSuppressHandRendering()) {
			ci.cancel();
		}
	}
}
