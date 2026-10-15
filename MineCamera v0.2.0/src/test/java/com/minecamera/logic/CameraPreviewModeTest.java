package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CameraPreviewModeTest {
	@Test
	void cyclesBetweenLiveAndReview() {
		assertEquals(CameraPreviewMode.REVIEW, CameraPreviewMode.LIVE.next());
		assertEquals(CameraPreviewMode.LIVE, CameraPreviewMode.REVIEW.next());
	}
}
