package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

final class CameraModeSessionTest {
	@Test
	void defaultsToLivePreviewWithNoSelectedReviewMedia() {
		CameraModeSession session = CameraModeSession.handheld();

		assertEquals(CameraPreviewMode.LIVE, session.previewMode());
		assertNull(session.selectedReviewMediaId());
	}

	@Test
	void cyclesForwardAcrossPresets() {
		CameraModeSession session = CameraModeSession.handheld();

		session = session.withPreviewMode(CameraPreviewMode.REVIEW);
		session = session.nextPreset();
		session = session.nextPreset();

		assertEquals("3:2", session.currentPreset().label());
		assertEquals(CameraPreviewMode.REVIEW, session.previewMode());
	}

	@Test
	void updatesPreviewModeAndSelectedReviewMediaIndependently() {
		CameraModeSession session = CameraModeSession.tripod();

		session = session.withPreviewMode(CameraPreviewMode.REVIEW);
		session = session.withSelectedReviewMediaId("media-003");

		assertEquals("tripod", session.source());
		assertEquals(CameraPreviewMode.REVIEW, session.previewMode());
		assertEquals("media-003", session.selectedReviewMediaId());
	}
}
