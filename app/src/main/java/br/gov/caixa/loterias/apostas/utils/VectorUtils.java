package br.gov.caixa.loterias.apostas.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class VectorUtils {

	public static Drawable getShape(int drawable, int color) {
		Context  context = Aplicacao.application.getApplicationContext();
		Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, drawable);
		Drawable shape = DrawableCompat.wrap(unwrappedDrawable);
		DrawableCompat.setTint(shape, color);
		DrawableCompat.setTint(shape, ContextCompat.getColor(context, color));
		return shape;
	}

	public static PorterDuffColorFilter getColorFilter(int color) {
		return new PorterDuffColorFilter(color,
										  PorterDuff.Mode.SRC_ATOP);
	}
}
