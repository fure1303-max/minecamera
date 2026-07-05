package com.minecamera;

import com.minecamera.registry.MineCameraBlockEntities;
import com.minecamera.registry.MineCameraBlocks;
import com.minecamera.registry.MineCameraCreativeInventory;
import com.minecamera.registry.MineCameraItems;
import com.minecamera.network.MineCameraNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MineCameraMod implements ModInitializer {
	public static final String MOD_ID = "minecamera";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		MineCameraBlocks.initialize();
		MineCameraItems.initialize();
		MineCameraCreativeInventory.initialize();
		MineCameraBlockEntities.initialize();
		MineCameraNetworking.initialize();
		LOGGER.info("MineCamera initialized");
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
