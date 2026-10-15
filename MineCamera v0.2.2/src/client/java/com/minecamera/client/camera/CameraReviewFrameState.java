package com.minecamera.client.camera;

import com.minecamera.client.media.LoadedPhotoTexture;
import net.minecraft.text.Text;

record CameraReviewFrameState(LoadedPhotoTexture texture, Text message, int index, int total) {
	static CameraReviewFrameState empty(Text message) {
		return new CameraReviewFrameState(null, message, 0, 0);
	}

	static CameraReviewFrameState message(Text message, int index, int total) {
		return new CameraReviewFrameState(null, message, index, total);
	}

	static CameraReviewFrameState texture(LoadedPhotoTexture texture, Text message, int index, int total) {
		return new CameraReviewFrameState(texture, message, index, total);
	}
}
