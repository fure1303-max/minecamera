package com.minecamera.client.capture;

final class CaptureCommitSession {
	private final long timeoutMillis;
	private String mediaId;
	private long expiresAtMillis;

	CaptureCommitSession(long timeoutMillis) {
		if (timeoutMillis <= 0L) {
			throw new IllegalArgumentException("Capture commit timeout must be positive");
		}
		this.timeoutMillis = timeoutMillis;
	}

	void begin(String mediaId, long nowMillis) {
		this.mediaId = mediaId;
		this.expiresAtMillis = nowMillis + timeoutMillis;
	}

	boolean matches(String mediaId) {
		return this.mediaId != null && this.mediaId.equals(mediaId);
	}

	boolean expire(long nowMillis) {
		if (mediaId == null || nowMillis < expiresAtMillis) {
			return false;
		}
		reset();
		return true;
	}

	void reset() {
		mediaId = null;
		expiresAtMillis = 0L;
	}
}
