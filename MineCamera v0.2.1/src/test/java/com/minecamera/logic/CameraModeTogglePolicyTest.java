package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CameraModeTogglePolicyTest {
	@Test
	void handheldRequestTogglesToCloseWhenAlreadyOpen() {
		assertEquals(CameraModeTogglePolicy.Action.CLOSE, CameraModeTogglePolicy.decide(false, true, false));
	}

	@Test
	void handheldRequestOpensWhenInactive() {
		assertEquals(CameraModeTogglePolicy.Action.OPEN, CameraModeTogglePolicy.decide(false, false, false));
	}

	@Test
	void tripodRequestTogglesToCloseWhenAlreadyInTripodMode() {
		assertEquals(CameraModeTogglePolicy.Action.CLOSE, CameraModeTogglePolicy.decide(true, true, true));
	}
}
