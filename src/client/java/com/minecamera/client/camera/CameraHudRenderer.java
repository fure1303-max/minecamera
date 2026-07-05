package com.minecamera.client.camera;

import com.minecamera.logic.CameraHudCornerGeometry;
import com.minecamera.logic.CameraModeHudLayout;
import com.minecamera.logic.CameraModeSession;
import com.minecamera.logic.CameraOpticalState;
import com.minecamera.logic.CameraPreviewMode;
import com.minecamera.logic.CameraShaderCompatibilityPolicy;
import com.minecamera.logic.CaptureFraming;
import com.minecamera.logic.ReviewTextureDrawPlan;
import com.minecamera.logic.ResolutionPreset;
import com.minecamera.logic.ShaderCompatibilityState;
import com.minecamera.logic.ViewfinderSurfacePalette;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

final class CameraHudRenderer {
	private static final RenderPipeline PHOTO_PIPELINE = RenderPipelines.GUI_TEXTURED;
	private static final int HUD_MARGIN_X = 28;
	private static final int HUD_MARGIN_BOTTOM = 54;
	private static final int VIEWFINDER_PADDING = 14;
	private static final int STATUS_HEIGHT = 22;

	void render(
		DrawContext context,
		MinecraftClient client,
		CameraModeSession session,
		CameraOpticalState opticalState,
		CameraReviewState reviewState,
		MineCameraViewfinderService viewfinder,
		Text status,
		ShaderCompatibilityState shaderState,
		int blankFilmCount
	) {
		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();
		ResolutionPreset preset = session.currentPreset();
		CameraModeHudLayout layout = CameraModeHudLayout.create(screenWidth, screenHeight, preset.width(), preset.height());
		int frameX = layout.frameX();
		int frameY = layout.frameY();
		int frameWidth = layout.frameWidth();
		int frameHeight = layout.frameHeight();

		context.drawTextWithShadow(client.textRenderer, text("hud.title"), HUD_MARGIN_X, 10, 0xFFF4F6FA);
		context.drawTextWithShadow(client.textRenderer, headerLineText(session, preset, opticalState, reviewState), HUD_MARGIN_X, 24, 0xFFF2D6A2);
		drawShaderIndicator(context, client, screenWidth, shaderState);
		context.drawTextWithShadow(client.textRenderer, controlHintText(session.previewMode()), HUD_MARGIN_X, screenHeight - 18, 0xFFE0E6EE);

		drawOutsideMask(context, screenWidth, screenHeight, frameX, frameY, frameWidth, frameHeight);

		int insetX = frameX + VIEWFINDER_PADDING;
		int insetY = frameY + VIEWFINDER_PADDING;
		int insetWidth = Math.max(1, frameWidth - (VIEWFINDER_PADDING * 2));
		int insetHeight = Math.max(1, frameHeight - (VIEWFINDER_PADDING * 2));
		if (session.previewMode() == CameraPreviewMode.LIVE) {
			CaptureFraming.FrameRect livePreviewRect = CaptureFraming.viewfinderFor(insetWidth, insetHeight, preset.width(), preset.height());
			drawLiveViewfinder(
				context,
				client,
				viewfinder,
				insetX + livePreviewRect.x(),
				insetY + livePreviewRect.y(),
				livePreviewRect.width(),
				livePreviewRect.height()
			);
		} else {
			drawReviewPreview(context, client, reviewState, insetX, insetY, insetWidth, insetHeight);
		}

		context.drawStrokedRectangle(frameX, frameY, frameWidth, frameHeight, 0xFFF6E8B5);
		drawCorner(context, CameraHudCornerGeometry.topLeft(frameX, frameY, 10, 2));
		drawCorner(context, CameraHudCornerGeometry.topRight(frameX + frameWidth - 2, frameY, 10, 2));
		drawCorner(context, CameraHudCornerGeometry.bottomLeft(frameX, frameY + frameHeight - 2, 10, 2));
		drawCorner(context, CameraHudCornerGeometry.bottomRight(frameX + frameWidth - 2, frameY + frameHeight - 2, 10, 2));

		context.fill(
			HUD_MARGIN_X,
			screenHeight - HUD_MARGIN_BOTTOM,
			screenWidth - HUD_MARGIN_X,
			screenHeight - HUD_MARGIN_BOTTOM + STATUS_HEIGHT,
			0xB9181E24
		);
		context.drawTextWithShadow(client.textRenderer, status, HUD_MARGIN_X + 8, screenHeight - HUD_MARGIN_BOTTOM + 7, 0xFFF7C57D);

		int helperTextY = Math.min(screenHeight - HUD_MARGIN_BOTTOM - 8, frameY + frameHeight + 8);
		if (session.previewMode() == CameraPreviewMode.LIVE) {
			context.drawTextWithShadow(
				client.textRenderer,
				text("hud.helper.blank_film", blankFilmCount),
				insetX,
				helperTextY,
				0xFFF1F4F8
			);
			context.drawTextWithShadow(
				client.textRenderer,
				text("hud.helper.final_crop"),
				insetX + 130,
				helperTextY,
				0xFFD9DEE5
			);
		} else {
			context.drawTextWithShadow(
				client.textRenderer,
				reviewFooterText(reviewState),
				insetX,
				helperTextY,
				0xFFF1F4F8
			);
		}
	}

	private static Text headerLineText(
		CameraModeSession session,
		ResolutionPreset preset,
		CameraOpticalState opticalState,
		CameraReviewState reviewState
	) {
		if (session.previewMode() == CameraPreviewMode.LIVE) {
			return text("hud.live_info", captureSourceText(session.source()), preset.label(), preset.width(), preset.height(), opticalState.focalLengthMm());
		}
		return text("hud.review_info", reviewState.frameState().index(), reviewState.frameState().total());
	}

	private static Text controlHintText(CameraPreviewMode previewMode) {
		return previewMode == CameraPreviewMode.LIVE
			? text("hud.controls.live")
			: text("hud.controls.review");
	}

	private static Text reviewFooterText(CameraReviewState reviewState) {
		if (reviewState.frameState().total() <= 0) {
			return reviewState.frameState().message();
		}
		return text(
			"preview.review_footer",
			reviewState.frameState().index(),
			reviewState.frameState().total(),
			reviewState.frameState().message()
		);
	}

	private static void drawShaderIndicator(DrawContext context, MinecraftClient client, int screenWidth, ShaderCompatibilityState shaderState) {
		if (!CameraShaderCompatibilityPolicy.shouldShowShaderHudIndicator(shaderState)) {
			return;
		}
		Text shaderText = text("hud.shader_active");
		int textWidth = client.textRenderer.getWidth(shaderText);
		context.drawTextWithShadow(client.textRenderer, shaderText, screenWidth - HUD_MARGIN_X - textWidth, 10, 0xFFB4D2FF);
	}

	private static void drawLiveViewfinder(
		DrawContext context,
		MinecraftClient client,
		MineCameraViewfinderService viewfinder,
		int x,
		int y,
		int width,
		int height
	) {
		viewfinder.drawPreview(context, x, y, width, height);
		if (!viewfinder.hasPreview()) {
			context.drawCenteredTextWithShadow(
				client.textRenderer,
				text("hud.loading"),
				x + (width / 2),
				y + (height / 2) - 4,
				0xFFE7E2D8
			);
		}
	}

	private static void drawReviewPreview(DrawContext context, MinecraftClient client, CameraReviewState reviewState, int insetX, int insetY, int insetWidth, int insetHeight) {
		context.fill(insetX, insetY, insetX + insetWidth, insetY + insetHeight, ViewfinderSurfacePalette.panelBackgroundColor());
		CameraReviewFrameState frameState = reviewState.frameState();
		if (frameState.texture() == null) {
			context.drawCenteredTextWithShadow(
				client.textRenderer,
				frameState.message(),
				insetX + (insetWidth / 2),
				insetY + (insetHeight / 2) - 4,
				0xFFE7E2D8
			);
			return;
		}

		ReviewTextureDrawPlan reviewPlan = ReviewTextureDrawPlan.create(
			insetX,
			insetY,
			insetWidth,
			insetHeight,
			frameState.texture().width(),
			frameState.texture().height()
		);
		context.drawTexture(
			PHOTO_PIPELINE,
			frameState.texture().textureId(),
			reviewPlan.drawX(),
			reviewPlan.drawY(),
			0.0F,
			0.0F,
			reviewPlan.drawWidth(),
			reviewPlan.drawHeight(),
			reviewPlan.regionWidth(),
			reviewPlan.regionHeight(),
			reviewPlan.textureWidth(),
			reviewPlan.textureHeight()
		);
	}

	private static void drawOutsideMask(DrawContext context, int screenWidth, int screenHeight, int frameX, int frameY, int frameWidth, int frameHeight) {
		int frameRight = frameX + frameWidth;
		int frameBottom = frameY + frameHeight;
		int maskColor = 0x5510151A;
		if (frameY > 0) {
			context.fill(0, 0, screenWidth, frameY, maskColor);
		}
		if (frameBottom < screenHeight) {
			context.fill(0, frameBottom, screenWidth, screenHeight, maskColor);
		}
		if (frameX > 0) {
			context.fill(0, frameY, frameX, frameBottom, maskColor);
		}
		if (frameRight < screenWidth) {
			context.fill(frameRight, frameY, screenWidth, frameBottom, maskColor);
		}
	}

	private static void drawCorner(DrawContext context, CameraHudCornerGeometry geometry) {
		CameraHudCornerGeometry.Rect horizontal = geometry.horizontal();
		CameraHudCornerGeometry.Rect vertical = geometry.vertical();
		context.fill(horizontal.x1(), horizontal.y1(), horizontal.x2(), horizontal.y2(), 0xFFF6E8B5);
		context.fill(vertical.x1(), vertical.y1(), vertical.x2(), vertical.y2(), 0xFFF6E8B5);
	}

	static int countBlankFilms(PlayerEntity player) {
		if (player == null) {
			return 0;
		}
		int count = 0;
		for (int slot = 0; slot < player.getInventory().size(); slot++) {
			if (player.getInventory().getStack(slot).isEmpty()) {
				continue;
			}
			if (com.minecamera.film.FilmService.isFilm(player.getInventory().getStack(slot))
				&& com.minecamera.film.FilmService.isBlank(player.getInventory().getStack(slot))) {
				count += player.getInventory().getStack(slot).getCount();
			}
		}
		return count;
	}

	private static MutableText captureSourceText(String source) {
		return switch (source) {
			case "tripod" -> text("source.tripod");
			default -> text("source.handheld");
		};
	}

	private static MutableText text(String key, Object... args) {
		return Text.translatable("screen.minecamera." + key, args);
	}
}
