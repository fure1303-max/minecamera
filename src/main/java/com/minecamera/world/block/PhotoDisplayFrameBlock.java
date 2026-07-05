package com.minecamera.world.block;

import com.minecamera.display.DisplaySurface;
import com.minecamera.display.DisplayFrameState;
import com.minecamera.film.FilmRecord;
import com.minecamera.film.FilmService;
import com.minecamera.logic.DisplayFrameStateLogic;
import com.minecamera.logic.GridPoint;
import com.minecamera.world.block.entity.PhotoDisplayFrameBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PhotoDisplayFrameBlock extends BlockWithEntity implements BlockEntityProvider {
	public static final MapCodec<PhotoDisplayFrameBlock> CODEC = createCodec(PhotoDisplayFrameBlock::new);
	public static final EnumProperty<BlockFace> BLOCK_FACE = Properties.BLOCK_FACE;
	public static final EnumProperty<Direction> HORIZONTAL_FACING = Properties.HORIZONTAL_FACING;
	private static final double THICKNESS = 1.0D / 16.0D;
	private static final VoxelShape FLOOR_SHAPE = VoxelShapes.cuboid(0.0D, 0.0D, 0.0D, 1.0D, THICKNESS, 1.0D);
	private static final Map<Direction, VoxelShape> WALL_SHAPES = Map.of(
		Direction.NORTH, VoxelShapes.cuboid(0.0D, 0.0D, 1.0D - THICKNESS, 1.0D, 1.0D, 1.0D),
		Direction.SOUTH, VoxelShapes.cuboid(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, THICKNESS),
		Direction.EAST, VoxelShapes.cuboid(0.0D, 0.0D, 0.0D, THICKNESS, 1.0D, 1.0D),
		Direction.WEST, VoxelShapes.cuboid(1.0D - THICKNESS, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)
	);

	public PhotoDisplayFrameBlock(Settings properties) {
		super(properties);
		setDefaultState(getStateManager().getDefaultState().with(BLOCK_FACE, BlockFace.FLOOR).with(HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
		builder.add(BLOCK_FACE, HORIZONTAL_FACING);
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (state.get(BLOCK_FACE) == BlockFace.FLOOR) {
			return FLOOR_SHAPE;
		}
		return WALL_SHAPES.getOrDefault(state.get(HORIZONTAL_FACING), FLOOR_SHAPE);
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext context) {
		for (Direction direction : context.getPlacementDirections()) {
			BlockFace blockFace = placementFace(direction);
			if (blockFace == null) {
				continue;
			}

			Direction horizontalFacing = blockFace == BlockFace.FLOOR ? context.getHorizontalPlayerFacing() : direction;
			BlockState state = getDefaultState().with(BLOCK_FACE, blockFace).with(HORIZONTAL_FACING, horizontalFacing);
			if (state.canPlaceAt(context.getWorld(), context.getBlockPos())) {
				return state;
			}
		}
		return null;
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos supportPos = supportPos(pos, state);
		Direction supportSide = state.get(BLOCK_FACE) == BlockFace.FLOOR ? Direction.UP : state.get(HORIZONTAL_FACING);
		return world.getBlockState(supportPos).isSideSolidFullSquare(world, supportPos, supportSide);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PhotoDisplayFrameBlockEntity(pos, state);
	}

	@Override
	protected ActionResult onUseWithItem(
		ItemStack stack,
		BlockState state,
		World world,
		BlockPos pos,
		PlayerEntity player,
		Hand hand,
		BlockHitResult hit
	) {
		if (!FilmService.isExposed(stack)) {
			return ActionResult.PASS;
		}

		FilmRecord filmRecord = FilmService.read(stack);
		if (filmRecord.mediaId() == null || filmRecord.mediaId().isBlank()) {
			return ActionResult.PASS;
		}

		if (!world.isClient() && world instanceof ServerWorld serverWorld) {
			applyFilmToConnectedFrames(serverWorld, pos, filmRecord.mediaId());
		}
		return ActionResult.SUCCESS;
	}

	@Override
	protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
		super.onStateReplaced(state, world, pos, moved);
		if (!moved) {
			repartitionNeighboringFrames(world, pos, state);
		}
	}

	private static void applyFilmToConnectedFrames(ServerWorld world, BlockPos clickedPos, String mediaId) {
		DisplaySurface surface = DisplaySurface.from(world.getBlockState(clickedPos));
		Map<BlockPos, PhotoDisplayFrameBlockEntity> frames = collectConnectedFrames(world, clickedPos, surface);
		if (frames.isEmpty()) {
			return;
		}

		Map<GridPoint, DisplayFrameState> current = new HashMap<>();
		Set<GridPoint> occupied = new HashSet<>();
		for (Map.Entry<BlockPos, PhotoDisplayFrameBlockEntity> entry : frames.entrySet()) {
			GridPoint point = surface.project(entry.getKey());
			occupied.add(point);
			DisplayFrameState state = entry.getValue().getDisplayState();
			if (!state.isEmpty()) {
				current.put(point, state);
			}
		}

		Map<GridPoint, DisplayFrameState> updated = DisplayFrameStateLogic.applyPhoto(
			occupied,
			current,
			surface.project(clickedPos),
			mediaId
		);
		applyStates(frames, updated);
	}

	private static void repartitionNeighboringFrames(ServerWorld world, BlockPos removedPos, BlockState removedState) {
		DisplaySurface surface = DisplaySurface.from(removedState);
		Set<BlockPos> visited = new HashSet<>();
		for (Direction direction : surface.connectionDirections()) {
			BlockPos neighborPos = removedPos.offset(direction);
			if (visited.contains(neighborPos) || !isFrameWithSurface(world, neighborPos, surface)) {
				continue;
			}

			Map<BlockPos, PhotoDisplayFrameBlockEntity> component = collectConnectedFrames(world, neighborPos, surface);
			visited.addAll(component.keySet());
			Map<GridPoint, DisplayFrameState> previous = new HashMap<>();
			Set<GridPoint> occupied = new HashSet<>();

			for (Map.Entry<BlockPos, PhotoDisplayFrameBlockEntity> entry : component.entrySet()) {
				GridPoint point = surface.project(entry.getKey());
				occupied.add(point);
				DisplayFrameState state = entry.getValue().getDisplayState();
				if (!state.isEmpty()) {
					previous.put(point, state);
				}
			}

			Map<GridPoint, DisplayFrameState> updated = DisplayFrameStateLogic.repartition(occupied, previous);
			applyStates(component, updated);
		}
	}

	private static void applyStates(Map<BlockPos, PhotoDisplayFrameBlockEntity> frames, Map<GridPoint, DisplayFrameState> states) {
		for (Map.Entry<BlockPos, PhotoDisplayFrameBlockEntity> entry : frames.entrySet()) {
			DisplaySurface surface = DisplaySurface.from(entry.getValue().getCachedState());
			GridPoint point = surface.project(entry.getKey());
			entry.getValue().setDisplayState(states.getOrDefault(point, DisplayFrameState.empty()));
		}
	}

	private static Map<BlockPos, PhotoDisplayFrameBlockEntity> collectConnectedFrames(
		ServerWorld world,
		BlockPos start,
		DisplaySurface surface
	) {
		Map<BlockPos, PhotoDisplayFrameBlockEntity> frames = new HashMap<>();
		if (!isFrameWithSurface(world, start, surface)) {
			return frames;
		}

		ArrayDeque<BlockPos> queue = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		queue.add(start);
		visited.add(start);

		while (!queue.isEmpty()) {
			BlockPos current = queue.removeFirst();

			BlockEntity blockEntity = world.getBlockEntity(current);
			if (blockEntity instanceof PhotoDisplayFrameBlockEntity frame) {
				frames.put(current, frame);
			}

			for (Direction direction : surface.connectionDirections()) {
				BlockPos neighbor = current.offset(direction);
				if (visited.add(neighbor) && isFrameWithSurface(world, neighbor, surface)) {
					queue.addLast(neighbor);
				}
			}
		}

		return frames;
	}

	private static boolean isFrameWithSurface(World world, BlockPos pos, DisplaySurface surface) {
		BlockState state = world.getBlockState(pos);
		return state.getBlock() instanceof PhotoDisplayFrameBlock && surface.equals(DisplaySurface.from(state));
	}

	private static @Nullable BlockFace placementFace(Direction direction) {
		if (direction == Direction.UP) {
			return BlockFace.FLOOR;
		}
		if (direction.getAxis().isHorizontal()) {
			return BlockFace.WALL;
		}
		return null;
	}

	private static BlockPos supportPos(BlockPos pos, BlockState state) {
		return state.get(BLOCK_FACE) == BlockFace.FLOOR ? pos.down() : pos.offset(state.get(HORIZONTAL_FACING).getOpposite());
	}
}
