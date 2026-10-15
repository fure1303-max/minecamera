package com.minecamera.network;

import com.minecamera.MineCameraMod;
import com.minecamera.media.DisplayThumbnailPolicy;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record PublishDisplayThumbnailPayload(String mediaId, byte[] thumbnailBytes) implements CustomPayload {
	public static final Id<PublishDisplayThumbnailPayload> ID = new Id<>(MineCameraMod.id("publish_display_thumbnail"));
	public static final PacketCodec<RegistryByteBuf, PublishDisplayThumbnailPayload> CODEC = PacketCodec.tuple(
		PacketCodecs.string(64), PublishDisplayThumbnailPayload::mediaId,
		PacketCodecs.byteArray(DisplayThumbnailPolicy.MAX_BYTES), PublishDisplayThumbnailPayload::thumbnailBytes,
		PublishDisplayThumbnailPayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
