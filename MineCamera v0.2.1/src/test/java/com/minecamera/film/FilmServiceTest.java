package com.minecamera.film;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.minecamera.logic.FocusMode;
import net.minecraft.nbt.NbtCompound;
import org.junit.jupiter.api.Test;

final class FilmServiceTest {
	@Test
	void readsBlankFilmFromMissingCustomData() {
		assertEquals(FilmRecord.blank(), FilmService.readFromCustomData(null));
	}

	@Test
	void roundTripsExposedFilmMetadata() {
		FilmRecord record = exposedRecord("media-001");
		NbtCompound root = FilmService.writeToCustomData(null, record);

		assertEquals(record, FilmService.readFromCustomData(root));
	}

	@Test
	void preservesForeignCustomDataWhenWritingFilmMetadata() {
		NbtCompound root = new NbtCompound();
		root.putString("other_mod", "keep");
		NbtCompound merged = FilmService.writeToCustomData(root, exposedRecord("media-002"));

		assertTrue(merged.contains("other_mod"));
		assertEquals("keep", merged.getString("other_mod", ""));
		assertTrue(merged.contains("minecamera_film"));
	}

	@Test
	void removesMinecameraDataWhenReturningToBlank() {
		NbtCompound root = FilmService.writeToCustomData(null, exposedRecord("media-003"));
		NbtCompound cleared = FilmService.writeToCustomData(root, FilmRecord.blank());

		assertNull(cleared);
	}

	@Test
	void stackedBlankFilmNeedsSeparateResultSlot() {
		assertTrue(FilmService.requiresSeparateResultStack(3));
		assertTrue(!FilmService.requiresSeparateResultStack(1));
	}

	private static FilmRecord exposedRecord(String mediaId) {
		return FilmRecord.exposed(
			FilmContentType.PHOTO,
			mediaId,
			new CaptureSettings(2880, 2880, 50, 2.8D, FocusMode.AUTO),
			"tester",
			1_728_000_000L,
			"handheld"
		);
	}
}
