package com.minecamera.logic;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class CameraPoseResolver {
	private CameraPoseResolver() {
	}

	public static CameraRigSnapshot resolveHandheld(CameraRigState rigState, Vec3d eyePosition, float yaw, float pitch) {
		return new CameraRigSnapshot(
			eyePosition,
			yaw,
			pitch,
			rigState.captureWidth(),
			rigState.captureHeight(),
			rigState.focalLengthMm(),
			rigState.apertureFStop(),
			rigState.focusMode(),
			rigState.focusDistance(),
			rigState.sourceType(),
			null
		);
	}

	public static CameraRigSnapshot resolveTripod(CameraRigState rigState, float yaw, float pitch) {
		TripodCameraPose pose = TripodCameraPose.fromBlock(rigState.tripodPos(), yaw, clampPitch(pitch));
		return new CameraRigSnapshot(
			pose.position(),
			pose.yaw(),
			pose.pitch(),
			rigState.captureWidth(),
			rigState.captureHeight(),
			rigState.focalLengthMm(),
			rigState.apertureFStop(),
			rigState.focusMode(),
			rigState.focusDistance(),
			rigState.sourceType(),
			rigState.tripodPos()
		);
	}

	public static CameraRigSnapshot resolveTripodViewport(
		CameraRigState rigState,
		CameraPreviewMode previewMode,
		float playerYaw,
		float playerPitch,
		boolean hasStoredPose,
		float storedYaw,
		float storedPitch
	) {
		boolean useStoredPose = previewMode == CameraPreviewMode.REVIEW && hasStoredPose;
		float yaw = useStoredPose ? storedYaw : playerYaw;
		float pitch = useStoredPose ? storedPitch : playerPitch;
		return resolveTripod(rigState, yaw, pitch);
	}

	public static float clampPitch(float pitch) {
		return MathHelper.clamp(pitch, -85.0F, 85.0F);
	}
}
