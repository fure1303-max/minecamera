package com.minecamera.display;

import com.minecamera.logic.GridPoint;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public final class DisplayFrameStateCodec {
	static final String ROOT_KEY = "minecamera_display";
	private static final String MEDIA_ID_KEY = "media_id";
	private static final String ANCHOR_X_KEY = "anchor_x";
	private static final String ANCHOR_Y_KEY = "anchor_y";
	private static final String GROUP_WIDTH_KEY = "group_width";
	private static final String GROUP_HEIGHT_KEY = "group_height";
	private static final String SLICE_X_KEY = "slice_x";
	private static final String SLICE_Y_KEY = "slice_y";

	private DisplayFrameStateCodec() {
	}

	public static DisplayFrameState readFromCustomData(NbtCompound container) {
		if (container == null || !container.contains(ROOT_KEY)) {
			return DisplayFrameState.empty();
		}

		NbtCompound displayData = container.getCompound(ROOT_KEY).orElse(null);
		if (displayData == null) {
			return DisplayFrameState.empty();
		}

		String mediaId = displayData.getString(MEDIA_ID_KEY, "");
		int groupWidth = displayData.getInt(GROUP_WIDTH_KEY, 0);
		int groupHeight = displayData.getInt(GROUP_HEIGHT_KEY, 0);
		if (mediaId.isBlank() || groupWidth <= 0 || groupHeight <= 0) {
			return DisplayFrameState.empty();
		}

		return new DisplayFrameState(
			mediaId,
			new GridPoint(displayData.getInt(ANCHOR_X_KEY, 0), displayData.getInt(ANCHOR_Y_KEY, 0)),
			groupWidth,
			groupHeight,
			displayData.getInt(SLICE_X_KEY, 0),
			displayData.getInt(SLICE_Y_KEY, 0)
		);
	}

	public static NbtCompound writeToCustomData(NbtCompound existingData, DisplayFrameState state) {
		NbtCompound root = existingData == null ? new NbtCompound() : existingData.copy();
		if (state == null || state.isEmpty()) {
			root.remove(ROOT_KEY);
		} else {
			root.put(ROOT_KEY, toNbt(state));
		}
		return root.isEmpty() ? null : root;
	}

	public static DisplayFrameState readFromView(ReadView view) {
		if (view == null) {
			return DisplayFrameState.empty();
		}

		ReadView displayData = view.getOptionalReadView(ROOT_KEY).orElse(null);
		if (displayData == null) {
			return DisplayFrameState.empty();
		}

		String mediaId = displayData.getString(MEDIA_ID_KEY, "");
		int groupWidth = displayData.getInt(GROUP_WIDTH_KEY, 0);
		int groupHeight = displayData.getInt(GROUP_HEIGHT_KEY, 0);
		if (mediaId.isBlank() || groupWidth <= 0 || groupHeight <= 0) {
			return DisplayFrameState.empty();
		}

		return new DisplayFrameState(
			mediaId,
			new GridPoint(displayData.getInt(ANCHOR_X_KEY, 0), displayData.getInt(ANCHOR_Y_KEY, 0)),
			groupWidth,
			groupHeight,
			displayData.getInt(SLICE_X_KEY, 0),
			displayData.getInt(SLICE_Y_KEY, 0)
		);
	}

	public static void writeToView(WriteView view, DisplayFrameState state) {
		if (view == null) {
			return;
		}
		if (state == null || state.isEmpty()) {
			view.remove(ROOT_KEY);
			return;
		}

		WriteView displayData = view.get(ROOT_KEY);
		displayData.putString(MEDIA_ID_KEY, state.mediaId());
		displayData.putInt(ANCHOR_X_KEY, state.groupAnchor().x());
		displayData.putInt(ANCHOR_Y_KEY, state.groupAnchor().y());
		displayData.putInt(GROUP_WIDTH_KEY, state.groupWidth());
		displayData.putInt(GROUP_HEIGHT_KEY, state.groupHeight());
		displayData.putInt(SLICE_X_KEY, state.sliceX());
		displayData.putInt(SLICE_Y_KEY, state.sliceY());
	}

	private static NbtCompound toNbt(DisplayFrameState state) {
		NbtCompound displayData = new NbtCompound();
		displayData.putString(MEDIA_ID_KEY, state.mediaId());
		displayData.putInt(ANCHOR_X_KEY, state.groupAnchor().x());
		displayData.putInt(ANCHOR_Y_KEY, state.groupAnchor().y());
		displayData.putInt(GROUP_WIDTH_KEY, state.groupWidth());
		displayData.putInt(GROUP_HEIGHT_KEY, state.groupHeight());
		displayData.putInt(SLICE_X_KEY, state.sliceX());
		displayData.putInt(SLICE_Y_KEY, state.sliceY());
		return displayData;
	}
}
