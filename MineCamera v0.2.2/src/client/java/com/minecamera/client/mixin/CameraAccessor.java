package com.minecamera.client.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
	@Invoker("setPos")
	void minecamera$setPos(Vec3d pos);

	@Invoker("setRotation")
	void minecamera$setRotation(float yaw, float pitch);
}
