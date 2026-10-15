package com.minecamera.world.block.entity;

import com.minecamera.display.DisplayFrameState;
import com.minecamera.display.DisplayFrameStateCodec;
import com.minecamera.registry.MineCameraBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class PhotoDisplayFrameBlockEntity extends BlockEntity {
	private DisplayFrameState displayState = DisplayFrameState.empty();

	public PhotoDisplayFrameBlockEntity(BlockPos pos, BlockState blockState) {
		super(MineCameraBlockEntities.PHOTO_DISPLAY_FRAME, pos, blockState);
	}

	public DisplayFrameState getDisplayState() {
		return displayState;
	}

	public void setDisplayState(DisplayFrameState displayState) {
		DisplayFrameState nextState = displayState == null ? DisplayFrameState.empty() : displayState;
		if (nextState.equals(this.displayState)) {
			return;
		}
		this.displayState = nextState;
		markDirty();
		if (world instanceof ServerWorld serverWorld) {
			serverWorld.getChunkManager().markForUpdate(pos);
		} else if (world != null) {
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

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}
}
