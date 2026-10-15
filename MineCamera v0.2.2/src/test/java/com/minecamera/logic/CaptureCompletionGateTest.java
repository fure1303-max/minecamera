package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

final class CaptureCompletionGateTest {
	@Test
	void onlyRunsCompletionOnce() {
		CaptureCompletionGate gate = new CaptureCompletionGate();
		AtomicInteger counter = new AtomicInteger();

		gate.run(() -> counter.incrementAndGet());
		gate.run(() -> counter.incrementAndGet());

		assertEquals(1, counter.get());
	}
}
