package com.minecamera.logic;

public enum CameraPreviewMode {
	LIVE,
	REVIEW;

	public CameraPreviewMode next() {
		return this == LIVE ? REVIEW : LIVE;
	}
}
