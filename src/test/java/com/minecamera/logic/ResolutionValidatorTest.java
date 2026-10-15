package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class ResolutionValidatorTest {
	@Test
	void acceptsTheHighestAllowedSquareResolution() {
		ResolutionValidationResult result = ResolutionValidator.validate(2880, 2880, 16384);

		assertTrue(result.valid());
		assertEquals(8_294_400, result.totalPixels());
	}

	@Test
	void rejectsCustomResolutionThatExceedsThePixelBudget() {
		ResolutionValidationResult result = ResolutionValidator.validate(2881, 2880, 16384);

		assertFalse(result.valid());
		assertEquals(ResolutionValidationError.TOO_MANY_PIXELS, result.error());
	}

	@Test
	void rejectsCustomResolutionThatExceedsTheSoftEdgeLimit() {
		ResolutionValidationResult result = ResolutionValidator.validate(4097, 1024, 16384);

		assertFalse(result.valid());
		assertEquals(ResolutionValidationError.EDGE_TOO_LARGE, result.error());
	}

	@Test
	void computesTheMaxPresetForOneToOneRatioFromThePixelBudget() {
		ResolutionPreset preset = ResolutionPresets.maxPreset("1:1", 1, 1);

		assertEquals(2880, preset.width());
		assertEquals(2880, preset.height());
	}

	@Test
	void computesAValidUltraWidePresetWithinTheEdgeLimit() {
		ResolutionPreset preset = ResolutionPresets.maxPreset("21:9", 21, 9);

		assertEquals(4096, preset.width());
		assertEquals(1755, preset.height());
		assertTrue(ResolutionValidator.validate(preset.width(), preset.height(), ResolutionValidator.SOFT_EDGE_LIMIT).valid());
	}

	@Test
	void computesStandardPresetsForCommonAspectRatios() {
		ResolutionPreset square = ResolutionPresets.standardPreset("1:1", 1, 1);
		assertEquals(1080, square.width());
		assertEquals(1080, square.height());
		assertTrue(ResolutionValidator.validate(square.width(), square.height(), 4096).valid());

		ResolutionPreset sixteenByNine = ResolutionPresets.standardPreset("16:9", 16, 9);
		assertEquals(1920, sixteenByNine.width());
		assertEquals(1080, sixteenByNine.height());
		assertTrue(ResolutionValidator.validate(sixteenByNine.width(), sixteenByNine.height(), 4096).valid());
	}
}
