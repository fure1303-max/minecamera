package com.minecamera.logic;

import com.minecamera.display.DisplayFrameState;
import java.util.HashMap;
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
		List<DisplayGroupLayout> layouts = DisplayGroupResolver.partition(occupied);
		for (DisplayGroupLayout layout : layouts) {
			String mediaId = firstMediaIdForLayout(layout, previous);
			if (mediaId == null || mediaId.isBlank()) {
				continue;
			}
			applyLayout(updated, layout, mediaId);
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

	private static String firstMediaIdForLayout(DisplayGroupLayout layout, Map<GridPoint, DisplayFrameState> previous) {
		for (int dy = 0; dy < layout.height(); dy++) {
			for (int dx = 0; dx < layout.width(); dx++) {
				GridPoint point = new GridPoint(layout.anchor().x() + dx, layout.anchor().y() + dy);
				DisplayFrameState state = previous.get(point);
				if (state != null && !state.isEmpty()) {
					return state.mediaId();
				}
			}
		}
		return null;
	}
}
