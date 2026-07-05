package com.minecamera.world.block;

import com.minecamera.world.block.entity.TripodBlockEntity;
import com.minecamera.logic.TripodInteractionAction;
import com.minecamera.logic.TripodInteractionLogic;
import com.minecamera.registry.MineCameraItems;
import java.lang.reflect.Method;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

public final class TripodBlock extends BlockWithEntity implements BlockEntityProvider {
	public static final MapCodec<TripodBlock> CODEC = createCodec(TripodBlock::new);
	public static final BooleanProperty HAS_CAMERA = Properties.ENABLED;
	private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.2D, 0.0D, 0.2D, 0.8D, 1.0D, 0.8D);

	public TripodBlock(Settings properties) {
		super(properties);
		setDefaultState(getStateManager().getDefaultState().with(HAS_CAMERA, false));
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
		builder.add(HAS_CAMERA);
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
		TripodInteractionAction action = TripodInteractionLogic.decide(
			state.get(HAS_CAMERA),
			player.isSneaking(),
			stack.isOf(MineCameraItems.CAMERA),
			stack.isEmpty()
		);
		return handleAction(action, state, world, pos, player, hand, stack);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		TripodInteractionAction action = TripodInteractionLogic.decide(
			state.get(HAS_CAMERA),
			player.isSneaking(),
			false,
			true
		);
		return handleAction(action, state, world, pos, player, Hand.MAIN_HAND, ItemStack.EMPTY);
	}

	@Override
	protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
		super.onStateReplaced(state, world, pos, moved);
		if (!moved && state.get(HAS_CAMERA)) {
			dropStack(world, pos, new ItemStack(MineCameraItems.CAMERA));
		}
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new TripodBlockEntity(pos, state);
	}

	private static ActionResult handleAction(
		TripodInteractionAction action,
		BlockState state,
		World world,
		BlockPos pos,
		PlayerEntity player,
		Hand hand,
		ItemStack heldStack
	) {
		return switch (action) {
			case MOUNT_CAMERA -> mountCamera(state, world, pos, player, heldStack);
			case DISMOUNT_CAMERA -> dismountCamera(state, world, pos, player);
			case OPEN_CAMERA_UI -> openTripodCamera(world, hand, pos);
			case PASS -> ActionResult.PASS;
		};
	}

	private static ActionResult mountCamera(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack heldStack) {
		if (!world.isClient()) {
			world.setBlockState(pos, state.with(HAS_CAMERA, true));
			if (!player.isCreative()) {
				heldStack.decrement(1);
			}
		}
		return ActionResult.SUCCESS;
	}

	private static ActionResult dismountCamera(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClient()) {
			world.setBlockState(pos, state.with(HAS_CAMERA, false));
			ItemStack cameraStack = new ItemStack(MineCameraItems.CAMERA);
			if (!player.getInventory().insertStack(cameraStack)) {
				dropStack(world, pos, cameraStack);
			}
		}
		return ActionResult.SUCCESS;
	}

	private static ActionResult openTripodCamera(World world, Hand hand, BlockPos pos) {
		if (world.isClient()) {
			try {
				Class<?> opener = Class.forName("com.minecamera.client.ui.MineCameraScreenOpener");
				Method method = opener.getMethod("openMountedCamera", Hand.class, BlockPos.class);
				method.invoke(null, hand, pos);
			} catch (ReflectiveOperationException exception) {
				throw new IllegalStateException("Unable to open tripod camera screen", exception);
			}
		}
		return ActionResult.SUCCESS;
	}
}
