package com.minecamera.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

import java.util.List;

public final class MineCameraCreativeInventory {
	private static final List<String> TOOLS_TAB_ITEM_IDS = List.of(
		"camera",
		"film",
		"tripod",
		"photo_display_frame"
	);

	private MineCameraCreativeInventory() {
	}

	static List<String> toolsTabItemIds() {
		return TOOLS_TAB_ITEM_IDS;
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
			entries.add(MineCameraItems.CAMERA);
			entries.add(MineCameraItems.FILM);
			entries.add(MineCameraItems.TRIPOD);
			entries.add(MineCameraItems.PHOTO_DISPLAY_FRAME);
		});
	}
}
