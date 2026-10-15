package com.minecamera.film;

import com.minecamera.item.FilmItem;
import com.minecamera.logic.FocusMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public final class FilmService {
	static final String ROOT_KEY = "minecamera_film";
	private static final String STATE_KEY = "state";
	private static final String CONTENT_TYPE_KEY = "content_type";
	private static final String MEDIA_ID_KEY = "media_id";
	private static final String AUTHOR_KEY = "author";
	private static final String CAPTURED_AT_KEY = "captured_at";
	private static final String CAPTURE_SOURCE_KEY = "capture_source";
	private static final String SETTINGS_KEY = "settings";
	private static final String WIDTH_KEY = "width";
	private static final String HEIGHT_KEY = "height";
	private static final String FOCAL_LENGTH_KEY = "focal_length";
	private static final String APERTURE_KEY = "aperture";
	private static final String FOCUS_MODE_KEY = "focus_mode";

	private FilmService() {
	}

	public static FilmRecord read(ItemStack stack) {
		if (!isFilm(stack)) {
			return FilmRecord.blank();
		}

		return readFromCustomData(readRootCompound(stack));
	}

	public static void write(ItemStack stack, FilmRecord record) {
		if (!isFilm(stack)) {
			throw new IllegalArgumentException("MineCamera film data can only be stored on FilmItem stacks");
		}

		NbtCompound root = writeToCustomData(readRootCompound(stack), record);
		if (root == null || root.isEmpty()) {
			stack.remove(DataComponentTypes.CUSTOM_DATA);
		} else {
			stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(root));
		}
	}

	public static boolean isBlank(ItemStack stack) {
		return read(stack).isBlank();
	}

	public static boolean isExposed(ItemStack stack) {
		return !isBlank(stack);
	}

	public static ItemStack createExposedFilm(ItemStack blankFilmStack, FilmRecord record) {
		if (!isFilm(blankFilmStack)) {
			throw new IllegalArgumentException("Expected a FilmItem stack");
		}
		if (!isBlank(blankFilmStack)) {
			throw new IllegalArgumentException("Expected a blank film stack");
		}
		if (record.isBlank()) {
			throw new IllegalArgumentException("Expected exposed film data");
		}

		if (!requiresSeparateResultStack(blankFilmStack.getCount())) {
			write(blankFilmStack, record);
			return blankFilmStack;
		}

		ItemStack exposedStack = blankFilmStack.split(1);
		write(exposedStack, record);
		return exposedStack;
	}

	public static boolean isFilm(ItemStack stack) {
		return stack.getItem() instanceof FilmItem;
	}

	static FilmRecord readFromCustomData(NbtCompound container) {
		if (container == null || !container.contains(ROOT_KEY)) {
			return FilmRecord.blank();
		}

		NbtCompound filmData = container.getCompound(ROOT_KEY).orElse(null);
		if (filmData == null) {
			return FilmRecord.blank();
		}

		FilmState state = FilmState.fromId(filmData.getString(STATE_KEY, FilmState.BLANK.id()));
		if (state == FilmState.BLANK) {
			return FilmRecord.blank();
		}

		FilmContentType contentType = FilmContentType.fromId(filmData.getString(CONTENT_TYPE_KEY, FilmContentType.PHOTO.id()));
		String mediaId = filmData.getString(MEDIA_ID_KEY, "");
		String author = filmData.getString(AUTHOR_KEY, "");
		long capturedAt = filmData.getLong(CAPTURED_AT_KEY, 0L);
		String captureSource = filmData.getString(CAPTURE_SOURCE_KEY, "");
		CaptureSettings captureSettings = readSettings(filmData);

		return FilmRecord.exposed(
			contentType,
			mediaId.isBlank() ? null : mediaId,
			captureSettings,
			author.isBlank() ? null : author,
			capturedAt,
			captureSource.isBlank() ? null : captureSource
		);
	}

	static NbtCompound writeToCustomData(NbtCompound existingData, FilmRecord record) {
		NbtCompound root = existingData == null ? new NbtCompound() : existingData.copy();
		if (record.isBlank()) {
			root.remove(ROOT_KEY);
		} else {
			root.put(ROOT_KEY, toNbt(record));
		}
		return root.isEmpty() ? null : root;
	}

	public static boolean requiresSeparateResultStack(int blankFilmCount) {
		return blankFilmCount > 1;
	}

	private static CaptureSettings readSettings(NbtCompound filmData) {
		if (!filmData.contains(SETTINGS_KEY)) {
			return null;
		}

		NbtCompound settings = filmData.getCompound(SETTINGS_KEY).orElse(null);
		if (settings == null) {
			return null;
		}
		return new CaptureSettings(
			settings.getInt(WIDTH_KEY, 0),
			settings.getInt(HEIGHT_KEY, 0),
			settings.getInt(FOCAL_LENGTH_KEY, 50),
			settings.getDouble(APERTURE_KEY, 2.8D),
			FocusMode.valueOf(settings.getString(FOCUS_MODE_KEY, FocusMode.AUTO.name()))
		);
	}

	private static NbtCompound toNbt(FilmRecord record) {
		NbtCompound filmData = new NbtCompound();
		filmData.putString(STATE_KEY, record.state().id());
		filmData.putString(CONTENT_TYPE_KEY, record.contentType().id());
		putIfPresent(filmData, MEDIA_ID_KEY, record.mediaId());
		putIfPresent(filmData, AUTHOR_KEY, record.author());
		putIfPresent(filmData, CAPTURE_SOURCE_KEY, record.captureSource());
		filmData.putLong(CAPTURED_AT_KEY, record.capturedAtEpochSeconds());

		if (record.captureSettings() != null) {
			NbtCompound settings = new NbtCompound();
			settings.putInt(WIDTH_KEY, record.captureSettings().width());
			settings.putInt(HEIGHT_KEY, record.captureSettings().height());
			settings.putInt(FOCAL_LENGTH_KEY, record.captureSettings().focalLength());
			settings.putDouble(APERTURE_KEY, record.captureSettings().aperture());
			settings.putString(FOCUS_MODE_KEY, record.captureSettings().focusMode().name());
			filmData.put(SETTINGS_KEY, settings);
		}

		return filmData;
	}

	private static NbtCompound readRootCompound(ItemStack stack) {
		NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
		return customData == null ? null : customData.copyNbt();
	}

	private static void putIfPresent(NbtCompound compound, String key, String value) {
		if (value != null && !value.isBlank()) {
			compound.putString(key, value);
		}
	}
}
