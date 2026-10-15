package com.minecamera.film;

public enum FilmContentType {
	PHOTO("photo"),
	VIDEO_RESERVED("video_reserved");

	private final String id;

	FilmContentType(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}

	public static FilmContentType fromId(String id) {
		for (FilmContentType value : values()) {
			if (value.id.equals(id)) {
				return value;
			}
		}
		return PHOTO;
	}
}
