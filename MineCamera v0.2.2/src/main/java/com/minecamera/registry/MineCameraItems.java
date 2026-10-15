package com.minecamera.registry;

import com.minecamera.MineCameraMod;
import com.minecamera.item.CameraItem;
import com.minecamera.item.FilmItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class MineCameraItems {
	private static final RegistryKey<Item> CAMERA_KEY = RegistryKey.of(RegistryKeys.ITEM, MineCameraMod.id("camera"));
	private static final RegistryKey<Item> FILM_KEY = RegistryKey.of(RegistryKeys.ITEM, MineCameraMod.id("film"));
	private static final RegistryKey<Item> TRIPOD_KEY = RegistryKey.of(RegistryKeys.ITEM, MineCameraMod.id("tripod"));
	private static final RegistryKey<Item> PHOTO_DISPLAY_FRAME_KEY = RegistryKey.of(
		RegistryKeys.ITEM,
		MineCameraMod.id("photo_display_frame")
	);

	public static final Item CAMERA = Registry.register(
		Registries.ITEM,
		CAMERA_KEY,
		new CameraItem(cameraSettings())
	);

	public static final Item FILM = Registry.register(
		Registries.ITEM,
		FILM_KEY,
		new FilmItem(filmSettings())
	);

	public static final Item TRIPOD = Registry.register(
		Registries.ITEM,
		TRIPOD_KEY,
		new BlockItem(MineCameraBlocks.TRIPOD, tripodSettings())
	);

	public static final Item PHOTO_DISPLAY_FRAME = Registry.register(
		Registries.ITEM,
		PHOTO_DISPLAY_FRAME_KEY,
		new BlockItem(MineCameraBlocks.PHOTO_DISPLAY_FRAME, photoDisplayFrameSettings())
	);

	private MineCameraItems() {
	}

	static Item.Settings cameraSettings() {
		return new Item.Settings().registryKey(CAMERA_KEY).maxCount(1);
	}

	static Item.Settings filmSettings() {
		return new Item.Settings().registryKey(FILM_KEY).maxCount(16);
	}

	static Item.Settings tripodSettings() {
		return new Item.Settings().registryKey(TRIPOD_KEY);
	}

	static Item.Settings photoDisplayFrameSettings() {
		return new Item.Settings().registryKey(PHOTO_DISPLAY_FRAME_KEY);
	}

	public static void initialize() {
	}
}
