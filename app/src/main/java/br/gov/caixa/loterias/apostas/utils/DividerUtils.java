package br.gov.caixa.loterias.apostas.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import br.gov.caixa.loterias.apostas.R;

public final class DividerUtils {

    private DividerUtils() {}

    /**
     * Torna a linha visível (alpha 255) ou "invisível" (alpha 0).
     */
    public static void setDividerVisible(View layoutItem, boolean visible) {
        if (layoutItem == null) return;

        Drawable bg = layoutItem.getBackground();
        if (!(bg instanceof LayerDrawable)) return;

        LayerDrawable layers = (LayerDrawable) bg.mutate();
        Drawable divider = layers.findDrawableByLayerId(R.id.layer_divider);
        if (divider == null) return;

        divider.setAlpha(visible ? 255 : 0);

        layoutItem.invalidate();
    }
}