package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class FocusMathTest {
	@Test
	void blurFactorIsZeroAtTheFocusDistance() {
		double blur = FocusMath.computeBlurFactor(12.0, 12.0, 1.4, 85.0);

		assertEquals(0.0, blur, 1.0E-9);
	}

	@Test
	void blurFactorIncreasesWhenDistanceMovesAwayFromFocusPlane() {
		double near = FocusMath.computeBlurFactor(11.0, 12.0, 1.4, 85.0);
		double far = FocusMath.computeBlurFactor(48.0, 12.0, 1.4, 85.0);

		assertTrue(far > near);
	}

	@Test
	void blurFactorRespondsToWiderApertures() {
		double narrow = FocusMath.computeBlurFactor(24.0, 12.0, 8.0, 50.0);
		double wide = FocusMath.computeBlurFactor(24.0, 12.0, 1.4, 50.0);

		assertTrue(wide > narrow);
	}
}

