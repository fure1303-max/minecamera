package com.minecamera.client.render;

import com.minecamera.display.DisplaySurface;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.util.Identifier;

public final class PhotoDisplayFrameBlockEntityRenderState extends BlockEntityRenderState {
	public DisplaySurface surface;
	public Identifier textureId;
	public RenderLayer renderLayer;
	public float u0;
	public float v0;
	public float u1;
	public float v1;
	public boolean hasTexture;
}
