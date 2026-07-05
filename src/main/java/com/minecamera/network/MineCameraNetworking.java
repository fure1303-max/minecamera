package com.minecamera.network;

import com.minecamera.logic.TripodPoseUpdatePolicy;
import com.minecamera.world.block.TripodBlock;
import com.minecamera.world.block.entity.TripodBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class MineCameraNetworking {
	private MineCameraNetworking() {
	}

	public static void initialize() {
		PayloadTypeRegistry.playC2S().register(UpdateTripodPosePayload.ID, UpdateTripodPosePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdateTripodPosePayload.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			context.server().execute(() -> applyTripodPose(player, payload));
		});
	}

	private static void applyTripodPose(ServerPlayerEntity player, UpdateTripodPosePayload payload) {
		World world = player.getEntityWorld();
		BlockState state = world.getBlockState(payload.tripodPos());
		boolean tripodTarget = state.getBlock() instanceof TripodBlock;
		boolean hasCamera = tripodTarget && state.get(TripodBlock.HAS_CAMERA);
		double squaredDistance = player.squaredDistanceTo(Vec3d.ofCenter(payload.tripodPos()));
		if (!TripodPoseUpdatePolicy.canApply(tripodTarget, hasCamera, squaredDistance)) {
			return;
		}

		if (world.getBlockEntity(payload.tripodPos()) instanceof TripodBlockEntity tripodBlockEntity) {
			if (tripodBlockEntity.setCameraPose(payload.cameraYaw(), payload.cameraPitch())) {
				tripodBlockEntity.markUpdated();
			}
		}
	}
}
