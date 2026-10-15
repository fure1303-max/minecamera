package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class CameraHudCornerGeometryTest {
	@Test
	void topRightCornerMirrorsAwayFromAnchor() {
		CameraHudCornerGeometry corner = CameraHudCornerGeometry.topRight(100, 20, 10, 2);

		assertEquals(new CameraHudCornerGeometry.Rect(92, 20, 102, 22), corner.horizontal());
		assertEquals(new CameraHudCornerGeometry.Rect(100, 20, 102, 30), corner.vertical());
	}

	@Test
	void bottomLeftCornerExtendsUpwardFromAnchor() {
		CameraHudCornerGeometry corner = CameraHudCornerGeometry.bottomLeft(40, 80, 10, 2);

		assertEquals(new CameraHudCornerGeometry.Rect(40, 80, 50, 82), corner.horizontal());
		assertEquals(new CameraHudCornerGeometry.Rect(40, 72, 42, 82), corner.vertical());
	}
}
