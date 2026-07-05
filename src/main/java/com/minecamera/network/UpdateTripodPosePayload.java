package com.minecamera.network;

import com.minecamera.MineCameraMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record UpdateTripodPosePayload(BlockPos tripodPos, float cameraYaw, float cameraPitch) implements CustomPayload {
	public static final Id<UpdateTripodPosePayload> ID = new Id<>(MineCameraMod.id("update_tripod_pose"));
	public static final PacketCodec<RegistryByteBuf, UpdateTripodPosePayload> CODEC = PacketCodec.tuple(
		BlockPos.PACKET_CODEC,
		UpdateTripodPosePayload::tripodPos,
		PacketCodecs.FLOAT,
		UpdateTripodPosePayload::cameraYaw,
		PacketCodecs.FLOAT,
		UpdateTripodPosePayload::cameraPitch,
		UpdateTripodPosePayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
