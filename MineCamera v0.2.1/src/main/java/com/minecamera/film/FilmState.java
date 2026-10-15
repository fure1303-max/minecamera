package com.minecamera.film;

public enum FilmState {
	BLANK("blank"),
	EXPOSED("exposed");

	private final String id;

	FilmState(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}

	public static FilmState fromId(String id) {
		for (FilmState value : values()) {
			if (value.id.equals(id)) {
				return value;
			}
		}
		return BLANK;
	}
}
