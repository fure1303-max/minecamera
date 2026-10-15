package com.minecamera.network;

import com.minecamera.MineCameraMod;
import com.minecamera.media.DisplayThumbnailPolicy;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record DisplayThumbnailPayload(String mediaId, byte[] thumbnailBytes) implements CustomPayload {
	public static final Id<DisplayThumbnailPayload> ID = new Id<>(MineCameraMod.id("display_thumbnail"));
	public static final PacketCodec<RegistryByteBuf, DisplayThumbnailPayload> CODEC = PacketCodec.tuple(
		PacketCodecs.string(64), DisplayThumbnailPayload::mediaId,
		PacketCodecs.byteArray(DisplayThumbnailPolicy.MAX_BYTES), DisplayThumbnailPayload::thumbnailBytes,
		DisplayThumbnailPayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
