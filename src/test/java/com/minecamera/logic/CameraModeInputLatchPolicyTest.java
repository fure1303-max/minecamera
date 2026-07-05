package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class CameraModeInputLatchPolicyTest {
	@Test
	void openingWithRightClickStartsCloseLatchAsPressed() {
		assertTrue(CameraModeInputLatchPolicy.closeLatchOnOpen(true));
		assertFalse(CameraModeInputLatchPolicy.closeLatchOnOpen(false));
	}

	@Test
	void heldOpeningRightClickDoesNotTriggerClose() {
		assertFalse(CameraModeInputLatchPolicy.shouldTriggerClose(true, true));
	}

	@Test
	void secondRightClickAfterReleaseDoesTriggerClose() {
		assertTrue(CameraModeInputLatchPolicy.shouldTriggerClose(false, true));
	}
}
