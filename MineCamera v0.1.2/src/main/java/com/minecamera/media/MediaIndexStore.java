package com.minecamera.media;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MediaIndexStore {
	private static final Pattern ENTRY_PATTERN = Pattern.compile(
		"\\{\\s*\"mediaId\":\"(?<mediaId>[^\"]+)\",\\s*\"filePath\":\"(?<filePath>[^\"]+)\",\\s*\"width\":(?<width>\\d+),\\s*\"height\":(?<height>\\d+),\\s*\"contentType\":\"(?<contentType>[^\"]+)\",\\s*\"author\":\"(?<author>[^\"]+)\",\\s*\"captureSource\":\"(?<captureSource>[^\"]+)\",\\s*\"capturedAtEpochSeconds\":(?<capturedAtEpochSeconds>\\d+)\\s*\\}"
	);

	private final Path indexPath;

	public MediaIndexStore(Path rootDirectory) {
		this.indexPath = rootDirectory.resolve("media-index.json");
	}

	public Map<String, StoredMediaEntry> load() throws IOException {
		Map<String, StoredMediaEntry> result = new LinkedHashMap<>();
		if (!Files.exists(indexPath)) {
			return result;
		}

		String json = Files.readString(indexPath, StandardCharsets.UTF_8);
		Matcher matcher = ENTRY_PATTERN.matcher(json);
		while (matcher.find()) {
			StoredMediaEntry entry = new StoredMediaEntry(
				matcher.group("mediaId"),
				matcher.group("filePath"),
				Integer.parseInt(matcher.group("width")),
				Integer.parseInt(matcher.group("height")),
				matcher.group("contentType"),
				matcher.group("author"),
				matcher.group("captureSource"),
				Long.parseLong(matcher.group("capturedAtEpochSeconds"))
			);
			result.put(entry.mediaId(), entry);
		}
		return result;
	}

	public void save(Map<String, StoredMediaEntry> entries) throws IOException {
		Files.createDirectories(indexPath.getParent());

		StringBuilder builder = new StringBuilder();
		builder.append("{\n  \"entries\": [\n");
		boolean first = true;
		for (StoredMediaEntry entry : entries.values()) {
			if (!first) {
				builder.append(",\n");
			}
			first = false;
			builder.append("    {")
				.append("\"mediaId\":\"").append(escape(entry.mediaId())).append("\",")
				.append("\"filePath\":\"").append(escape(entry.filePath())).append("\",")
				.append("\"width\":").append(entry.width()).append(',')
				.append("\"height\":").append(entry.height()).append(',')
				.append("\"contentType\":\"").append(escape(entry.contentType())).append("\",")
				.append("\"author\":\"").append(escape(entry.author())).append("\",")
				.append("\"captureSource\":\"").append(escape(entry.captureSource())).append("\",")
				.append("\"capturedAtEpochSeconds\":").append(entry.capturedAtEpochSeconds())
				.append('}');
		}
		builder.append("\n  ]\n}\n");

		Files.writeString(indexPath, builder.toString(), StandardCharsets.UTF_8);
	}

	public Path indexPath() {
		return indexPath;
	}

	private static String escape(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}

