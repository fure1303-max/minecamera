package com.minecamera.logic;

import java.util.concurrent.atomic.AtomicBoolean;

public final class CaptureCompletionGate {
	private final AtomicBoolean completed = new AtomicBoolean();

	public void run(Runnable action) {
		if (completed.compareAndSet(false, true)) {
			action.run();
		}
	}
}
