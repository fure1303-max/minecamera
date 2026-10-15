package com.minecamera.client.capture;

import com.minecamera.logic.CameraRigSnapshot;

public record MineCameraCaptureRequest(
	int filmSlot,
	CameraRigSnapshot snapshot
) {
}
