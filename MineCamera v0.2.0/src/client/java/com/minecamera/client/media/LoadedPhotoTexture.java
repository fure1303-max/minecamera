package com.minecamera.client.media;

import net.minecraft.util.Identifier;

public record LoadedPhotoTexture(
	String mediaId,
	Identifier textureId,
	int width,
	int height
) {
}
