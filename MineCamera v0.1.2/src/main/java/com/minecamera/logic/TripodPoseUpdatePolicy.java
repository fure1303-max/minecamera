package com.minecamera.logic;

public final class TripodPoseUpdatePolicy {
	private static final double MAX_DISTANCE_SQUARED = 64.0D;

	private TripodPoseUpdatePolicy() {
	}

	public static boolean canApply(boolean isTripodTarget, boolean hasMountedCamera, double squaredDistanceToTripod) {
		return isTripodTarget && hasMountedCamera && squaredDistanceToTripod <= MAX_DISTANCE_SQUARED;
	}
}
