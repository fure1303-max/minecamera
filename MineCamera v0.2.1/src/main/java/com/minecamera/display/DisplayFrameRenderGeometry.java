package com.minecamera.display;

public record DisplayFrameRenderGeometry(
	float leftInset,
	float topInset,
	float rightInset,
	float bottomInset
) {
	public static final float DEFAULT_INSET = 1.0F / 16.0F;

	public static DisplayFrameRenderGeometry forState(DisplayFrameState state) {
		return forState(state, DEFAULT_INSET);
	}

	public static DisplayFrameRenderGeometry forState(DisplayFrameState state, float outerInset) {
		if (state == null || state.isEmpty()) {
			throw new IllegalArgumentException("Display frame state must contain a photo group");
		}
		validateInset(outerInset);
		return new DisplayFrameRenderGeometry(
			state.sliceX() == 0 ? outerInset : 0.0F,
			state.sliceY() == 0 ? outerInset : 0.0F,
			state.sliceX() == state.groupWidth() - 1 ? outerInset : 0.0F,
			state.sliceY() == state.groupHeight() - 1 ? outerInset : 0.0F
		);
	}

	public static DisplayFrameRenderGeometry fromInset(float inset) {
		validateInset(inset);
		return new DisplayFrameRenderGeometry(inset, inset, inset, inset);
	}

	public float width() {
		return 1.0F - leftInset - rightInset;
	}

	public float height() {
		return 1.0F - topInset - bottomInset;
	}

	private static void validateInset(float inset) {
		if (inset < 0.0F || inset >= 0.5F) {
			throw new IllegalArgumentException("Display frame inset must be in [0, 0.5)");
		}
	}
}
