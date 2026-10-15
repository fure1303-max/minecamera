package com.minecamera.logic;

public final class OffscreenCameraRenderState {
	private boolean active;
	private int framebufferWidth;
	private int framebufferHeight;
	private int viewportWidth;
	private int viewportHeight;
	private CameraRigSnapshot activeSnapshot;
	private Object colorTextureOverride;
	private Object depthTextureOverride;

	public void begin(CameraRigSnapshot snapshot, int framebufferWidth, int framebufferHeight) {
		begin(snapshot, framebufferWidth, framebufferHeight, null, null);
	}

	public void begin(
		CameraRigSnapshot snapshot,
		int framebufferWidth,
		int framebufferHeight,
		Object colorTextureOverride,
		Object depthTextureOverride
	) {
		if (snapshot == null) {
			throw new IllegalArgumentException("Active snapshot is required");
		}
		if (framebufferWidth <= 0 || framebufferHeight <= 0) {
			throw new IllegalArgumentException("Offscreen render dimensions must be positive");
		}
		if (active) {
			throw new IllegalStateException("Offscreen render scope is already active");
		}

		active = true;
		activeSnapshot = snapshot;
		this.framebufferWidth = framebufferWidth;
		this.framebufferHeight = framebufferHeight;
		this.viewportWidth = framebufferWidth;
		this.viewportHeight = framebufferHeight;
		this.colorTextureOverride = colorTextureOverride;
		this.depthTextureOverride = depthTextureOverride;
	}

	public void end() {
		active = false;
		framebufferWidth = 0;
		framebufferHeight = 0;
		viewportWidth = 0;
		viewportHeight = 0;
		activeSnapshot = null;
		colorTextureOverride = null;
		depthTextureOverride = null;
	}

	public boolean isActive() {
		return active;
	}

	public CameraRigSnapshot activeSnapshot() {
		return activeSnapshot;
	}

	public int framebufferWidthOr(int fallbackWidth) {
		return active ? framebufferWidth : fallbackWidth;
	}

	public int framebufferHeightOr(int fallbackHeight) {
		return active ? framebufferHeight : fallbackHeight;
	}

	public int windowWidthOr(int fallbackWidth) {
		return active ? framebufferWidth : fallbackWidth;
	}

	public int windowHeightOr(int fallbackHeight) {
		return active ? framebufferHeight : fallbackHeight;
	}

	public int viewportWidthOr(int fallbackWidth) {
		return active ? viewportWidth : fallbackWidth;
	}

	public int viewportHeightOr(int fallbackHeight) {
		return active ? viewportHeight : fallbackHeight;
	}

	public Object colorTextureOverrideOr(Object fallbackOverride) {
		return active ? colorTextureOverride : fallbackOverride;
	}

	public Object depthTextureOverrideOr(Object fallbackOverride) {
		return active ? depthTextureOverride : fallbackOverride;
	}

	public float verticalFovOr(float fallbackFov) {
		return active && activeSnapshot != null ? activeSnapshot.verticalFovDegrees() : fallbackFov;
	}

	public boolean shouldSuppressHandRendering() {
		return active;
	}
}
