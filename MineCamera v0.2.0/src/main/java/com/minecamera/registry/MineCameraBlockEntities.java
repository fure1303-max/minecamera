package com.minecamera.registry;

import com.minecamera.MineCameraMod;
import com.minecamera.world.block.entity.PhotoDisplayFrameBlockEntity;
import com.minecamera.world.block.entity.TripodBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class MineCameraBlockEntities {
	public static final BlockEntityType<TripodBlockEntity> TRIPOD = Registry.register(
		Registries.BLOCK_ENTITY_TYPE,
		MineCameraMod.id("tripod"),
		FabricBlockEntityTypeBuilder.create(TripodBlockEntity::new, MineCameraBlocks.TRIPOD).build()
	);

	public static final BlockEntityType<PhotoDisplayFrameBlockEntity> PHOTO_DISPLAY_FRAME = Registry.register(
		Registries.BLOCK_ENTITY_TYPE,
		MineCameraMod.id("photo_display_frame"),
		FabricBlockEntityTypeBuilder.create(PhotoDisplayFrameBlockEntity::new, MineCameraBlocks.PHOTO_DISPLAY_FRAME).build()
	);

	private MineCameraBlockEntities() {
	}

	public static void initialize() {
	}
}
