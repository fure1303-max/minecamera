package com.minecamera.media;

public record StoredMediaEntry(
	String mediaId,
	String filePath,
	int width,
	int height,
	String contentType,
	String author,
	String captureSource,
	long capturedAtEpochSeconds
) {
}

