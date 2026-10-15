package com.minecamera.item;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public final class CameraClientHooks {
	public static Consumer<Hand> openHeldCamera = hand -> {};
	public static BiConsumer<Hand, BlockPos> openMountedCamera = (hand, pos) -> {};

	private CameraClientHooks() {
	}
}
