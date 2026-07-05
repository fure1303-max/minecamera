package com.minecamera.client.camera;

final class ViewfinderRefreshResult {
	private static final ViewfinderRefreshResult SKIPPED = new ViewfinderRefreshResult(false, false, null);
	private static final ViewfinderRefreshResult SUCCESS = new ViewfinderRefreshResult(true, true, null);

	private final boolean attempted;
	private final boolean succeeded;
	private final String error;

	private ViewfinderRefreshResult(boolean attempted, boolean succeeded, String error) {
		this.attempted = attempted;
		this.succeeded = succeeded;
		this.error = error;
	}

	static ViewfinderRefreshResult skipped() {
		return SKIPPED;
	}

	static ViewfinderRefreshResult success() {
		return SUCCESS;
	}

	static ViewfinderRefreshResult failed(String error) {
		return new ViewfinderRefreshResult(true, false, error);
	}

	boolean failed() {
		return attempted && !succeeded;
	}

	boolean succeeded() {
		return attempted && succeeded;
	}

	String error() {
		return error;
	}
}
