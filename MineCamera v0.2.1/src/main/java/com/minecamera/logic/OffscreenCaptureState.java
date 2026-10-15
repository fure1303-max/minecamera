package com.minecamera.logic;

public final class OffscreenCaptureState {
	private boolean active;
	private int framebufferWidth;
	private int framebufferHeight;

	public void begin(int framebufferWidth, int framebufferHeight) {
		if (framebufferWidth <= 0 || framebufferHeight <= 0) {
			throw new IllegalArgumentException("Offscreen capture dimensions must be positive");
		}

		active = true;
		this.framebufferWidth = framebufferWidth;
		this.framebufferHeight = framebufferHeight;
	}

	public void end() {
		active = false;
		framebufferWidth = 0;
		framebufferHeight = 0;
	}

	public boolean isActive() {
		return active;
	}

	public int framebufferWidthOr(int fallbackWidth) {
		return active ? framebufferWidth : fallbackWidth;
	}

	public int framebufferHeightOr(int fallbackHeight) {
		return active ? framebufferHeight : fallbackHeight;
	}

	public boolean shouldSuppressHandRendering() {
		return active;
	}
}
