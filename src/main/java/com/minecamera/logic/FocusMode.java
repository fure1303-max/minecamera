package com.minecamera.logic;

public enum FocusMode {
	AUTO,
	LOCKED,
	MANUAL;

	public FocusMode next() {
		return values()[(ordinal() + 1) % values().length];
	}
}

