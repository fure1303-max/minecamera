package com.minecamera.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class DisplayGroupResolver {
	private DisplayGroupResolver() {
	}

	public static List<DisplayGroupLayout> partition(Set<GridPoint> occupied) {
		List<GridPoint> ordered = occupied.stream()
			.sorted(Comparator.comparingInt(GridPoint::y).thenComparingInt(GridPoint::x))
			.toList();
		Set<GridPoint> remaining = new HashSet<>(occupied);
		List<DisplayGroupLayout> layouts = new ArrayList<>();

		for (GridPoint start : ordered) {
			if (!remaining.contains(start)) {
				continue;
			}

			int width = 1;
			while (remaining.contains(new GridPoint(start.x() + width, start.y()))) {
				width++;
			}

			int height = 1;
			boolean canGrow = true;
			while (canGrow) {
				for (int dx = 0; dx < width; dx++) {
					if (!remaining.contains(new GridPoint(start.x() + dx, start.y() + height))) {
						canGrow = false;
						break;
					}
				}
				if (canGrow) {
					height++;
				}
			}

			DisplayGroupLayout layout = new DisplayGroupLayout(start, width, height);
			layouts.add(layout);
			for (int dy = 0; dy < height; dy++) {
				for (int dx = 0; dx < width; dx++) {
					remaining.remove(new GridPoint(start.x() + dx, start.y() + dy));
				}
			}
		}

		return layouts;
	}

	public static Optional<DisplayGroupLayout> findContainingLayout(Set<GridPoint> occupied, GridPoint clicked) {
		return partition(occupied).stream().filter(layout -> layout.contains(clicked)).findFirst();
	}
}
