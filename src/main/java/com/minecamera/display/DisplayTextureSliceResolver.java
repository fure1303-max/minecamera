package com.minecamera.display;

public final class DisplayTextureSliceResolver {
	private DisplayTextureSliceResolver() {
	}

	public static DisplayTextureSlice resolve(DisplayFrameState state) {
		float cellWidth = 1.0F / state.groupWidth();
		float cellHeight = 1.0F / state.groupHeight();
		float u0 = state.sliceX() * cellWidth;
		float v0 = state.sliceY() * cellHeight;
		return new DisplayTextureSlice(u0, v0, u0 + cellWidth, v0 + cellHeight);
	}
}
