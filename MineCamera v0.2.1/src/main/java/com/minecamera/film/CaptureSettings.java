package com.minecamera.film;

import com.minecamera.logic.FocusMode;

public record CaptureSettings(
	int width,
	int height,
	int focalLength,
	double aperture,
	FocusMode focusMode
) {
}
