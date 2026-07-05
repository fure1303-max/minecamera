package com.minecamera.logic;

public record ViewfinderFramebufferQuadPlan(int x1, int y1, int x2, int y2, float minU, float maxU, float minV, float maxV) {
	public ViewfinderFramebufferQuadPlan {
		if (x2 <= x1 || y2 <= y1) {
			throw new IllegalArgumentException("HUD rect corner coordinates must describe a positive area");
		}
	}

	public static ViewfinderFramebufferQuadPlan forHudRect(int x, int y, int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("HUD rect dimensions must be positive");
		}
		return new ViewfinderFramebufferQuadPlan(x, y, x + width, y + height, 0.0F, 1.0F, 1.0F, 0.0F);
	}
}
