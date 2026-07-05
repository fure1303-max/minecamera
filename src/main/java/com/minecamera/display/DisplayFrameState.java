package com.minecamera.display;

import com.minecamera.logic.GridPoint;

public record DisplayFrameState(
	String mediaId,
	GridPoint groupAnchor,
	int groupWidth,
	int groupHeight,
	int sliceX,
	int sliceY
) {
	public static DisplayFrameState empty() {
		return new DisplayFrameState(null, null, 0, 0, 0, 0);
	}

	public boolean isEmpty() {
		return mediaId == null || mediaId.isBlank() || groupAnchor == null || groupWidth <= 0 || groupHeight <= 0;
	}
}
