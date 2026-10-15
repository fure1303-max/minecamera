package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class ReviewTextureDrawPlanTest {
	@Test
	void usesFullTextureRegionEvenWhenPreviewBoxIsSmallerThanImage() {
		ReviewTextureDrawPlan plan = ReviewTextureDrawPlan.fitInto(100, 50, 400, 200);

		assertEquals(100, plan.drawWidth());
		assertEquals(50, plan.drawHeight());
		assertEquals(400, plan.regionWidth());
		assertEquals(200, plan.regionHeight());
		assertEquals(400, plan.textureWidth());
		assertEquals(200, plan.textureHeight());
	}

	@Test
	void centersScaledImageWithinInsetBox() {
		ReviewTextureDrawPlan plan = ReviewTextureDrawPlan.create(10, 20, 200, 200, 400, 200);

		assertEquals(10, plan.drawX());
		assertEquals(70, plan.drawY());
		assertEquals(200, plan.drawWidth());
		assertEquals(100, plan.drawHeight());
	}
}
