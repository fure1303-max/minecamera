package com.minecamera.display;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.minecamera.logic.GridPoint;
import net.minecraft.nbt.NbtCompound;
import org.junit.jupiter.api.Test;

final class DisplayFrameStateCodecTest {
	@Test
	void roundTripsDisplayFrameState() {
		DisplayFrameState state = new DisplayFrameState("media-100", new GridPoint(4, 7), 3, 2, 1, 0);

		NbtCompound root = DisplayFrameStateCodec.writeToCustomData(null, state);

		assertEquals(state, DisplayFrameStateCodec.readFromCustomData(root));
	}

	@Test
	void preservesForeignDataWhenWritingDisplayFrameState() {
		NbtCompound root = new NbtCompound();
		root.putString("other_mod", "keep");

		NbtCompound merged = DisplayFrameStateCodec.writeToCustomData(
			root,
			new DisplayFrameState("media-200", new GridPoint(0, 0), 1, 1, 0, 0)
		);

		assertTrue(merged.contains("other_mod"));
		assertEquals("keep", merged.getString("other_mod", ""));
		assertEquals(
			new DisplayFrameState("media-200", new GridPoint(0, 0), 1, 1, 0, 0),
			DisplayFrameStateCodec.readFromCustomData(merged)
		);
	}

	@Test
	void clearsMinecameraStateWhenFrameBecomesEmpty() {
		NbtCompound root = DisplayFrameStateCodec.writeToCustomData(
			null,
			new DisplayFrameState("media-300", new GridPoint(1, 2), 2, 2, 1, 1)
		);

		assertEquals(DisplayFrameState.empty(), DisplayFrameStateCodec.readFromCustomData(
			DisplayFrameStateCodec.writeToCustomData(root, DisplayFrameState.empty())
		));
	}
}
