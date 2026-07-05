package com.minecamera.client.mixin;

import com.minecamera.client.camera.MineCameraModeController;
import com.minecamera.logic.CameraModeWorldInputPolicy;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
abstract class MinecraftClientInputMixin {
	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void minecamera$cancelAttackWhileCameraModeIsActive(CallbackInfoReturnable<Boolean> cir) {
		if (CameraModeWorldInputPolicy.shouldCancelWorldAction(MineCameraModeController.isActive())) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
	private void minecamera$cancelBlockBreakingWhileCameraModeIsActive(boolean breaking, CallbackInfo ci) {
		if (CameraModeWorldInputPolicy.shouldCancelContinuousBlockBreaking(MineCameraModeController.isActive())) {
			ci.cancel();
		}
	}

	@Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
	private void minecamera$cancelUseWhileCameraModeIsActive(CallbackInfo ci) {
		if (CameraModeWorldInputPolicy.shouldCancelWorldAction(MineCameraModeController.isActive())) {
			ci.cancel();
		}
	}

	@Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
	private void minecamera$cancelPickWhileCameraModeIsActive(CallbackInfo ci) {
		if (CameraModeWorldInputPolicy.shouldCancelWorldAction(MineCameraModeController.isActive())) {
			ci.cancel();
		}
	}
}
