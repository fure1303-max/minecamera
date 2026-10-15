package com.minecamera.media;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class MediaIndexStoreTest {
	@Test
	void persistsAndReloadsMediaEntries(@TempDir Path tempDir) throws IOException {
		MediaIndexStore store = new MediaIndexStore(tempDir);
		StoredMediaEntry entry = new StoredMediaEntry(
			"media-001",
			"captures/media-001.png",
			1920,
			1080,
			"photo",
			"player-one",
			"handheld",
			1_728_000_000L
		);

		store.save(Map.of(entry.mediaId(), entry));
		Map<String, StoredMediaEntry> loaded = store.load();

		assertEquals(1, loaded.size());
		assertEquals(entry, loaded.get("media-001"));
		assertEquals(true, Files.exists(tempDir.resolve("media-index.json")));
	}
}

