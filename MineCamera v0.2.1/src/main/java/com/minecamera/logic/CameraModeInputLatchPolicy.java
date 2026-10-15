package com.minecamera.logic;

public final class CameraModeInputLatchPolicy {
	private CameraModeInputLatchPolicy() {
	}

	public static boolean closeLatchOnOpen(boolean openedWithRightClick) {
		return openedWithRightClick;
	}

	public static boolean shouldTriggerClose(boolean wasDown, boolean isDown) {
		return isDown && !wasDown;
	}
}
