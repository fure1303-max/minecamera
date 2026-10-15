package com.minecamera.display;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.minecamera.logic.GridPoint;
import org.junit.jupiter.api.Test;

final class DisplayFrameRenderGeometryTest {
	@Test
	void leavesOneSixteenthBorderAroundSingleFrame() {
		DisplayFrameRenderGeometry geometry = DisplayFrameRenderGeometry.forState(
			new DisplayFrameState("media-single", new GridPoint(0, 0), 1, 1, 0, 0)
		);

		assertEquals(1.0F / 16.0F, geometry.leftInset());
		assertEquals(1.0F / 16.0F, geometry.topInset());
		assertEquals(1.0F / 16.0F, geometry.rightInset());
		assertEquals(1.0F / 16.0F, geometry.bottomInset());
		assertEquals(0.875F, geometry.width());
		assertEquals(0.875F, geometry.height());
	}

	@Test
	void removesInternalBordersInsidePhotoGroup() {
		DisplayFrameRenderGeometry topLeft = DisplayFrameRenderGeometry.forState(
			new DisplayFrameState("media-group", new GridPoint(0, 0), 2, 2, 0, 0)
		);
		DisplayFrameRenderGeometry bottomRight = DisplayFrameRenderGeometry.forState(
			new DisplayFrameState("media-group", new GridPoint(0, 0), 2, 2, 1, 1)
		);

		assertEquals(1.0F / 16.0F, topLeft.leftInset());
		assertEquals(1.0F / 16.0F, topLeft.topInset());
		assertEquals(0.0F, topLeft.rightInset());
		assertEquals(0.0F, topLeft.bottomInset());
		assertEquals(0.0F, bottomRight.leftInset());
		assertEquals(0.0F, bottomRight.topInset());
		assertEquals(1.0F / 16.0F, bottomRight.rightInset());
		assertEquals(1.0F / 16.0F, bottomRight.bottomInset());
	}

	@Test
	void rejectsInsetsThatCollapseThePhotoPlane() {
		assertThrows(IllegalArgumentException.class, () -> DisplayFrameRenderGeometry.fromInset(0.5F));
	}
}
