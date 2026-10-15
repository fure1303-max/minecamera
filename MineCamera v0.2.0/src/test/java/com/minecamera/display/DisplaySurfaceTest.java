package com.minecamera.display;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.minecamera.logic.GridPoint;
import java.lang.reflect.Method;
import java.util.Set;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.junit.jupiter.api.Test;

final class DisplaySurfaceTest {
	@Test
	void northWallProjectsAcrossAndDownFromViewerPerspective() throws ReflectiveOperationException {
		Object surface = createSurface(BlockFace.WALL, Direction.NORTH);

		assertEquals(Set.of(Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN), connectionDirections(surface));
		assertEquals(new GridPoint(-10, -64), project(surface, new BlockPos(10, 64, 20)));
		assertEquals(new GridPoint(-9, -64), project(surface, new BlockPos(9, 64, 20)));
		assertEquals(new GridPoint(-10, -63), project(surface, new BlockPos(10, 63, 20)));
	}

	@Test
	void floorFacingNorthProjectsEastAndSouth() throws ReflectiveOperationException {
		Object surface = createSurface(BlockFace.FLOOR, Direction.NORTH);

		assertEquals(Set.of(Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH), connectionDirections(surface));
		assertEquals(new GridPoint(10, 20), project(surface, new BlockPos(10, 64, 20)));
		assertEquals(new GridPoint(11, 20), project(surface, new BlockPos(11, 64, 20)));
		assertEquals(new GridPoint(10, 21), project(surface, new BlockPos(10, 64, 21)));
	}

	private static Object createSurface(BlockFace face, Direction facing) throws ReflectiveOperationException {
		Class<?> type = Class.forName("com.minecamera.display.DisplaySurface");
		Method method = type.getMethod("of", BlockFace.class, Direction.class);
		return method.invoke(null, face, facing);
	}

	@SuppressWarnings("unchecked")
	private static Set<Direction> connectionDirections(Object surface) throws ReflectiveOperationException {
		Method method = surface.getClass().getMethod("connectionDirections");
		return (Set<Direction>) method.invoke(surface);
	}

	private static GridPoint project(Object surface, BlockPos pos) throws ReflectiveOperationException {
		Method method = surface.getClass().getMethod("project", BlockPos.class);
		return (GridPoint) method.invoke(surface, pos);
	}
}
