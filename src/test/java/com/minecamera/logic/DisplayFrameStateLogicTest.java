package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.minecamera.display.DisplayFrameState;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

final class DisplayFrameStateLogicTest {
	@Test
	void appliesPhotoToEveryCellInTheContainingRectangle() {
		Set<GridPoint> occupied = Set.of(
			new GridPoint(0, 0),
			new GridPoint(1, 0),
			new GridPoint(0, 1),
			new GridPoint(1, 1)
		);

		Map<GridPoint, DisplayFrameState> applied = DisplayFrameStateLogic.applyPhoto(
			occupied,
			Map.of(),
			new GridPoint(1, 1),
			"media-001"
		);

		assertEquals(
			Map.of(
				new GridPoint(0, 0), new DisplayFrameState("media-001", new GridPoint(0, 0), 2, 2, 0, 0),
				new GridPoint(1, 0), new DisplayFrameState("media-001", new GridPoint(0, 0), 2, 2, 1, 0),
				new GridPoint(0, 1), new DisplayFrameState("media-001", new GridPoint(0, 0), 2, 2, 0, 1),
				new GridPoint(1, 1), new DisplayFrameState("media-001", new GridPoint(0, 0), 2, 2, 1, 1)
			),
			applied
		);
	}

	@Test
	void onlyFillsTheRectangleContainingTheClickedCell() {
		Set<GridPoint> occupied = Set.of(
			new GridPoint(0, 0),
			new GridPoint(1, 0),
			new GridPoint(0, 1)
		);

		Map<GridPoint, DisplayFrameState> applied = DisplayFrameStateLogic.applyPhoto(
			occupied,
			Map.of(new GridPoint(0, 1), new DisplayFrameState("other-media", new GridPoint(0, 1), 1, 1, 0, 0)),
			new GridPoint(1, 0),
			"media-002"
		);

		assertEquals(
			Map.of(
				new GridPoint(0, 0), new DisplayFrameState("media-002", new GridPoint(0, 0), 2, 1, 0, 0),
				new GridPoint(1, 0), new DisplayFrameState("media-002", new GridPoint(0, 0), 2, 1, 1, 0),
				new GridPoint(0, 1), new DisplayFrameState("other-media", new GridPoint(0, 1), 1, 1, 0, 0)
			),
			applied
		);
	}

	@Test
	void expandsAnExistingPhotoWhenAFrameIsAdded() {
		Map<GridPoint, DisplayFrameState> expanded = DisplayFrameStateLogic.repartition(
			Set.of(new GridPoint(0, 0), new GridPoint(1, 0)),
			Map.of(new GridPoint(0, 0), new DisplayFrameState("media-added", new GridPoint(0, 0), 1, 1, 0, 0))
		);

		assertEquals(
			Map.of(
				new GridPoint(0, 0), new DisplayFrameState("media-added", new GridPoint(0, 0), 2, 1, 0, 0),
				new GridPoint(1, 0), new DisplayFrameState("media-added", new GridPoint(0, 0), 2, 1, 1, 0)
			),
			expanded
		);
	}

	@Test
	void repartitionsRemainingCellsAndPreservesThePhotoReference() {
		Map<GridPoint, DisplayFrameState> original = Map.of(
			new GridPoint(0, 0), new DisplayFrameState("media-003", new GridPoint(0, 0), 2, 2, 0, 0),
			new GridPoint(1, 0), new DisplayFrameState("media-003", new GridPoint(0, 0), 2, 2, 1, 0),
			new GridPoint(0, 1), new DisplayFrameState("media-003", new GridPoint(0, 0), 2, 2, 0, 1),
			new GridPoint(1, 1), new DisplayFrameState("media-003", new GridPoint(0, 0), 2, 2, 1, 1)
		);

		Map<GridPoint, DisplayFrameState> repartitioned = DisplayFrameStateLogic.repartition(
			Set.of(
				new GridPoint(0, 0),
				new GridPoint(1, 0),
				new GridPoint(0, 1)
			),
			original
		);

		assertEquals(
			Map.of(
				new GridPoint(0, 0), new DisplayFrameState("media-003", new GridPoint(0, 0), 2, 1, 0, 0),
				new GridPoint(1, 0), new DisplayFrameState("media-003", new GridPoint(0, 0), 2, 1, 1, 0)
			),
			repartitioned
		);
	}
	@Test
	void preservesLargestRectangleAndLeavesSingleAddedFrameBlank() {
		Map<GridPoint, DisplayFrameState> original = twoByTwoPhoto("media-largest");

		Map<GridPoint, DisplayFrameState> repartitioned = DisplayFrameStateLogic.repartition(
			Set.of(
				new GridPoint(0, 0),
				new GridPoint(1, 0),
				new GridPoint(2, 0),
				new GridPoint(0, 1),
				new GridPoint(1, 1)
			),
			original
		);

		assertEquals(original, repartitioned);
	}

	@Test
	void expandsWhenAddedFramesCompleteALargerRectangle() {
		Map<GridPoint, DisplayFrameState> repartitioned = DisplayFrameStateLogic.repartition(
			Set.of(
				new GridPoint(0, 0),
				new GridPoint(1, 0),
				new GridPoint(0, 1),
				new GridPoint(1, 1),
				new GridPoint(0, 2),
				new GridPoint(1, 2)
			),
			twoByTwoPhoto("media-expanded")
		);

		assertEquals(6, repartitioned.size());
		assertEquals(new DisplayFrameState("media-expanded", new GridPoint(0, 0), 2, 3, 0, 0), repartitioned.get(new GridPoint(0, 0)));
		assertEquals(new DisplayFrameState("media-expanded", new GridPoint(0, 0), 2, 3, 1, 2), repartitioned.get(new GridPoint(1, 2)));
	}

	private static Map<GridPoint, DisplayFrameState> twoByTwoPhoto(String mediaId) {
		return Map.of(
			new GridPoint(0, 0), new DisplayFrameState(mediaId, new GridPoint(0, 0), 2, 2, 0, 0),
			new GridPoint(1, 0), new DisplayFrameState(mediaId, new GridPoint(0, 0), 2, 2, 1, 0),
			new GridPoint(0, 1), new DisplayFrameState(mediaId, new GridPoint(0, 0), 2, 2, 0, 1),
			new GridPoint(1, 1), new DisplayFrameState(mediaId, new GridPoint(0, 0), 2, 2, 1, 1)
		);
	}

	@Test
	void removesEntirePhotoGroupWhenOneCellIsRemoved() {
		Map<GridPoint, DisplayFrameState> original = twoByTwoPhoto("media-remove");
		Map<GridPoint, DisplayFrameState> result = DisplayFrameStateLogic.removePhoto(original, new GridPoint(1, 1));

		assertTrue(result.isEmpty());
	}

	@Test
	void leavesOtherPhotoGroupsUntouchedWhenOneGroupIsRemoved() {
		Map<GridPoint, DisplayFrameState> current = new java.util.HashMap<>(twoByTwoPhoto("media-group1"));
		current.put(new GridPoint(3, 3), new DisplayFrameState("media-group2", new GridPoint(3, 3), 1, 1, 0, 0));

		Map<GridPoint, DisplayFrameState> result = DisplayFrameStateLogic.removePhoto(current, new GridPoint(0, 1));

		assertEquals(1, result.size());
		assertEquals(new DisplayFrameState("media-group2", new GridPoint(3, 3), 1, 1, 0, 0), result.get(new GridPoint(3, 3)));
	}

	@Test
	void returnsUnchangedMapWhenRemovingFromEmptyOrMissingCell() {
		Map<GridPoint, DisplayFrameState> original = twoByTwoPhoto("media-keep");
		Map<GridPoint, DisplayFrameState> result = DisplayFrameStateLogic.removePhoto(original, new GridPoint(5, 5));

		assertEquals(original, result);
	}
}
