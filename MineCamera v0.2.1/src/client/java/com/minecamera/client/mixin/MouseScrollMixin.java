package com.minecamera.client.mixin;

import com.minecamera.client.camera.MineCameraModeController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
abstract class MouseScrollMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
	private void minecamera$consumeCameraModeScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
		if (MineCameraModeController.handleMouseScroll(client, vertical)) {
			ci.cancel();
		}
	}
}
