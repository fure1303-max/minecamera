package com.minecamera.client.capture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CaptureCommitSessionTest {
	@Test
	void expiresOnlyAfterDeadlineAndClearsPendingMedia() {
		CaptureCommitSession session = new CaptureCommitSession(5_000L);
		session.begin("media-001", 10_000L);

		assertTrue(session.matches("media-001"));
		assertFalse(session.expire(14_999L));
		assertTrue(session.expire(15_000L));
		assertFalse(session.matches("media-001"));
	}

	@Test
	void resetClearsPendingMediaWithoutWaiting() {
		CaptureCommitSession session = new CaptureCommitSession(5_000L);
		session.begin("media-002", 10_000L);

		session.reset();

		assertFalse(session.matches("media-002"));
		assertFalse(session.expire(20_000L));
	}
}
