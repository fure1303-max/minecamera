package com.minecamera.logic;

import com.minecamera.film.FilmRecord;
import java.util.List;

public final class CameraReviewSelectionPolicy {
	private CameraReviewSelectionPolicy() {
	}

	public static String resolveSelectedMediaId(
		List<FilmRecord> orderedFilms,
		String latestCapturedMediaId,
		String currentSelectedMediaId,
		boolean preferLatestCapture
	) {
		if (orderedFilms.isEmpty()) {
			return null;
		}
		if (preferLatestCapture && CameraReviewStrip.containsMediaId(orderedFilms, latestCapturedMediaId)) {
			return latestCapturedMediaId;
		}
		if (CameraReviewStrip.containsMediaId(orderedFilms, currentSelectedMediaId)) {
			return currentSelectedMediaId;
		}
		return CameraReviewStrip.defaultSelectedMediaId(orderedFilms);
	}
}
