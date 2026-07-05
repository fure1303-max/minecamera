package com.minecamera.display;

import com.minecamera.logic.GridPoint;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record DisplaySurface(BlockFace face, Direction facing, Direction normal, Direction right, Direction down) {
	public static DisplaySurface from(BlockState state) {
		return of(state.get(Properties.BLOCK_FACE), state.get(Properties.HORIZONTAL_FACING));
	}

	public static DisplaySurface of(BlockFace face, Direction facing) {
		if (!facing.getAxis().isHorizontal()) {
			throw new IllegalArgumentException("Display surface facing must be horizontal");
		}

		return switch (face) {
			case FLOOR -> new DisplaySurface(face, facing, Direction.UP, facing.rotateYClockwise(), facing.getOpposite());
			case WALL -> new DisplaySurface(face, facing, facing, facing.rotateYCounterclockwise(), Direction.DOWN);
			case CEILING -> throw new IllegalArgumentException("Ceiling-mounted displays are not supported");
		};
	}

	public Set<Direction> connectionDirections() {
		return Set.of(right, right.getOpposite(), down, down.getOpposite());
	}

	public GridPoint project(BlockPos pos) {
		return new GridPoint(dot(pos, right), dot(pos, down));
	}

	private static int dot(BlockPos pos, Direction direction) {
		return pos.getX() * direction.getOffsetX()
			+ pos.getY() * direction.getOffsetY()
			+ pos.getZ() * direction.getOffsetZ();
	}
}
