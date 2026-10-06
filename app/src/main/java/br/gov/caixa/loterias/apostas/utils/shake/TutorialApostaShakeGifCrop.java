package br.gov.caixa.loterias.apostas.utils.shake;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Uniformiza a área dos GIFs sem distorcer a proporção dos quadros.
 * Os GIFs de 412 x 804 têm margens extras; os de 300 x 580 já estão ajustados.
 */
public final class TutorialApostaShakeGifCrop extends BitmapTransformation {
    private static final String ID = "br.gov.caixa.loterias.apostas.TutorialApostaShakeGifCrop.v2";
    @Override protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap source, int outWidth, int outHeight) {
        boolean comMargens = source.getWidth() == 412 && source.getHeight() == 804;
        int left = comMargens ? 53 : 0;
        int top = comMargens ? 119 : 0;
        int right = comMargens ? 359 : source.getWidth();
        int bottom = comMargens ? 699 : source.getHeight();
        left = Math.min(left, source.getWidth() - 1);
        top = Math.min(top, source.getHeight() - 1);
        right = Math.min(source.getWidth(), Math.max(left + 1, right));
        bottom = Math.min(source.getHeight(), Math.max(top + 1, bottom));
        Bitmap result = pool.get(306, 580, Bitmap.Config.ARGB_8888);
        result.setHasAlpha(true);
        result.eraseColor(android.graphics.Color.TRANSPARENT);
        float scale = Math.min(306f / (right - left), 580f / (bottom - top));
        int width = Math.round((right - left) * scale);
        int height = Math.round((bottom - top) * scale);
        int x = (306 - width) / 2;
        int y = (580 - height) / 2;
        new Canvas(result).drawBitmap(source, new Rect(left, top, right, bottom),
                new Rect(x, y, x + width, y + height), new Paint(Paint.FILTER_BITMAP_FLAG));
        return result;
    }
    @Override public boolean equals(Object other) { return other instanceof TutorialApostaShakeGifCrop; }
    @Override public int hashCode() { return ID.hashCode(); }
    @Override public void updateDiskCacheKey(@NonNull MessageDigest digest) { digest.update(ID.getBytes(StandardCharsets.UTF_8)); }
}
