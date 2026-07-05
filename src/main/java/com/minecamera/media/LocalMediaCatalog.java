package com.minecamera.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public final class LocalMediaCatalog {
	private final Path root;
	private final MediaIndexStore indexStore;

	public LocalMediaCatalog(Path root) {
		this.root = root;
		this.indexStore = new MediaIndexStore(root);
	}

	public Optional<ResolvedMedia> find(String mediaId) throws IOException {
		Map<String, StoredMediaEntry> entries = indexStore.load();
		StoredMediaEntry entry = entries.get(mediaId);
		if (entry == null) {
			return Optional.empty();
		}

		Path absolutePath = root.resolve(entry.filePath()).normalize();
		return Optional.of(new ResolvedMedia(entry, absolutePath, Files.exists(absolutePath)));
	}
}
