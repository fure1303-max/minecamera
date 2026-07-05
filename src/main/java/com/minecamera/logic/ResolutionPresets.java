package com.minecamera.logic;

public final class ResolutionPresets {
	private ResolutionPresets() {
	}

	public static ResolutionPreset maxPreset(String label, int ratioWidth, int ratioHeight) {
		long area = (long) ratioWidth * ratioHeight;
		double scale = Math.sqrt((double) ResolutionValidator.MAX_TOTAL_PIXELS / area);
		int width = (int) Math.floor(ratioWidth * scale);
		int height = (int) Math.floor(ratioHeight * scale);
		return new ResolutionPreset(label, width, height);
	}
}

