package com.minecamera.logic;

import com.minecamera.display.DisplayFrameState;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DisplayFrameStateLogic {
	private DisplayFrameStateLogic() {
	}

	public static Map<GridPoint, DisplayFrameState> applyPhoto(
		Set<GridPoint> occupied,
		Map<GridPoint, DisplayFrameState> current,
		GridPoint clicked,
		String mediaId
	) {
		DisplayGroupLayout layout = DisplayGroupResolver.findContainingLayout(occupied, clicked).orElseThrow(
			() -> new IllegalArgumentException("Clicked cell must belong to an occupied display group")
		);

		Map<GridPoint, DisplayFrameState> updated = new HashMap<>(current);
		applyLayout(updated, layout, mediaId);
		return updated;
	}

	public static Map<GridPoint, DisplayFrameState> repartition(
		Set<GridPoint> occupied,
		Map<GridPoint, DisplayFrameState> previous
	) {
		Map<GridPoint, DisplayFrameState> updated = new HashMap<>();
		List<PreviousGroup> previousGroups = collectPreviousGroups(previous);
		Set<PreviousGroupKey> assignedGroups = new HashSet<>();

		for (DisplayGroupLayout layout : DisplayGroupResolver.partition(occupied)) {
			PreviousGroup inherited = bestPreviousGroup(layout, previousGroups, assignedGroups);
			if (inherited == null) {
				continue;
			}
			assignedGroups.add(inherited.key());
			applyLayout(updated, layout, inherited.key().mediaId());
		}
		return updated;
	}

	private static void applyLayout(Map<GridPoint, DisplayFrameState> target, DisplayGroupLayout layout, String mediaId) {
		for (int dy = 0; dy < layout.height(); dy++) {
			for (int dx = 0; dx < layout.width(); dx++) {
				GridPoint point = new GridPoint(layout.anchor().x() + dx, layout.anchor().y() + dy);
				target.put(point, new DisplayFrameState(mediaId, layout.anchor(), layout.width(), layout.height(), dx, dy));
			}
		}
	}

	private static List<PreviousGroup> collectPreviousGroups(Map<GridPoint, DisplayFrameState> previous) {
		Map<PreviousGroupKey, Set<GridPoint>> groupedPoints = new HashMap<>();
		for (Map.Entry<GridPoint, DisplayFrameState> entry : previous.entrySet()) {
			DisplayFrameState state = entry.getValue();
			if (state == null || state.isEmpty()) {
				continue;
			}
			PreviousGroupKey key = new PreviousGroupKey(
				state.mediaId(),
				state.groupAnchor(),
				state.groupWidth(),
				state.groupHeight()
			);
			groupedPoints.computeIfAbsent(key, ignored -> new HashSet<>()).add(entry.getKey());
		}

		List<PreviousGroup> groups = new ArrayList<>();
		for (Map.Entry<PreviousGroupKey, Set<GridPoint>> entry : groupedPoints.entrySet()) {
			groups.add(new PreviousGroup(entry.getKey(), Set.copyOf(entry.getValue())));
		}
		return groups;
	}

	private static PreviousGroup bestPreviousGroup(
		DisplayGroupLayout layout,
		List<PreviousGroup> previousGroups,
		Set<PreviousGroupKey> assignedGroups
	) {
		return previousGroups.stream()
			.filter(group -> !assignedGroups.contains(group.key()))
			.filter(group -> overlap(layout, group.points()) > 0)
			.min(
				Comparator.comparingInt((PreviousGroup group) -> overlap(layout, group.points())).reversed()
					.thenComparing(Comparator.comparingInt((PreviousGroup group) -> group.points().size()).reversed())
					.thenComparing(Comparator.comparingInt((PreviousGroup group) -> group.key().area()).reversed())
					.thenComparingInt(group -> group.key().anchor().y())
					.thenComparingInt(group -> group.key().anchor().x())
					.thenComparing(group -> group.key().mediaId())
			)
			.orElse(null);
	}

	private static int overlap(DisplayGroupLayout layout, Set<GridPoint> points) {
		int overlap = 0;
		for (GridPoint point : points) {
			if (layout.contains(point)) {
				overlap++;
			}
		}
		return overlap;
	}

	private record PreviousGroup(PreviousGroupKey key, Set<GridPoint> points) {
	}

	private record PreviousGroupKey(String mediaId, GridPoint anchor, int width, int height) {
		private int area() {
			return width * height;
		}
	}
}
