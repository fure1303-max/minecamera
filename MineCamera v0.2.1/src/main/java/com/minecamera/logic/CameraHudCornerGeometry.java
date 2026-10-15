package com.minecamera.logic;

public record CameraHudCornerGeometry(Rect horizontal, Rect vertical) {
	public static CameraHudCornerGeometry topLeft(int x, int y, int length, int thickness) {
		return new CameraHudCornerGeometry(
			new Rect(x, y, x + length, y + thickness),
			new Rect(x, y, x + thickness, y + length)
		);
	}

	public static CameraHudCornerGeometry topRight(int x, int y, int length, int thickness) {
		return new CameraHudCornerGeometry(
			new Rect(x - length + thickness, y, x + thickness, y + thickness),
			new Rect(x, y, x + thickness, y + length)
		);
	}

	public static CameraHudCornerGeometry bottomLeft(int x, int y, int length, int thickness) {
		return new CameraHudCornerGeometry(
			new Rect(x, y, x + length, y + thickness),
			new Rect(x, y - length + thickness, x + thickness, y + thickness)
		);
	}

	public static CameraHudCornerGeometry bottomRight(int x, int y, int length, int thickness) {
		return new CameraHudCornerGeometry(
			new Rect(x - length + thickness, y, x + thickness, y + thickness),
			new Rect(x, y - length + thickness, x + thickness, y + thickness)
		);
	}

	public record Rect(int x1, int y1, int x2, int y2) {
	}
}
