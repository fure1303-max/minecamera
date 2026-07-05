package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class ViewfinderFramebufferQuadPlanTest {
	@Test
	void expandsRequestedHudRectIntoOrderedCornerCoordinates() {
		ViewfinderFramebufferQuadPlan plan = ViewfinderFramebufferQuadPlan.forHudRect(120, 80, 640, 360);

		assertEquals(120, plan.x1());
		assertEquals(80, plan.y1());
		assertEquals(760, plan.x2());
		assertEquals(440, plan.y2());
	}

	@Test
	void samplesWholeFramebufferAndFlipsVertically() {
		ViewfinderFramebufferQuadPlan plan = ViewfinderFramebufferQuadPlan.forHudRect(0, 0, 512, 288);

		assertEquals(0.0F, plan.minU());
		assertEquals(1.0F, plan.maxU());
		assertEquals(1.0F, plan.minV());
		assertEquals(0.0F, plan.maxV());
	}
}
