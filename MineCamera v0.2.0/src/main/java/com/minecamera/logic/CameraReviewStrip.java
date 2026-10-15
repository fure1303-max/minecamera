package com.minecamera.logic;

import com.minecamera.film.FilmRecord;
import java.util.Comparator;
import java.util.List;

public final class CameraReviewStrip {
	private static final Comparator<FilmRecord> REVIEW_ORDER = Comparator
		.comparingLong(FilmRecord::capturedAtEpochSeconds)
		.reversed()
		.thenComparing(FilmRecord::mediaId, Comparator.nullsLast(String::compareTo));

	private CameraReviewStrip() {
	}

	public static List<FilmRecord> orderedExposedFilms(List<FilmRecord> films) {
		return films.stream()
			.sorted(REVIEW_ORDER)
			.toList();
	}

	public static String defaultSelectedMediaId(List<FilmRecord> orderedFilms) {
		List<FilmRecord> normalizedFilms = orderedExposedFilms(orderedFilms);
		if (normalizedFilms.isEmpty()) {
			return null;
		}
		return normalizedFilms.getFirst().mediaId();
	}

	public static String stepSelectedMediaId(List<FilmRecord> orderedFilms, String currentMediaId, int delta) {
		List<FilmRecord> normalizedFilms = orderedExposedFilms(orderedFilms);
		if (normalizedFilms.isEmpty()) {
			return null;
		}
		if (delta == 0) {
			return currentMediaId == null ? defaultSelectedMediaId(normalizedFilms) : currentMediaId;
		}

		int currentIndex = indexOfMediaId(normalizedFilms, currentMediaId);
		if (currentIndex < 0) {
			currentIndex = 0;
		}
		int nextIndex = Math.floorMod(currentIndex + delta, normalizedFilms.size());
		return normalizedFilms.get(nextIndex).mediaId();
	}

	public static boolean containsMediaId(List<FilmRecord> orderedFilms, String mediaId) {
		return indexOfMediaId(orderedExposedFilms(orderedFilms), mediaId) >= 0;
	}

	private static int indexOfMediaId(List<FilmRecord> orderedFilms, String mediaId) {
		for (int index = 0; index < orderedFilms.size(); index++) {
			if (java.util.Objects.equals(orderedFilms.get(index).mediaId(), mediaId)) {
				return index;
			}
		}
		return -1;
	}
}
