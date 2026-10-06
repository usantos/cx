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

/** Remove somente as margens transparentes dos GIFs de volante/menu (412 x 804).
 * A área (53, 119)-(359, 699) contém os pixels visíveis de TODOS os quadros.
 * Glide aplica a transformação a cada quadro; os arquivos originais são preservados.
 */
public final class TutorialApostaShakeGifCrop extends BitmapTransformation {
    private static final String ID = "br.gov.caixa.loterias.apostas.TutorialApostaShakeGifCrop.v1";
    @Override protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap source, int outWidth, int outHeight) {
        int left = Math.round(source.getWidth() * 53f / 412f);
        int top = Math.round(source.getHeight() * 119f / 804f);
        int right = Math.round(source.getWidth() * 359f / 412f);
        int bottom = Math.round(source.getHeight() * 699f / 804f);
        left = Math.min(left, source.getWidth() - 1);
        top = Math.min(top, source.getHeight() - 1);
        right = Math.min(source.getWidth(), Math.max(left + 1, right));
        bottom = Math.min(source.getHeight(), Math.max(top + 1, bottom));
        Bitmap result = pool.get(right - left, bottom - top, Bitmap.Config.ARGB_8888);
        result.setHasAlpha(true);
        new Canvas(result).drawBitmap(source, new Rect(left, top, right, bottom),
                new Rect(0, 0, result.getWidth(), result.getHeight()), new Paint(Paint.FILTER_BITMAP_FLAG));
        return result;
    }
    @Override public boolean equals(Object other) { return other instanceof TutorialApostaShakeGifCrop; }
    @Override public int hashCode() { return ID.hashCode(); }
    @Override public void updateDiskCacheKey(@NonNull MessageDigest digest) { digest.update(ID.getBytes(StandardCharsets.UTF_8)); }
}
