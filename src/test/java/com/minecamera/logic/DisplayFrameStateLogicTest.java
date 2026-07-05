package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
				new GridPoint(1, 0), new DisplayFrameState("media-003", new GridPoint(0, 0), 2, 1, 1, 0),
				new GridPoint(0, 1), new DisplayFrameState("media-003", new GridPoint(0, 1), 1, 1, 0, 0)
			),
			repartitioned
		);
	}
}
