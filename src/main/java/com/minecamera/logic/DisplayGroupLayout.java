package com.minecamera.logic;

public record DisplayGroupLayout(GridPoint anchor, int width, int height) {
	public boolean contains(GridPoint point) {
		return point.x() >= anchor.x()
			&& point.x() < anchor.x() + width
			&& point.y() >= anchor.y()
			&& point.y() < anchor.y() + height;
	}
}

