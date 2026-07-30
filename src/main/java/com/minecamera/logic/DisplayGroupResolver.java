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
		Set<GridPoint> remaining = new HashSet<>(occupied);
		List<DisplayGroupLayout> layouts = new ArrayList<>();

		while (!remaining.isEmpty()) {
			DisplayGroupLayout completeRectangle = completeBoundingRectangle(remaining);
			if (completeRectangle != null) {
				layouts.add(completeRectangle);
				break;
			}
			DisplayGroupLayout layout = largestRectangle(remaining);
			layouts.add(layout);
			for (int dy = 0; dy < layout.height(); dy++) {
				for (int dx = 0; dx < layout.width(); dx++) {
					remaining.remove(new GridPoint(layout.anchor().x() + dx, layout.anchor().y() + dy));
				}
			}
		}

		return layouts;
	}

	private static DisplayGroupLayout completeBoundingRectangle(Set<GridPoint> occupied) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (GridPoint point : occupied) {
			minX = Math.min(minX, point.x());
			minY = Math.min(minY, point.y());
			maxX = Math.max(maxX, point.x());
			maxY = Math.max(maxY, point.y());
		}

		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		return (long) width * height == occupied.size()
			? new DisplayGroupLayout(new GridPoint(minX, minY), width, height)
			: null;
	}

	public static Optional<DisplayGroupLayout> findContainingLayout(Set<GridPoint> occupied, GridPoint clicked) {
		return partition(occupied).stream().filter(layout -> layout.contains(clicked)).findFirst();
	}

	private static DisplayGroupLayout largestRectangle(Set<GridPoint> occupied) {
		return occupied.stream()
			.flatMap(anchor -> rectanglesFrom(anchor, occupied).stream())
			.min(
				Comparator.comparingInt(DisplayGroupResolver::area).reversed()
					.thenComparing(Comparator.comparingInt(DisplayGroupLayout::width).reversed())
					.thenComparingInt(layout -> layout.anchor().y())
					.thenComparingInt(layout -> layout.anchor().x())
			)
			.orElseThrow();
	}

	private static List<DisplayGroupLayout> rectanglesFrom(GridPoint anchor, Set<GridPoint> occupied) {
		List<DisplayGroupLayout> rectangles = new ArrayList<>();
		int height = 0;
		int width = Integer.MAX_VALUE;

		while (occupied.contains(new GridPoint(anchor.x(), anchor.y() + height))) {
			int rowWidth = 0;
			while (occupied.contains(new GridPoint(anchor.x() + rowWidth, anchor.y() + height))) {
				rowWidth++;
			}
			width = Math.min(width, rowWidth);
			height++;
			rectangles.add(new DisplayGroupLayout(anchor, width, height));
		}

		return rectangles;
	}

	private static int area(DisplayGroupLayout layout) {
		return layout.width() * layout.height();
	}
}
