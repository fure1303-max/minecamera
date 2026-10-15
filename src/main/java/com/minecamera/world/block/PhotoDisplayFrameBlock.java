package com.minecamera.world.block;

import com.minecamera.display.DisplaySurface;
import com.minecamera.display.DisplayFrameState;
import com.minecamera.film.FilmContentType;
import com.minecamera.film.FilmRecord;
import com.minecamera.film.FilmService;
import com.minecamera.logic.DisplayFrameStateLogic;
import com.minecamera.logic.GridPoint;
import com.minecamera.world.block.entity.PhotoDisplayFrameBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.tick.ScheduledTickView;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PhotoDisplayFrameBlock extends BlockWithEntity implements BlockEntityProvider {
	private static final int MAX_CONNECTED_FRAMES = 1_024;
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
		Direction clickedSide = context.getSide();
		BlockFace blockFace = placementFace(clickedSide);
		if (blockFace == null) {
			return null;
		}

		Direction horizontalFacing = blockFace == BlockFace.FLOOR ? context.getHorizontalPlayerFacing() : clickedSide;
		BlockState state = getDefaultState().with(BLOCK_FACE, blockFace).with(HORIZONTAL_FACING, horizontalFacing);
		return state.canPlaceAt(context.getWorld(), context.getBlockPos()) ? state : null;
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos supportPos = supportPos(pos, state);
		Direction supportSide = state.get(BLOCK_FACE) == BlockFace.FLOOR ? Direction.UP : state.get(HORIZONTAL_FACING);
		return world.getBlockState(supportPos).isSideSolidFullSquare(world, supportPos, supportSide);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		if (neighborPos.equals(supportPos(pos, state)) && !state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		}
		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
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
			applyFilmToConnectedFrames(serverWorld, pos, player, stack, filmRecord);
		}
		return ActionResult.SUCCESS;
	}

	@Override
	protected ActionResult onUse(
		BlockState state,
		World world,
		BlockPos pos,
		PlayerEntity player,
		BlockHitResult hit
	) {
		if (!player.getMainHandStack().isEmpty()) {
			return ActionResult.PASS;
		}

		BlockEntity be = world.getBlockEntity(pos);
		if (!(be instanceof PhotoDisplayFrameBlockEntity clickedFrame)) {
			return ActionResult.PASS;
		}

		DisplayFrameState displayState = clickedFrame.getDisplayState();
		if (displayState.isEmpty()) {
			return ActionResult.PASS;
		}

		if (world.isClient()) {
			return ActionResult.SUCCESS;
		}

		if (world instanceof ServerWorld serverWorld) {
			DisplaySurface surface = DisplaySurface.from(state);
			Map<BlockPos, PhotoDisplayFrameBlockEntity> frames = collectConnectedFrames(serverWorld, pos, surface);
			if (frames.isEmpty()) {
				return ActionResult.PASS;
			}

			FilmRecord filmRecord = clickedFrame.getFilmRecord();
			if (filmRecord == null || filmRecord.isBlank()) {
				filmRecord = FilmRecord.exposed(FilmContentType.PHOTO, displayState.mediaId(), null, null, 0L, null);
			}

			Map<GridPoint, DisplayFrameState> current = new HashMap<>();
			Map<String, FilmRecord> filmByMedia = new HashMap<>();
			for (Map.Entry<BlockPos, PhotoDisplayFrameBlockEntity> entry : frames.entrySet()) {
				GridPoint point = surface.project(entry.getKey());
				DisplayFrameState s = entry.getValue().getDisplayState();
				if (!s.isEmpty()) {
					current.put(point, s);
					FilmRecord r = entry.getValue().getFilmRecord();
					if (r != null && !r.isBlank()) {
						filmByMedia.put(s.mediaId(), r);
					}
				}
			}

			Map<GridPoint, DisplayFrameState> updated = DisplayFrameStateLogic.removePhoto(
				current,
				surface.project(pos)
			);
			applyStates(frames, updated, filmByMedia);

			ItemStack filmStack = FilmService.createExposedFilmStack(filmRecord);
			if (!player.getInventory().insertStack(filmStack)) {
				dropStack(serverWorld, pos, filmStack);
			}

			serverWorld.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if (!world.isClient() && world instanceof ServerWorld serverWorld) {
			repartitionConnectedFrames(serverWorld, pos, state);
		}
	}

	@Override
	protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
		FilmRecord removedFilm = null;
		String removedMediaId = null;
		if (!moved) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof PhotoDisplayFrameBlockEntity frame) {
				DisplayFrameState frameState = frame.getDisplayState();
				if (!frameState.isEmpty()) {
					removedMediaId = frameState.mediaId();
					removedFilm = frame.getFilmRecord();
					if (removedFilm == null || removedFilm.isBlank()) {
						removedFilm = FilmRecord.exposed(FilmContentType.PHOTO, removedMediaId, null, null, 0L, null);
					}
				}
			}
		}

		super.onStateReplaced(state, world, pos, moved);

		if (!moved) {
			repartitionNeighboringFrames(world, pos, state);

			if (removedMediaId != null && removedFilm != null) {
				DisplaySurface surface = DisplaySurface.from(state);
				boolean stillPresent = false;
				for (Direction direction : surface.connectionDirections()) {
					BlockPos neighborPos = pos.offset(direction);
					if (world.getBlockEntity(neighborPos) instanceof PhotoDisplayFrameBlockEntity neighborFrame) {
						if (removedMediaId.equals(neighborFrame.getDisplayState().mediaId())) {
							stillPresent = true;
							break;
						}
					}
				}
				if (!stillPresent) {
					dropStack(world, pos, FilmService.createExposedFilmStack(removedFilm));
				}
			}
		}
	}

	private static void applyFilmToConnectedFrames(
		ServerWorld world,
		BlockPos clickedPos,
		PlayerEntity player,
		ItemStack stack,
		FilmRecord filmRecord
	) {
		DisplaySurface surface = DisplaySurface.from(world.getBlockState(clickedPos));
		Map<BlockPos, PhotoDisplayFrameBlockEntity> frames = collectConnectedFrames(world, clickedPos, surface);
		if (frames.isEmpty()) {
			return;
		}

		PhotoDisplayFrameBlockEntity clickedEntity = frames.get(clickedPos);
		DisplayFrameState existingState = clickedEntity != null ? clickedEntity.getDisplayState() : DisplayFrameState.empty();
		FilmRecord existingFilm = clickedEntity != null ? clickedEntity.getFilmRecord() : null;

		Map<GridPoint, DisplayFrameState> current = new HashMap<>();
		Map<String, FilmRecord> filmByMedia = new HashMap<>();
		Set<GridPoint> occupied = new HashSet<>();
		for (Map.Entry<BlockPos, PhotoDisplayFrameBlockEntity> entry : frames.entrySet()) {
			GridPoint point = surface.project(entry.getKey());
			occupied.add(point);
			DisplayFrameState state = entry.getValue().getDisplayState();
			if (!state.isEmpty()) {
				current.put(point, state);
				FilmRecord rec = entry.getValue().getFilmRecord();
				if (rec != null && !rec.isBlank()) {
					filmByMedia.put(state.mediaId(), rec);
				}
			}
		}

		Map<GridPoint, DisplayFrameState> updated = DisplayFrameStateLogic.applyPhoto(
			occupied,
			current,
			surface.project(clickedPos),
			filmRecord.mediaId()
		);
		filmByMedia.put(filmRecord.mediaId(), filmRecord);
		applyStates(frames, updated, filmByMedia);

		if (!existingState.isEmpty()) {
			FilmRecord returnRecord = (existingFilm != null && !existingFilm.isBlank())
				? existingFilm
				: FilmRecord.exposed(FilmContentType.PHOTO, existingState.mediaId(), null, null, 0L, null);
			ItemStack oldFilmStack = FilmService.createExposedFilmStack(returnRecord);
			if (!player.getInventory().insertStack(oldFilmStack)) {
				dropStack(world, clickedPos, oldFilmStack);
			}
		}

		if (!player.isCreative()) {
			stack.decrement(1);
		}

		world.playSound(null, clickedPos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
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
			repartitionComponent(surface, component);
		}
	}

	private static void repartitionConnectedFrames(ServerWorld world, BlockPos start, BlockState state) {
		DisplaySurface surface = DisplaySurface.from(state);
		repartitionComponent(surface, collectConnectedFrames(world, start, surface));
	}

	private static void repartitionComponent(
		DisplaySurface surface,
		Map<BlockPos, PhotoDisplayFrameBlockEntity> component
	) {
		Map<GridPoint, DisplayFrameState> previous = new HashMap<>();
		Map<String, FilmRecord> filmByMedia = new HashMap<>();
		Set<GridPoint> occupied = new HashSet<>();
		for (Map.Entry<BlockPos, PhotoDisplayFrameBlockEntity> entry : component.entrySet()) {
			GridPoint point = surface.project(entry.getKey());
			occupied.add(point);
			DisplayFrameState state = entry.getValue().getDisplayState();
			if (!state.isEmpty()) {
				previous.put(point, state);
				FilmRecord rec = entry.getValue().getFilmRecord();
				if (rec != null && !rec.isBlank()) {
					filmByMedia.put(state.mediaId(), rec);
				}
			}
		}

		applyStates(component, DisplayFrameStateLogic.repartition(occupied, previous), filmByMedia);
	}

	private static void applyStates(
		Map<BlockPos, PhotoDisplayFrameBlockEntity> frames,
		Map<GridPoint, DisplayFrameState> states,
		Map<String, FilmRecord> filmByMedia
	) {
		for (Map.Entry<BlockPos, PhotoDisplayFrameBlockEntity> entry : frames.entrySet()) {
			DisplaySurface surface = DisplaySurface.from(entry.getValue().getCachedState());
			GridPoint point = surface.project(entry.getKey());
			DisplayFrameState newState = states.getOrDefault(point, DisplayFrameState.empty());
			FilmRecord record = newState.isEmpty() ? null : filmByMedia.get(newState.mediaId());
			entry.getValue().setDisplayStateAndFilm(newState, record);
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
				if (frames.size() > MAX_CONNECTED_FRAMES) {
					return Map.of();
				}
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
