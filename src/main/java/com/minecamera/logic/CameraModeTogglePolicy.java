package com.minecamera.logic;

public final class CameraModeTogglePolicy {
	public enum Action {
		OPEN,
		CLOSE
	}

	private CameraModeTogglePolicy() {
	}

	public static Action decide(boolean requestTripodMode, boolean active, boolean activeTripodMode) {
		if (!active) {
			return Action.OPEN;
		}
		if (requestTripodMode == activeTripodMode) {
			return Action.CLOSE;
		}
		return Action.OPEN;
	}
}
