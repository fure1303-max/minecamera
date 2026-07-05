package com.minecamera.logic;

import java.util.List;

public final class CameraModePresetSet {
	public static final List<ResolutionPreset> PRESETS = List.of(
		ResolutionPresets.maxPreset("1:1", 1, 1),
		ResolutionPresets.maxPreset("4:3", 4, 3),
		ResolutionPresets.maxPreset("3:2", 3, 2),
		ResolutionPresets.maxPreset("16:9", 16, 9),
		ResolutionPresets.maxPreset("21:9", 21, 9)
	);

	private CameraModePresetSet() {
	}
}
