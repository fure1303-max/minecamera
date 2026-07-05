package com.minecamera.logic;

public record ReviewTextureDrawPlan(
	int drawX,
	int drawY,
	int drawWidth,
	int drawHeight,
	int regionWidth,
	int regionHeight,
	int textureWidth,
	int textureHeight
) {
	public static ReviewTextureDrawPlan create(
		int insetX,
		int insetY,
		int insetWidth,
		int insetHeight,
		int textureWidth,
		int textureHeight
	) {
		CaptureFraming.FrameRect rect = CaptureFraming.viewfinderFor(insetWidth, insetHeight, textureWidth, textureHeight);
		return new ReviewTextureDrawPlan(
			insetX + rect.x(),
			insetY + rect.y(),
			rect.width(),
			rect.height(),
			textureWidth,
			textureHeight,
			textureWidth,
			textureHeight
		);
	}

	public static ReviewTextureDrawPlan fitInto(int drawWidth, int drawHeight, int textureWidth, int textureHeight) {
		return new ReviewTextureDrawPlan(0, 0, drawWidth, drawHeight, textureWidth, textureHeight, textureWidth, textureHeight);
	}
}
