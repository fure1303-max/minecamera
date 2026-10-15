package com.minecamera.logic;

public final class ResolutionValidator {
	public static final int MAX_TOTAL_PIXELS = 8_294_400;
	public static final int SOFT_EDGE_LIMIT = 4_096;

	private ResolutionValidator() {
	}

	public static ResolutionValidationResult validate(int width, int height, int runtimeTextureLimit) {
		if (width <= 0 || height <= 0) {
			return ResolutionValidationResult.invalid(width, height, ResolutionValidationError.NON_POSITIVE);
		}

		int edgeLimit = Math.min(runtimeTextureLimit, SOFT_EDGE_LIMIT);
		if (width > edgeLimit || height > edgeLimit) {
			return ResolutionValidationResult.invalid(width, height, ResolutionValidationError.EDGE_TOO_LARGE);
		}

		long totalPixels = (long) width * height;
		if (totalPixels > MAX_TOTAL_PIXELS) {
			return ResolutionValidationResult.invalid(width, height, ResolutionValidationError.TOO_MANY_PIXELS);
		}

		return ResolutionValidationResult.valid(width, height);
	}
}

