package com.minecamera.media;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MediaIndexStore {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final Path indexPath;

	public MediaIndexStore(Path rootDirectory) {
		this.indexPath = rootDirectory.resolve("media-index.json");
	}

	public Map<String, StoredMediaEntry> load() throws IOException {
		Map<String, StoredMediaEntry> result = new LinkedHashMap<>();
		if (!Files.exists(indexPath)) {
			return result;
		}

		try (BufferedReader reader = Files.newBufferedReader(indexPath, StandardCharsets.UTF_8)) {
			IndexContainer container = GSON.fromJson(reader, IndexContainer.class);
			if (container != null && container.entries != null) {
				for (StoredMediaEntry entry : container.entries) {
					if (entry != null && entry.mediaId() != null) {
						result.put(entry.mediaId(), entry);
					}
				}
			}
		} catch (JsonSyntaxException exception) {
			throw new IOException("Corrupted media index JSON", exception);
		}
		return result;
	}

	public void save(Map<String, StoredMediaEntry> entries) throws IOException {
		Files.createDirectories(indexPath.getParent());
		IndexContainer container = new IndexContainer(new ArrayList<>(entries.values()));
		try (BufferedWriter writer = Files.newBufferedWriter(indexPath, StandardCharsets.UTF_8)) {
			GSON.toJson(container, writer);
		}
	}

	public Path indexPath() {
		return indexPath;
	}

	private record IndexContainer(List<StoredMediaEntry> entries) {
	}
}
