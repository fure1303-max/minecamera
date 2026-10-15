package com.minecamera.item;

import com.minecamera.logic.CameraTripodTargetingAction;
import com.minecamera.logic.CameraTripodTargetingPolicy;
import com.minecamera.world.block.TripodBlock;
import java.lang.reflect.Method;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public final class CameraItem extends Item {
	public CameraItem(Settings properties) {
		super(properties);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockState state = world.getBlockState(context.getBlockPos());
		boolean targetingTripod = state.getBlock() instanceof TripodBlock;
		boolean tripodHasCamera = targetingTripod && state.get(TripodBlock.HAS_CAMERA);
		PlayerEntity player = context.getPlayer();
		boolean sneaking = player != null && player.isSneaking();

		CameraTripodTargetingAction action = CameraTripodTargetingPolicy.decide(targetingTripod, tripodHasCamera, sneaking);
		return switch (action) {
			case PASS -> ActionResult.PASS;
			case BLOCK_HANDHELD_UI -> ActionResult.SUCCESS;
			case MOUNT_CAMERA -> mountOnTripod(world, context, player, state);
			case OPEN_TRIPOD_UI -> openTripodScreen(world, context.getHand(), context.getBlockPos());
		};
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (world.isClient()) {
			openClientScreen(hand);
		}
		return ActionResult.SUCCESS;
	}

	private static void openClientScreen(Hand hand) {
		try {
			Class<?> opener = Class.forName("com.minecamera.client.ui.MineCameraScreenOpener");
			Method method = opener.getMethod("openHeldCamera", Hand.class);
			method.invoke(null, hand);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException("Unable to open MineCamera screen", exception);
		}
	}

	private static ActionResult mountOnTripod(World world, ItemUsageContext context, PlayerEntity player, BlockState state) {
		if (!world.isClient()) {
			world.setBlockState(context.getBlockPos(), state.with(TripodBlock.HAS_CAMERA, true));
			if (player != null && !player.isCreative()) {
				ItemStack stack = context.getStack();
				stack.decrement(1);
			}
		}
		return ActionResult.SUCCESS;
	}

	private static ActionResult openTripodScreen(World world, Hand hand, BlockPos pos) {
		if (world.isClient()) {
			openMountedClientScreen(hand, pos);
		}
		return ActionResult.SUCCESS;
	}

	private static void openMountedClientScreen(Hand hand, BlockPos pos) {
		try {
			Class<?> opener = Class.forName("com.minecamera.client.ui.MineCameraScreenOpener");
			Method method = opener.getMethod("openMountedCamera", Hand.class, BlockPos.class);
			method.invoke(null, hand, pos);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException("Unable to open tripod camera screen", exception);
		}
	}
}
