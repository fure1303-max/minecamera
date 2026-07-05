package com.minecamera.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.minecamera.film.CaptureSettings;
import com.minecamera.film.FilmContentType;
import com.minecamera.film.FilmRecord;
import java.util.List;
import org.junit.jupiter.api.Test;

final class CameraReviewSelectionPolicyTest {
	@Test
	void prefersLatestCapturedMediaWhenRequestedAndPresent() {
		assertEquals(
			"media-003",
			CameraReviewSelectionPolicy.resolveSelectedMediaId(
				orderedFilms(),
				"media-003",
				"media-002",
				true
			)
		);
	}

	@Test
	void fallsBackToCurrentSelectionWhenLatestCaptureIsMissing() {
		assertEquals(
			"media-002",
			CameraReviewSelectionPolicy.resolveSelectedMediaId(
				orderedFilms(),
				"media-999",
				"media-002",
				true
			)
		);
	}

	@Test
	void fallsBackToNewestFilmWhenNeitherLatestNorCurrentSelectionExist() {
		assertEquals(
			"media-003",
			CameraReviewSelectionPolicy.resolveSelectedMediaId(
				orderedFilms(),
				"media-999",
				"media-888",
				true
			)
		);
	}

	@Test
	void returnsNullForEmptyFilmList() {
		assertNull(CameraReviewSelectionPolicy.resolveSelectedMediaId(List.of(), "media-001", "media-001", true));
	}

	private static List<FilmRecord> orderedFilms() {
		return CameraReviewStrip.orderedExposedFilms(List.of(
			exposed("media-001", 100L),
			exposed("media-003", 300L),
			exposed("media-002", 200L)
		));
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
