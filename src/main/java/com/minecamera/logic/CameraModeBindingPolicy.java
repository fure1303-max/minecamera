package com.minecamera.logic;

public final class CameraModeBindingPolicy {
	private CameraModeBindingPolicy() {
	}

	public static boolean shouldClose(boolean tripodMode, boolean heldItemIsCamera) {
		return !tripodMode && !heldItemIsCamera;
	}
}
