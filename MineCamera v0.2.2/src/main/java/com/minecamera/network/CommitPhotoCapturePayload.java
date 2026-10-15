package com.minecamera.network;

import com.minecamera.MineCameraMod;
import com.minecamera.media.DisplayThumbnailPolicy;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record CommitPhotoCapturePayload(
	int filmSlot,
	String mediaId,
	int width,
	int height,
	int focalLength,
	double aperture,
	String focusMode,
	String author,
	long capturedAtEpochSeconds,
	String captureSource,
	byte[] thumbnailBytes
) implements CustomPayload {
	public static final Id<CommitPhotoCapturePayload> ID = new Id<>(MineCameraMod.id("commit_photo_capture"));
	public static final PacketCodec<RegistryByteBuf, CommitPhotoCapturePayload> CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT, CommitPhotoCapturePayload::filmSlot,
		PacketCodecs.string(64), CommitPhotoCapturePayload::mediaId,
		PacketCodecs.VAR_INT, CommitPhotoCapturePayload::width,
		PacketCodecs.VAR_INT, CommitPhotoCapturePayload::height,
		PacketCodecs.VAR_INT, CommitPhotoCapturePayload::focalLength,
		PacketCodecs.DOUBLE, CommitPhotoCapturePayload::aperture,
		PacketCodecs.string(16), CommitPhotoCapturePayload::focusMode,
		PacketCodecs.string(64), CommitPhotoCapturePayload::author,
		PacketCodecs.VAR_LONG, CommitPhotoCapturePayload::capturedAtEpochSeconds,
		PacketCodecs.string(32), CommitPhotoCapturePayload::captureSource,
		PacketCodecs.byteArray(DisplayThumbnailPolicy.MAX_BYTES), CommitPhotoCapturePayload::thumbnailBytes,
		CommitPhotoCapturePayload::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
