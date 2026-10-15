package com.minecamera.logic;

public final class ResolutionPresets {
	private ResolutionPresets() {
	}

	public static ResolutionPreset standardPreset(String label, int ratioWidth, int ratioHeight) {
		if (ratioWidth <= 0 || ratioHeight <= 0) {
			throw new IllegalArgumentException("Aspect ratio dimensions must be positive");
		}
		int height = 1080;
		int width = (int) Math.round((double) height * ratioWidth / ratioHeight);
		width = (width / 2) * 2;
		height = (height / 2) * 2;
		return new ResolutionPreset(label, width, height);
	}

	public static ResolutionPreset maxPreset(String label, int ratioWidth, int ratioHeight) {
		if (ratioWidth <= 0 || ratioHeight <= 0) {
			throw new IllegalArgumentException("Aspect ratio dimensions must be positive");
		}
		long area = (long) ratioWidth * ratioHeight;
		double pixelBudgetScale = Math.sqrt((double) ResolutionValidator.MAX_TOTAL_PIXELS / area);
		double edgeLimitScale = Math.min(
			(double) ResolutionValidator.SOFT_EDGE_LIMIT / ratioWidth,
			(double) ResolutionValidator.SOFT_EDGE_LIMIT / ratioHeight
		);
		double scale = Math.min(pixelBudgetScale, edgeLimitScale);
		int width = (int) Math.floor(ratioWidth * scale);
		int height = (int) Math.floor(ratioHeight * scale);
		return new ResolutionPreset(label, width, height);
	}
}
