package com.minecamera.logic;

public record CameraModeHudLayout(CaptureFraming.FrameRect frame) {
	public static CameraModeHudLayout create(int screenWidth, int screenHeight, int targetWidth, int targetHeight) {
		if (screenWidth <= 0 || screenHeight <= 0) {
			throw new IllegalArgumentException("Screen dimensions must be positive");
		}
		if (targetWidth <= 0 || targetHeight <= 0) {
			throw new IllegalArgumentException("Target dimensions must be positive");
		}
		return new CameraModeHudLayout(CaptureFraming.viewfinderFor(screenWidth, screenHeight, targetWidth, targetHeight));
	}

	public int frameX() {
		return frame.x();
	}

	public int frameY() {
		return frame.y();
	}

	public int frameWidth() {
		return frame.width();
	}

	public int frameHeight() {
		return frame.height();
	}
}
