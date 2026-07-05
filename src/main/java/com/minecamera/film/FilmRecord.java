package com.minecamera.film;

public record FilmRecord(
	FilmState state,
	FilmContentType contentType,
	String mediaId,
	CaptureSettings captureSettings,
	String author,
	long capturedAtEpochSeconds,
	String captureSource
) {
	public static FilmRecord blank() {
		return new FilmRecord(FilmState.BLANK, FilmContentType.PHOTO, null, null, null, 0L, null);
	}

	public static FilmRecord exposed(
		FilmContentType contentType,
		String mediaId,
		CaptureSettings captureSettings,
		String author,
		long capturedAtEpochSeconds,
		String captureSource
	) {
		return new FilmRecord(
			FilmState.EXPOSED,
			contentType,
			mediaId,
			captureSettings,
			author,
			capturedAtEpochSeconds,
			captureSource
		);
	}

	public boolean isBlank() {
		return state == FilmState.BLANK;
	}
}
