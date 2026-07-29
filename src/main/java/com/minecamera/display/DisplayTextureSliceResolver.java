package com.minecamera.display;

public final class DisplayTextureSliceResolver {
	private DisplayTextureSliceResolver() {
	}

	public static DisplayTextureSlice resolve(DisplayFrameState state, int textureWidth, int textureHeight) {
		if (state == null || state.isEmpty()) {
			throw new IllegalArgumentException("Display frame state must contain a photo group");
		}
		if (textureWidth <= 0 || textureHeight <= 0) {
			throw new IllegalArgumentException("Texture dimensions must be positive");
		}

		float outerInset = DisplayFrameRenderGeometry.DEFAULT_INSET;
		DisplayFrameRenderGeometry geometry = DisplayFrameRenderGeometry.forState(state, outerInset);
		float groupPhotoWidth = state.groupWidth() - outerInset * 2.0F;
		float groupPhotoHeight = state.groupHeight() - outerInset * 2.0F;
		float targetAspect = groupPhotoWidth / groupPhotoHeight;
		float sourceAspect = (float) textureWidth / textureHeight;

		float cropU0 = 0.0F;
		float cropV0 = 0.0F;
		float cropU1 = 1.0F;
		float cropV1 = 1.0F;
		if (sourceAspect > targetAspect) {
			float visibleWidth = targetAspect / sourceAspect;
			cropU0 = (1.0F - visibleWidth) * 0.5F;
			cropU1 = cropU0 + visibleWidth;
		} else if (sourceAspect < targetAspect) {
			float visibleHeight = sourceAspect / targetAspect;
			cropV0 = (1.0F - visibleHeight) * 0.5F;
			cropV1 = cropV0 + visibleHeight;
		}

		float groupX0 = (state.sliceX() + geometry.leftInset() - outerInset) / groupPhotoWidth;
		float groupX1 = (state.sliceX() + 1.0F - geometry.rightInset() - outerInset) / groupPhotoWidth;
		float groupY0 = (state.sliceY() + geometry.topInset() - outerInset) / groupPhotoHeight;
		float groupY1 = (state.sliceY() + 1.0F - geometry.bottomInset() - outerInset) / groupPhotoHeight;

		return new DisplayTextureSlice(
			lerp(cropU0, cropU1, groupX0),
			lerp(cropV0, cropV1, groupY0),
			lerp(cropU0, cropU1, groupX1),
			lerp(cropV0, cropV1, groupY1)
		);
	}

	private static float lerp(float start, float end, float progress) {
		return start + (end - start) * progress;
	}
}
