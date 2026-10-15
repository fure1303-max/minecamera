package com.minecamera.world.block.entity;

import com.minecamera.logic.CameraPoseResolver;
import com.minecamera.registry.MineCameraBlockEntities;
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

public final class TripodBlockEntity extends BlockEntity {
	private static final String CAMERA_YAW_KEY = "camera_yaw";
	private static final String CAMERA_PITCH_KEY = "camera_pitch";
	private static final String CAMERA_POSE_INITIALIZED_KEY = "camera_pose_initialized";

	private float cameraYaw;
	private float cameraPitch;
	private boolean cameraPoseInitialized;

	public TripodBlockEntity(BlockPos pos, BlockState blockState) {
		super(MineCameraBlockEntities.TRIPOD, pos, blockState);
	}

	public boolean hasCameraPose() {
		return cameraPoseInitialized;
	}

	public float cameraYaw() {
		return cameraYaw;
	}

	public float cameraPitch() {
		return cameraPitch;
	}

	public boolean setCameraPose(float yaw, float pitch) {
		float clampedPitch = CameraPoseResolver.clampPitch(pitch);
		if (cameraPoseInitialized && Float.compare(cameraYaw, yaw) == 0 && Float.compare(cameraPitch, clampedPitch) == 0) {
			return false;
		}

		cameraYaw = yaw;
		cameraPitch = clampedPitch;
		cameraPoseInitialized = true;
		markDirty();
		return true;
	}

	public void markUpdated() {
		markDirty();
		if (world instanceof ServerWorld serverWorld) {
			serverWorld.getChunkManager().markForUpdate(pos);
		}
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);
		view.putFloat(CAMERA_YAW_KEY, cameraYaw);
		view.putFloat(CAMERA_PITCH_KEY, cameraPitch);
		view.putBoolean(CAMERA_POSE_INITIALIZED_KEY, cameraPoseInitialized);
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);
		cameraYaw = view.getFloat(CAMERA_YAW_KEY, 0.0F);
		cameraPitch = CameraPoseResolver.clampPitch(view.getFloat(CAMERA_PITCH_KEY, 0.0F));
		cameraPoseInitialized = view.getBoolean(CAMERA_POSE_INITIALIZED_KEY, false);
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
