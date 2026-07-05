package com.minecamera.client.camera;

import com.minecamera.logic.CameraOpticalState;
import com.minecamera.logic.CameraPoseResolver;
import com.minecamera.network.UpdateTripodPosePayload;
import com.minecamera.world.block.entity.TripodBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

final class TripodPoseSyncController {
	CameraOpticalState initializeFromWorld(MinecraftClient client, BlockPos tripodPos, CameraOpticalState opticalState) {
		if (tripodPos == null || client.player == null) {
			return opticalState;
		}
		TripodBlockEntity tripod = tripodBlockEntity(client, tripodPos);
		if (tripod == null) {
			return opticalState;
		}
		if (tripod.hasCameraPose()) {
			applyPlayerLook(client.player, tripod.cameraYaw(), tripod.cameraPitch());
			return opticalState.withLastSentTripodPose(tripod.cameraYaw(), tripod.cameraPitch());
		}

		float yaw = client.player.getYaw();
		float pitch = CameraPoseResolver.clampPitch(client.player.getPitch());
		if (tripod.setCameraPose(yaw, pitch)) {
			ClientPlayNetworking.send(new UpdateTripodPosePayload(tripodPos, yaw, pitch));
			return opticalState.withLastSentTripodPose(yaw, pitch);
		}
		return opticalState;
	}

	CameraOpticalState captureLivePose(MinecraftClient client, BlockPos tripodPos, CameraOpticalState opticalState) {
		if (tripodPos == null || client.player == null) {
			return opticalState;
		}
		TripodBlockEntity tripod = tripodBlockEntity(client, tripodPos);
		if (tripod == null) {
			return opticalState;
		}
		float yaw = client.player.getYaw();
		float pitch = CameraPoseResolver.clampPitch(client.player.getPitch());
		if (!tripod.setCameraPose(yaw, pitch) || opticalState.matchesLastSentTripodPose(yaw, pitch)) {
			return opticalState;
		}
		ClientPlayNetworking.send(new UpdateTripodPosePayload(tripodPos, yaw, pitch));
		return opticalState.withLastSentTripodPose(yaw, pitch);
	}

	private static TripodBlockEntity tripodBlockEntity(MinecraftClient client, BlockPos tripodPos) {
		if (client.world == null) {
			return null;
		}
		return client.world.getBlockEntity(tripodPos) instanceof TripodBlockEntity tripod ? tripod : null;
	}

	private static void applyPlayerLook(PlayerEntity player, float yaw, float pitch) {
		player.setYaw(yaw);
		player.setPitch(pitch);
		player.setBodyYaw(yaw);
		player.setHeadYaw(yaw);
	}
}
