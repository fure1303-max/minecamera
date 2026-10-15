package com.minecamera.media;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

public final class ServerDisplayMediaStore {
	private static final String DIRECTORY_NAME = "minecamera/display-cache";

	private final Path root;

	public ServerDisplayMediaStore(Path worldRoot) {
		this.root = worldRoot.resolve(DIRECTORY_NAME);
	}

	public static ServerDisplayMediaStore fromServer(MinecraftServer server) {
		return new ServerDisplayMediaStore(server.getSavePath(WorldSavePath.ROOT));
	}

	public boolean write(String mediaId, byte[] jpegBytes) throws IOException {
		if (!DisplayThumbnailPolicy.isValidMediaId(mediaId) || DisplayThumbnailPolicy.inspectJpeg(jpegBytes).isEmpty()) {
			return false;
		}

		Files.createDirectories(root);
		Path destination = pathFor(mediaId);
		Path temporary = destination.resolveSibling(destination.getFileName() + ".tmp");
		Files.write(temporary, jpegBytes);
		try {
			Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException exception) {
			Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
		}
		return true;
	}

	public Optional<byte[]> read(String mediaId) throws IOException {
		if (!DisplayThumbnailPolicy.isValidMediaId(mediaId)) {
			return Optional.empty();
		}
		Path path = pathFor(mediaId);
		if (!Files.isRegularFile(path) || Files.size(path) > DisplayThumbnailPolicy.MAX_BYTES) {
			return Optional.empty();
		}
		byte[] bytes = Files.readAllBytes(path);
		return DisplayThumbnailPolicy.inspectJpeg(bytes).isPresent() ? Optional.of(bytes) : Optional.empty();
	}

	private Path pathFor(String mediaId) {
		return root.resolve(mediaId + ".jpg");
	}
}
