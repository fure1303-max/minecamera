package com.minecamera.registry;

import com.minecamera.MineCameraMod;
import com.minecamera.world.block.PhotoDisplayFrameBlock;
import com.minecamera.world.block.TripodBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class MineCameraBlocks {
	private static final RegistryKey<Block> TRIPOD_KEY = RegistryKey.of(RegistryKeys.BLOCK, MineCameraMod.id("tripod"));
	private static final RegistryKey<Block> PHOTO_DISPLAY_FRAME_KEY = RegistryKey.of(
		RegistryKeys.BLOCK,
		MineCameraMod.id("photo_display_frame")
	);

	public static final Block TRIPOD = Registry.register(
		Registries.BLOCK,
		TRIPOD_KEY,
		new TripodBlock(tripodSettings())
	);

	public static final Block PHOTO_DISPLAY_FRAME = Registry.register(
		Registries.BLOCK,
		PHOTO_DISPLAY_FRAME_KEY,
		new PhotoDisplayFrameBlock(photoDisplayFrameSettings())
	);

	private MineCameraBlocks() {
	}

	static AbstractBlock.Settings tripodSettings() {
		return AbstractBlock.Settings.create().registryKey(TRIPOD_KEY).mapColor(MapColor.IRON_GRAY).strength(1.5F).nonOpaque();
	}

	static AbstractBlock.Settings photoDisplayFrameSettings() {
		return AbstractBlock.Settings.create().registryKey(PHOTO_DISPLAY_FRAME_KEY).mapColor(MapColor.OAK_TAN).strength(1.0F).nonOpaque();
	}

	public static void initialize() {
	}
}
