package com.minecamera.media;

import java.nio.file.Path;

public record MineCameraPaths(
	Path root,
	Path rawRoot,
	Path captureRoot,
	Path displayCacheRoot
) {
	public static MineCameraPaths fromRunDirectory(Path runDirectory) {
		Path root = runDirectory.resolve("minecamera");
		return new MineCameraPaths(
			root,
			root.resolve("raw"),
			root.resolve("captures"),
			root.resolve("display-cache")
		);
	}
}
