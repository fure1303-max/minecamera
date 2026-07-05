package com.minecamera.client.render;

import com.minecamera.display.DisplayTextureSlice;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public record ResolvedDisplayTexture(
	Identifier textureId,
	RenderLayer renderLayer,
	DisplayTextureSlice slice
) {
}
