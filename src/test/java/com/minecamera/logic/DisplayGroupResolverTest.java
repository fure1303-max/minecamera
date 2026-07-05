package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

final class DisplayGroupResolverTest {
	@Test
	void groupsAPerfectRectangleIntoOneRegion() {
		Set<GridPoint> occupied = Set.of(
			new GridPoint(0, 0),
			new GridPoint(1, 0),
			new GridPoint(0, 1),
			new GridPoint(1, 1)
		);

		List<DisplayGroupLayout> layouts = DisplayGroupResolver.partition(occupied);

		assertEquals(1, layouts.size());
		assertEquals(new DisplayGroupLayout(new GridPoint(0, 0), 2, 2), layouts.getFirst());
	}

	@Test
	void splitsAnLShapeIntoDeterministicRectangles() {
		Set<GridPoint> occupied = Set.of(
			new GridPoint(0, 0),
			new GridPoint(1, 0),
			new GridPoint(0, 1)
		);

		List<DisplayGroupLayout> layouts = DisplayGroupResolver.partition(occupied);

		assertEquals(
			List.of(
				new DisplayGroupLayout(new GridPoint(0, 0), 2, 1),
				new DisplayGroupLayout(new GridPoint(0, 1), 1, 1)
			),
			layouts
		);
	}

	@Test
	void findsTheLayoutContainingTheClickedCell() {
		Set<GridPoint> occupied = Set.of(
			new GridPoint(2, 4),
			new GridPoint(3, 4),
			new GridPoint(2, 5),
			new GridPoint(3, 5)
		);

		DisplayGroupLayout layout = DisplayGroupResolver.findContainingLayout(occupied, new GridPoint(3, 5)).orElseThrow();

		assertTrue(layout.contains(new GridPoint(2, 4)));
		assertEquals(2, layout.width());
		assertEquals(2, layout.height());
	}
}
