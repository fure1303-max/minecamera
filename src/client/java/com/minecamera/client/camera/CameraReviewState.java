package com.minecamera.client.camera;

import com.minecamera.film.FilmRecord;
import java.util.List;
import net.minecraft.text.Text;

record CameraReviewState(List<FilmRecord> films, String selectedMediaId, CameraReviewFrameState frameState) {
	static CameraReviewState empty(Text message) {
		return new CameraReviewState(List.of(), null, CameraReviewFrameState.empty(message));
	}

	boolean isEmpty() {
		return films.isEmpty();
	}
}
