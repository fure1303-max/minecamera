package com.minecamera.client.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.minecamera.logic.CameraPreviewMode;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

final class CameraModeLifecycleTest {
	@Test
	void handheldOpenStartsLiveSessionAndClearsTripodState() {
		CameraModeRuntimeState state = CameraModeLifecycle.openHandheld(
			Hand.OFF_HAND,
			Text.literal("ready"),
			CameraReviewState.empty(Text.literal("empty")),
			8.0D
		);

		assertTrue(state.active());
		assertEquals(Hand.OFF_HAND, state.activeHand());
		assertEquals("handheld", state.session().source());
		assertEquals(CameraPreviewMode.LIVE, state.session().previewMode());
		assertNull(state.tripodPos());
		assertNull(state.latestCapturedMediaId());
		assertFalse(state.previewFailureActive());
	}

	@Test
	void tripodOpenPreservesTripodPositionAndUsesTripodSession() {
		BlockPos tripodPos = new BlockPos(1, 64, 2);

		CameraModeRuntimeState state = CameraModeLifecycle.openTripod(
			Hand.MAIN_HAND,
			tripodPos,
			Text.literal("ready"),
			CameraReviewState.empty(Text.literal("empty")),
			8.0D
		);

		assertTrue(state.active());
		assertEquals("tripod", state.session().source());
		assertEquals(tripodPos, state.tripodPos());
	}

	@Test
	void closeReturnsInactiveDefaultState() {
		CameraModeRuntimeState state = CameraModeLifecycle.closed(
			Text.literal("idle"),
			CameraReviewState.empty(Text.literal("empty")),
			8.0D
		);

		assertFalse(state.active());
		assertEquals(Hand.MAIN_HAND, state.activeHand());
		assertEquals("handheld", state.session().source());
		assertNull(state.tripodPos());
		assertEquals("idle", state.status().getString());
		assertFalse(state.previewFailureActive());
	}
}
