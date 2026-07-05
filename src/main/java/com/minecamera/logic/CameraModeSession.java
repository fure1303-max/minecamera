package com.minecamera.logic;

import java.util.Objects;

public record CameraModeSession(String source, int presetIndex, CameraPreviewMode previewMode, String selectedReviewMediaId) {
	public CameraModeSession {
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(previewMode, "previewMode");
	}

	public static CameraModeSession handheld() {
		return new CameraModeSession("handheld", 0, CameraPreviewMode.LIVE, null);
	}

	public static CameraModeSession tripod() {
		return new CameraModeSession("tripod", 0, CameraPreviewMode.LIVE, null);
	}

	public ResolutionPreset currentPreset() {
		return CameraModePresetSet.PRESETS.get(presetIndex);
	}

	public CameraModeSession nextPreset() {
		return new CameraModeSession(source, (presetIndex + 1) % CameraModePresetSet.PRESETS.size(), previewMode, selectedReviewMediaId);
	}

	public CameraModeSession withPreviewMode(CameraPreviewMode nextPreviewMode) {
		return new CameraModeSession(source, presetIndex, nextPreviewMode, selectedReviewMediaId);
	}

	public CameraModeSession withSelectedReviewMediaId(String mediaId) {
		return new CameraModeSession(source, presetIndex, previewMode, mediaId);
	}
}
