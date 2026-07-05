package com.minecamera.client.render;

import com.minecamera.display.DisplaySurface;
import com.minecamera.world.block.entity.PhotoDisplayFrameBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class PhotoDisplayFrameBlockEntityRenderer implements BlockEntityRenderer<PhotoDisplayFrameBlockEntity, PhotoDisplayFrameBlockEntityRenderState> {
	private static final float PLANE_OFFSET = 1.0F / 16.0F + 0.001F;
	private static final PhotoDisplayTextureCache TEXTURE_CACHE = new PhotoDisplayTextureCache();

	public PhotoDisplayFrameBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
	}

	@Override
	public PhotoDisplayFrameBlockEntityRenderState createRenderState() {
		return new PhotoDisplayFrameBlockEntityRenderState();
	}

	@Override
	public void updateRenderState(
		PhotoDisplayFrameBlockEntity blockEntity,
		PhotoDisplayFrameBlockEntityRenderState state,
		float tickProgress,
		Vec3d cameraPos,
		ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay
	) {
		BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
		state.hasTexture = false;
		state.surface = DisplaySurface.from(blockEntity.getCachedState());
		TEXTURE_CACHE.resolve(MinecraftClient.getInstance(), blockEntity.getDisplayState()).ifPresent(texture -> {
			state.textureId = texture.textureId();
			state.renderLayer = texture.renderLayer();
			state.u0 = texture.slice().u0();
			state.v0 = texture.slice().v0();
			state.u1 = texture.slice().u1();
			state.v1 = texture.slice().v1();
			state.hasTexture = true;
		});
	}

	@Override
	public void render(
		PhotoDisplayFrameBlockEntityRenderState state,
		MatrixStack matrices,
		OrderedRenderCommandQueue queue,
		CameraRenderState cameraState
	) {
		if (!state.hasTexture || state.renderLayer == null || state.surface == null) {
			return;
		}

		queue.submitCustom(matrices, state.renderLayer, (entry, vertices) -> renderSurface(state, entry, vertices));
	}

	private static void renderSurface(PhotoDisplayFrameBlockEntityRenderState state, MatrixStack.Entry entry, VertexConsumer vertices) {
		DisplaySurface surface = state.surface;
		Direction right = surface.right();
		Direction down = surface.down();
		Direction normal = surface.normal();

		float planeX = planeCoordinate(normal.getOffsetX());
		float planeY = planeCoordinate(normal.getOffsetY());
		float planeZ = planeCoordinate(normal.getOffsetZ());

		float originX = normal.getAxis() == Direction.Axis.X ? planeX : axisOrigin(right.getOffsetX(), down.getOffsetX());
		float originY = normal.getAxis() == Direction.Axis.Y ? planeY : axisOrigin(right.getOffsetY(), down.getOffsetY());
		float originZ = normal.getAxis() == Direction.Axis.Z ? planeZ : axisOrigin(right.getOffsetZ(), down.getOffsetZ());

		float rightX = right.getOffsetX();
		float rightY = right.getOffsetY();
		float rightZ = right.getOffsetZ();
		float downX = down.getOffsetX();
		float downY = down.getOffsetY();
		float downZ = down.getOffsetZ();

		vertex(vertices, entry, originX, originY, originZ, state.u0, state.v0, state.lightmapCoordinates, normal);
		vertex(vertices, entry, originX + downX, originY + downY, originZ + downZ, state.u0, state.v1, state.lightmapCoordinates, normal);
		vertex(
			vertices,
			entry,
			originX + downX + rightX,
			originY + downY + rightY,
			originZ + downZ + rightZ,
			state.u1,
			state.v1,
			state.lightmapCoordinates,
			normal
		);
		vertex(vertices, entry, originX + rightX, originY + rightY, originZ + rightZ, state.u1, state.v0, state.lightmapCoordinates, normal);
	}

	private static float planeCoordinate(int normalComponent) {
		if (normalComponent > 0) {
			return PLANE_OFFSET;
		}
		if (normalComponent < 0) {
			return 1.0F - PLANE_OFFSET;
		}
		return 0.0F;
	}

	private static float axisOrigin(int rightComponent, int downComponent) {
		if (rightComponent < 0 || downComponent < 0) {
			return 1.0F;
		}
		return 0.0F;
	}

	private static void vertex(
		VertexConsumer vertices,
		MatrixStack.Entry entry,
		float x,
		float y,
		float z,
		float u,
		float v,
		int light,
		Direction normal
	) {
		vertices.vertex(entry, x, y, z)
			.color(255, 255, 255, 255)
			.texture(u, v)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(light)
			.normal(entry, normal.getOffsetX(), normal.getOffsetY(), normal.getOffsetZ());
	}
}
