package com.minecamera.network;

import com.minecamera.MineCameraMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RequestDisplayThumbnailPayload(String mediaId) implements CustomPayload {
	public static final Id<RequestDisplayThumbnailPayload> ID = new Id<>(MineCameraMod.id("request_display_thumbnail"));
	public static final PacketCodec<RegistryByteBuf, RequestDisplayThumbnailPayload> CODEC = PacketCodec.tuple(
		PacketCodecs.string(64), RequestDisplayThumbnailPayload::mediaId,
		RequestDisplayThumbnailPayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
