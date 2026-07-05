package com.minecamera.logic;

public record CameraCaptureDefaults(int focalLengthMm, double apertureFStop, FocusMode focusMode) {
	private static final CameraCaptureDefaults BASIC_PHOTO = new CameraCaptureDefaults(20, 2.8D, FocusMode.AUTO);

	public static CameraCaptureDefaults basicPhoto() {
		return BASIC_PHOTO;
	}
}
