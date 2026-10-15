package com.minecamera.display;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.minecamera.logic.GridPoint;
import org.junit.jupiter.api.Test;

final class DisplayTextureSliceResolverTest {
	private static final float DELTA = 0.000001F;

	@Test
	void resolvesFullFrameToWholeTextureWhenAspectsMatch() {
		DisplayTextureSlice slice = DisplayTextureSliceResolver.resolve(
			new DisplayFrameState("media-001", new GridPoint(0, 0), 1, 1, 0, 0),
			100,
			100
		);

		assertSlice(slice, 0.0F, 0.0F, 1.0F, 1.0F);
	}

	@Test
	void resolvesDifferentSlicesForTheSameMediaId() {
		DisplayFrameState leftState = new DisplayFrameState("shared-media", new GridPoint(0, 0), 2, 1, 0, 0);
		DisplayFrameState rightState = new DisplayFrameState("shared-media", new GridPoint(0, 0), 2, 1, 1, 0);

		DisplayTextureSlice left = DisplayTextureSliceResolver.resolve(leftState, 1500, 700);
		DisplayTextureSlice right = DisplayTextureSliceResolver.resolve(rightState, 1500, 700);

		assertSlice(left, 0.0F, 0.0F, 0.5F, 1.0F);
		assertSlice(right, 0.5F, 0.0F, 1.0F, 1.0F);
	}

	@Test
	void resolvesBottomRightQuarterOfTwoByTwoGroup() {
		DisplayTextureSlice slice = DisplayTextureSliceResolver.resolve(
			new DisplayFrameState("media-003", new GridPoint(0, 0), 2, 2, 1, 1),
			100,
			100
		);

		assertSlice(slice, 0.5F, 0.5F, 1.0F, 1.0F);
	}

	@Test
	void centerCropsWidePhotoToFillSquareFrame() {
		DisplayTextureSlice slice = DisplayTextureSliceResolver.resolve(
			new DisplayFrameState("wide-media", new GridPoint(0, 0), 1, 1, 0, 0),
			200,
			100
		);

		assertSlice(slice, 0.25F, 0.0F, 0.75F, 1.0F);
	}

	@Test
	void centerCropsTallPhotoToFillSquareFrame() {
		DisplayTextureSlice slice = DisplayTextureSliceResolver.resolve(
			new DisplayFrameState("tall-media", new GridPoint(0, 0), 1, 1, 0, 0),
			100,
			200
		);

		assertSlice(slice, 0.0F, 0.25F, 1.0F, 0.75F);
	}

	@Test
	void accountsForOuterBorderWhenSlicingThreeWideGroup() {
		DisplayTextureSlice left = DisplayTextureSliceResolver.resolve(
			new DisplayFrameState("three-wide", new GridPoint(0, 0), 3, 1, 0, 0),
			2300,
			700
		);
		DisplayTextureSlice middle = DisplayTextureSliceResolver.resolve(
			new DisplayFrameState("three-wide", new GridPoint(0, 0), 3, 1, 1, 0),
			2300,
			700
		);

		assertSlice(left, 0.0F, 0.0F, 15.0F / 46.0F, 1.0F);
		assertSlice(middle, 15.0F / 46.0F, 0.0F, 31.0F / 46.0F, 1.0F);
	}

	private static void assertSlice(DisplayTextureSlice actual, float u0, float v0, float u1, float v1) {
		assertEquals(u0, actual.u0(), DELTA);
		assertEquals(v0, actual.v0(), DELTA);
		assertEquals(u1, actual.u1(), DELTA);
		assertEquals(v1, actual.v1(), DELTA);
	}
}
