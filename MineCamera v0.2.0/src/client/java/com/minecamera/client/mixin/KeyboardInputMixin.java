package com.minecamera.client.mixin;

import com.minecamera.client.camera.MineCameraModeController;
import com.minecamera.logic.CameraModeKeyEventPolicy;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
abstract class KeyboardInputMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
	private void minecamera$consumeCameraModeKeys(long window, int action, KeyInput keyInput, CallbackInfo ci) {
		if (client.options == null) {
			return;
		}
		boolean pressOrRepeat = action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT;
		boolean consume = CameraModeKeyEventPolicy.shouldConsumeKeyEvent(
			MineCameraModeController.isActive(),
			MineCameraModeController.previewMode(),
			pressOrRepeat,
			client.options.togglePerspectiveKey.matchesKey(keyInput),
			client.options.leftKey.matchesKey(keyInput),
			client.options.rightKey.matchesKey(keyInput)
		);
		if (!consume) {
			return;
		}
		client.options.togglePerspectiveKey.setPressed(false);
		client.options.leftKey.setPressed(false);
		client.options.rightKey.setPressed(false);
		ci.cancel();
	}
}
