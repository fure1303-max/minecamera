package com.minecamera.logic;

public final class CaptureFraming {
	private CaptureFraming() {
	}

	public static CropRect cropFor(int sourceWidth, int sourceHeight, int targetWidth, int targetHeight) {
		double sourceAspect = sourceWidth / (double) sourceHeight;
		double targetAspect = targetWidth / (double) targetHeight;
		if (sourceAspect > targetAspect) {
			int cropWidth = (int) Math.round(sourceHeight * targetAspect);
			int x = (sourceWidth - cropWidth) / 2;
			return new CropRect(x, 0, cropWidth, sourceHeight);
		}

		int cropHeight = (int) Math.round(sourceWidth / targetAspect);
		int y = (sourceHeight - cropHeight) / 2;
		return new CropRect(0, y, sourceWidth, cropHeight);
	}

	public static FrameRect viewfinderFor(int boxWidth, int boxHeight, int targetWidth, int targetHeight) {
		double boxAspect = boxWidth / (double) boxHeight;
		double targetAspect = targetWidth / (double) targetHeight;
		if (boxAspect > targetAspect) {
			int width = (int) Math.round(boxHeight * targetAspect);
			int x = (boxWidth - width) / 2;
			return new FrameRect(x, 0, width, boxHeight);
		}

		int height = (int) Math.round(boxWidth / targetAspect);
		int y = (boxHeight - height) / 2;
		return new FrameRect(0, y, boxWidth, height);
	}

	public record CropRect(int x, int y, int width, int height) {
	}

	public record FrameRect(int x, int y, int width, int height) {
	}
}
