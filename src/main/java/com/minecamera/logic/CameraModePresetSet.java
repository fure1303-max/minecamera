package com.minecamera.logic;

import java.util.List;

public final class CameraModePresetSet {
	public static final List<ResolutionPreset> PRESETS = List.of(
		ResolutionPresets.standardPreset("1:1", 1, 1),
		ResolutionPresets.standardPreset("4:3", 4, 3),
		ResolutionPresets.standardPreset("3:2", 3, 2),
		ResolutionPresets.standardPreset("16:9", 16, 9),
		ResolutionPresets.standardPreset("21:9", 21, 9),
		ResolutionPresets.maxPreset("16:9 4K", 16, 9),
		ResolutionPresets.maxPreset("3:2 4K", 3, 2),
		ResolutionPresets.maxPreset("1:1 4K", 1, 1)
	);

	private CameraModePresetSet() {
	}
}
