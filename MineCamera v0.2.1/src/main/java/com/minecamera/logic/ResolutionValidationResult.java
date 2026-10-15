package com.minecamera.logic;

public record ResolutionValidationResult(boolean valid, long totalPixels, ResolutionValidationError error) {
	public static ResolutionValidationResult valid(int width, int height) {
		return new ResolutionValidationResult(true, (long) width * height, ResolutionValidationError.NONE);
	}

	public static ResolutionValidationResult invalid(int width, int height, ResolutionValidationError error) {
		return new ResolutionValidationResult(false, (long) width * height, error);
	}
}

