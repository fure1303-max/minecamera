package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class ViewfinderPreviewPlanTest {
	@Test
	void capsSquarePreviewByLongEdge() {
		ViewfinderPreviewPlan plan = ViewfinderPreviewPlan.fromTarget(2880, 2880);

		assertEquals(512, plan.width());
		assertEquals(512, plan.height());
	}

	@Test
	void preservesLandscapeAspectWhenDownscaling() {
		ViewfinderPreviewPlan plan = ViewfinderPreviewPlan.fromTarget(3840, 2160);

		assertEquals(512, plan.width());
		assertEquals(288, plan.height());
	}

	@Test
	void preservesPortraitAspectWhenDownscaling() {
		ViewfinderPreviewPlan plan = ViewfinderPreviewPlan.fromTarget(1080, 1920);

		assertEquals(288, plan.width());
		assertEquals(512, plan.height());
	}
}
