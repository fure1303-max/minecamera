package com.minecamera.client.capture;

import java.nio.file.Path;

public record CapturedPhoto(String mediaId, Path path, byte[] thumbnailBytes) {
}
