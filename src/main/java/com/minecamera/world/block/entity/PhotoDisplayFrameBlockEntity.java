package com.minecamera.world.block.entity;

import com.minecamera.display.DisplayFrameState;
import com.minecamera.display.DisplayFrameStateCodec;
import com.minecamera.registry.MineCameraBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

public final class PhotoDisplayFrameBlockEntity extends BlockEntity {
	private DisplayFrameState displayState = DisplayFrameState.empty();

	public PhotoDisplayFrameBlockEntity(BlockPos pos, BlockState blockState) {
		super(MineCameraBlockEntities.PHOTO_DISPLAY_FRAME, pos, blockState);
	}

	public DisplayFrameState getDisplayState() {
		return displayState;
	}

	public void setDisplayState(DisplayFrameState displayState) {
		this.displayState = displayState == null ? DisplayFrameState.empty() : displayState;
		markDirty();
		if (world != null) {
			world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
		}
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);
		DisplayFrameStateCodec.writeToView(view, displayState);
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);
		displayState = DisplayFrameStateCodec.readFromView(view);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
		return createNbt(registries);
	}
}
