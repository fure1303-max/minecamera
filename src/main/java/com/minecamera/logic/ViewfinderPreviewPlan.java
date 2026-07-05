package com.minecamera.logic;

public record ViewfinderPreviewPlan(int width, int height) {
	private static final int MAX_LONG_EDGE = 512;

	public ViewfinderPreviewPlan {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Preview dimensions must be positive");
		}
	}

	public static ViewfinderPreviewPlan fromTarget(int targetWidth, int targetHeight) {
		if (targetWidth <= 0 || targetHeight <= 0) {
			throw new IllegalArgumentException("Target dimensions must be positive");
		}

		int longEdge = Math.max(targetWidth, targetHeight);
		if (longEdge <= MAX_LONG_EDGE) {
			return new ViewfinderPreviewPlan(targetWidth, targetHeight);
		}

		double scale = MAX_LONG_EDGE / (double) longEdge;
		int scaledWidth = Math.max(1, (int) Math.round(targetWidth * scale));
		int scaledHeight = Math.max(1, (int) Math.round(targetHeight * scale));
		return new ViewfinderPreviewPlan(scaledWidth, scaledHeight);
	}
}
