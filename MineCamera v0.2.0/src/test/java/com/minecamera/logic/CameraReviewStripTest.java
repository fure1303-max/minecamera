package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.minecamera.film.CaptureSettings;
import com.minecamera.film.FilmContentType;
import com.minecamera.film.FilmRecord;
import java.util.List;
import org.junit.jupiter.api.Test;

final class CameraReviewStripTest {
	@Test
	void ordersExposedFilmsNewestFirstAndThenByMediaId() {
		List<FilmRecord> ordered = CameraReviewStrip.orderedExposedFilms(List.of(
			exposed("media-002", 100L),
			exposed("media-001", 100L),
			exposed("media-003", 200L)
		));

		assertEquals(
			List.of("media-003", "media-001", "media-002"),
			ordered.stream().map(FilmRecord::mediaId).toList()
		);
	}

	@Test
	void defaultsSelectionToLatestFilm() {
		String selected = CameraReviewStrip.defaultSelectedMediaId(List.of(
			exposed("media-001", 100L),
			exposed("media-003", 300L),
			exposed("media-002", 200L)
		));

		assertEquals("media-003", selected);
	}

	@Test
	void cyclesSelectionAcrossOrderedFilms() {
		List<FilmRecord> ordered = CameraReviewStrip.orderedExposedFilms(List.of(
			exposed("media-001", 100L),
			exposed("media-003", 300L),
			exposed("media-002", 200L)
		));

		assertEquals("media-002", CameraReviewStrip.stepSelectedMediaId(ordered, "media-003", 1));
		assertEquals("media-001", CameraReviewStrip.stepSelectedMediaId(ordered, "media-003", -1));
	}

	@Test
	void emptyStripHasNoSelection() {
		assertNull(CameraReviewStrip.defaultSelectedMediaId(List.of()));
		assertNull(CameraReviewStrip.stepSelectedMediaId(List.of(), "media-001", 1));
	}

	@Test
	void reportsWhetherMediaExistsInStrip() {
		List<FilmRecord> ordered = CameraReviewStrip.orderedExposedFilms(List.of(
			exposed("media-001", 100L),
			exposed("media-003", 300L),
			exposed("media-002", 200L)
		));

		assertEquals(true, CameraReviewStrip.containsMediaId(ordered, "media-002"));
		assertEquals(false, CameraReviewStrip.containsMediaId(ordered, "media-999"));
	}

	private static FilmRecord exposed(String mediaId, long capturedAt) {
		return FilmRecord.exposed(
			FilmContentType.PHOTO,
			mediaId,
			new CaptureSettings(100, 100, 50, 2.8D, FocusMode.AUTO),
			"tester",
			capturedAt,
			"handheld"
		);
	}
}
