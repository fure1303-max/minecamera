package com.minecamera.item;

import com.minecamera.film.FilmRecord;
import com.minecamera.film.FilmService;
import java.util.function.Consumer;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class FilmItem extends Item {
	public FilmItem(Settings properties) {
		super(properties);
	}

	@Override
	public void appendTooltip(
		ItemStack stack,
		TooltipContext context,
		TooltipDisplayComponent displayComponent,
		Consumer<Text> textConsumer,
		TooltipType type
	) {
		FilmRecord record = FilmService.read(stack);
		if (record.isBlank()) {
			textConsumer.accept(Text.translatable("tooltip.minecamera.film.blank").formatted(Formatting.GRAY));
			return;
		}

		textConsumer.accept(Text.translatable("tooltip.minecamera.film.exposed").formatted(Formatting.GOLD));
		if (record.captureSettings() != null) {
			textConsumer.accept(
				Text.literal(record.captureSettings().width() + "x" + record.captureSettings().height())
					.formatted(Formatting.GRAY)
			);
		}
		if (record.mediaId() != null && !record.mediaId().isBlank()) {
			textConsumer.accept(Text.literal("ID: " + shortMediaId(record.mediaId())).formatted(Formatting.DARK_GRAY));
		}
	}

	private static String shortMediaId(String mediaId) {
		return mediaId.length() <= 8 ? mediaId : mediaId.substring(0, 8);
	}
}
