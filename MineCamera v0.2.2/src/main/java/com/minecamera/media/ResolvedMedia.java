package com.minecamera.media;

import java.nio.file.Path;

public record ResolvedMedia(
	StoredMediaEntry entry,
	Path absolutePath,
	boolean available
) {
}
