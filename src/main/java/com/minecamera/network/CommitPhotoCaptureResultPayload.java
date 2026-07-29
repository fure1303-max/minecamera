package com.minecamera.network;

import com.minecamera.MineCameraMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record CommitPhotoCaptureResultPayload(String mediaId, boolean success, String error) implements CustomPayload {
	public static final Id<CommitPhotoCaptureResultPayload> ID = new Id<>(MineCameraMod.id("commit_photo_capture_result"));
	public static final PacketCodec<RegistryByteBuf, CommitPhotoCaptureResultPayload> CODEC = PacketCodec.tuple(
		PacketCodecs.string(64), CommitPhotoCaptureResultPayload::mediaId,
		PacketCodecs.BOOLEAN, CommitPhotoCaptureResultPayload::success,
		PacketCodecs.string(128), CommitPhotoCaptureResultPayload::error,
		CommitPhotoCaptureResultPayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
