package com.minecamera.world.block.entity;

import com.minecamera.display.DisplayFrameState;
import com.minecamera.display.DisplayFrameStateCodec;
import com.minecamera.film.FilmRecord;
import com.minecamera.film.FilmService;
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

import java.util.Objects;

public final class PhotoDisplayFrameBlockEntity extends BlockEntity {
	private DisplayFrameState displayState = DisplayFrameState.empty();
	private @Nullable FilmRecord filmRecord;

	public PhotoDisplayFrameBlockEntity(BlockPos pos, BlockState blockState) {
		super(MineCameraBlockEntities.PHOTO_DISPLAY_FRAME, pos, blockState);
	}

	public DisplayFrameState getDisplayState() {
		return displayState;
	}

	public void setDisplayState(DisplayFrameState displayState) {
		setDisplayStateAndFilm(displayState, this.filmRecord);
	}

	public @Nullable FilmRecord getFilmRecord() {
		return filmRecord;
	}

	public void setFilmRecord(@Nullable FilmRecord filmRecord) {
		this.filmRecord = filmRecord;
		markDirty();
	}

	public void setDisplayStateAndFilm(DisplayFrameState displayState, @Nullable FilmRecord filmRecord) {
		DisplayFrameState nextState = displayState == null ? DisplayFrameState.empty() : displayState;
		FilmRecord nextFilm = nextState.isEmpty() ? null : filmRecord;
		boolean stateChanged = !nextState.equals(this.displayState);
		boolean filmChanged = !Objects.equals(nextFilm, this.filmRecord);
		if (!stateChanged && !filmChanged) {
			return;
		}

		this.displayState = nextState;
		this.filmRecord = nextFilm;
		markDirty();
		if (stateChanged) {
			if (world instanceof ServerWorld serverWorld) {
				serverWorld.getChunkManager().markForUpdate(pos);
			} else if (world != null) {
				world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
			}
		}
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);
		DisplayFrameStateCodec.writeToView(view, displayState);
		FilmService.writeToView(view, filmRecord);
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);
		displayState = DisplayFrameStateCodec.readFromView(view);
		filmRecord = FilmService.readFromView(view);
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
