package com.minecamera.client.mixin;

import com.minecamera.client.capture.MineCameraCaptureRenderContext;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Window.class)
abstract class WindowFramebufferOverrideMixin {
	@Inject(method = "getFramebufferWidth", at = @At("HEAD"), cancellable = true)
	private void minecamera$overrideFramebufferWidth(CallbackInfoReturnable<Integer> cir) {
		if (MineCameraCaptureRenderContext.isActive()) {
			cir.setReturnValue(MineCameraCaptureRenderContext.framebufferWidthOr(1));
		}
	}

	@Inject(method = "getFramebufferHeight", at = @At("HEAD"), cancellable = true)
	private void minecamera$overrideFramebufferHeight(CallbackInfoReturnable<Integer> cir) {
		if (MineCameraCaptureRenderContext.isActive()) {
			cir.setReturnValue(MineCameraCaptureRenderContext.framebufferHeightOr(1));
		}
	}

	@Inject(method = "getWidth", at = @At("HEAD"), cancellable = true)
	private void minecamera$overrideWindowWidth(CallbackInfoReturnable<Integer> cir) {
		if (MineCameraCaptureRenderContext.isActive()) {
			cir.setReturnValue(MineCameraCaptureRenderContext.windowWidthOr(1));
		}
	}

	@Inject(method = "getHeight", at = @At("HEAD"), cancellable = true)
	private void minecamera$overrideWindowHeight(CallbackInfoReturnable<Integer> cir) {
		if (MineCameraCaptureRenderContext.isActive()) {
			cir.setReturnValue(MineCameraCaptureRenderContext.windowHeightOr(1));
		}
	}
}
