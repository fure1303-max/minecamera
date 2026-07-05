package com.minecamera.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class LocalMediaCatalogTest {
	@Test
	void resolvesIndexedMediaToAnAbsoluteCapturePath(@TempDir Path tempDir) throws IOException {
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
		Files.createDirectories(tempDir.resolve("captures"));
		Files.writeString(tempDir.resolve("captures/media-001.png"), "fake");
		new MediaIndexStore(tempDir).save(Map.of(entry.mediaId(), entry));

		ResolvedMedia resolved = new LocalMediaCatalog(tempDir).find("media-001").orElseThrow();

		assertEquals(entry, resolved.entry());
		assertEquals(tempDir.resolve("captures/media-001.png"), resolved.absolutePath());
		assertTrue(resolved.available());
	}

	@Test
	void marksResolvedMediaAsMissingWhenTheIndexedFileIsGone(@TempDir Path tempDir) throws IOException {
		StoredMediaEntry entry = new StoredMediaEntry(
			"media-002",
			"captures/media-002.png",
			2880,
			2880,
			"photo",
			"player-two",
			"tripod",
			1_728_000_100L
		);
		new MediaIndexStore(tempDir).save(Map.of(entry.mediaId(), entry));

		ResolvedMedia resolved = new LocalMediaCatalog(tempDir).find("media-002").orElseThrow();

		assertEquals(entry, resolved.entry());
		assertEquals(tempDir.resolve("captures/media-002.png"), resolved.absolutePath());
		assertTrue(!resolved.available());
	}

	@Test
	void returnsEmptyWhenMediaIdIsNotIndexed(@TempDir Path tempDir) throws IOException {
		new MediaIndexStore(tempDir).save(Map.of());

		assertTrue(new LocalMediaCatalog(tempDir).find("unknown-media").isEmpty());
	}
}
