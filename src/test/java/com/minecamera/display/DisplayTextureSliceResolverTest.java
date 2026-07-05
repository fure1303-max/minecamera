package com.minecamera.display;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.minecamera.logic.GridPoint;
import org.junit.jupiter.api.Test;

final class DisplayTextureSliceResolverTest {
	@Test
	void resolvesFullFrameToWholeTexture() {
		DisplayTextureSlice slice = DisplayTextureSliceResolver.resolve(
			new DisplayFrameState("media-001", new GridPoint(0, 0), 1, 1, 0, 0)
		);

		assertEquals(new DisplayTextureSlice(0.0F, 0.0F, 1.0F, 1.0F), slice);
	}

	@Test
	void resolvesRightHalfOfTwoWideGroup() {
		DisplayTextureSlice slice = DisplayTextureSliceResolver.resolve(
			new DisplayFrameState("media-002", new GridPoint(0, 0), 2, 1, 1, 0)
		);

		assertEquals(new DisplayTextureSlice(0.5F, 0.0F, 1.0F, 1.0F), slice);
	}

	@Test
	void resolvesBottomRightQuarterOfTwoByTwoGroup() {
		DisplayTextureSlice slice = DisplayTextureSliceResolver.resolve(
			new DisplayFrameState("media-003", new GridPoint(0, 0), 2, 2, 1, 1)
		);

		assertEquals(new DisplayTextureSlice(0.5F, 0.5F, 1.0F, 1.0F), slice);
	}
}
