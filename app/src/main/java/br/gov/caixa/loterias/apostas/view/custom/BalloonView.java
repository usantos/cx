package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

/**
 * Custom View that renders a realistic 3D balloon using Canvas + RadialGradient.
 *
 * Layers (bottom to top):
 *   1. Balloon body with RadialGradient (light source top-left → dark bottom-right)
 *   2. Subtle dark rim at bottom for depth
 *   3. Primary specular highlight (large soft oval, top-left)
 *   4. Secondary specular dot (small bright spot)
 *   5. Knot (small triangle at bottom of body)
 *   6. String (bezier curve)
 */
public class BalloonView extends View {

    private final Paint bodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint rimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint knotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Base color of the balloon (set via setBalloonColor)
    private int baseColor = Color.parseColor("#4CAF50");

    public BalloonView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_SOFTWARE, null); // needed for shader on older APIs
    }

    public void setBalloonColor(int color) {
        this.baseColor = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        // Body occupies top 50% of view: nearly circular oval (classic balloon look)
        // The remaining 50% is reserved for a long, curved string with room to breathe
        float bodyRight  = w;
        float bodyBottom = h * 0.50f;
        float cx = w / 2f;
        float cy = bodyBottom * 0.46f;
        float rx = w / 2f;
        float ry = bodyBottom / 2f;

        RectF bodyRect = new RectF(0, 0, bodyRight, bodyBottom);

        // ── 1. Balloon body: RadialGradient simulating 3D sphere illumination ──
        // Light source comes from top-left.
        // Highlight color = tinted lighter version of baseColor
        // Shadow color  = tinted darker version of baseColor
        int lightColor  = blendWithWhite(baseColor, 0.55f);   // bright center
        int midColor    = baseColor;
        int shadowColor = darkenColor(baseColor, 0.50f);       // dark bottom-right

        // Radial gradient center is offset to the top-left (where the light hits)
        float gradCx = cx * 0.55f;
        float gradCy = ry * 0.45f;
        float gradRadius = Math.max(w, bodyBottom) * 0.95f;

        bodyPaint.setShader(new RadialGradient(
                gradCx, gradCy,
                gradRadius,
                new int[]{lightColor, midColor, shadowColor},
                new float[]{0f, 0.45f, 1f},
                Shader.TileMode.CLAMP
        ));

        canvas.drawOval(bodyRect, bodyPaint);

        // ── 2. Rim shadow at the bottom edge (dark halo for depth) ──
        rimPaint.setShader(new RadialGradient(
                cx, bodyBottom,
                ry * 0.6f,
                new int[]{Color.argb(100, 0, 0, 0), Color.TRANSPARENT},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawOval(new RectF(cx - rx * 0.7f, bodyBottom - ry * 0.35f,
                cx + rx * 0.7f, bodyBottom + ry * 0.05f), rimPaint);

        // ── 3. Primary specular highlight: big soft oval at top-left ──
        float hlCx = cx * 0.42f;
        float hlCy = ry * 0.34f;
        float hlW  = rx * 0.55f;
        float hlH  = ry * 0.35f;
        highlightPaint.setShader(new RadialGradient(
                hlCx, hlCy,
                Math.max(hlW, hlH),
                new int[]{Color.argb(200, 255, 255, 255), Color.argb(60, 255, 255, 255), Color.TRANSPARENT},
                new float[]{0f, 0.4f, 1f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawOval(new RectF(hlCx - hlW, hlCy - hlH, hlCx + hlW, hlCy + hlH), highlightPaint);

        // ── 4. Secondary specular dot: tiny bright spot for gloss ──
        float dotCx = cx * 0.58f;
        float dotCy = ry * 0.18f;
        float dotR  = rx * 0.09f;
        dotPaint.setShader(new RadialGradient(
                dotCx, dotCy,
                dotR,
                new int[]{Color.argb(230, 255, 255, 255), Color.TRANSPARENT},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(dotCx, dotCy, dotR, dotPaint);

        // ── 5. Knot: teardrop shape at the bottom of the body ──
        float knotTop = bodyBottom - 2;
        float knotW   = w * 0.12f;
        float knotH   = h * 0.06f;
        Path knotPath = new Path();
        knotPath.moveTo(cx - knotW / 2f, knotTop);
        knotPath.lineTo(cx + knotW / 2f, knotTop);
        knotPath.quadTo(cx + knotW * 0.3f, knotTop + knotH * 0.6f, cx, knotTop + knotH);
        knotPath.quadTo(cx - knotW * 0.3f, knotTop + knotH * 0.6f, cx - knotW / 2f, knotTop);
        knotPath.close();
        knotPaint.setColor(darkenColor(baseColor, 0.35f));
        knotPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(knotPath, knotPaint);

        // ── 6. String: long S-curve (cubic bezier) matching a real balloon in the air ──
        float stringStartX = cx;
        float stringStartY = knotTop + knotH;
        float stringEndX   = cx + w * 0.10f;  // ends near center-right
        float stringEndY   = h * 0.99f;
        float strLen = stringEndY - stringStartY;
        // ctrl1 pulls strongly right → creates upper arc
        float ctrl1X = cx + w * 0.55f;
        float ctrl1Y = stringStartY + strLen * 0.28f;
        // ctrl2 pulls strongly left → creates lower arc, giving the S
        float ctrl2X = cx - w * 0.40f;
        float ctrl2Y = stringStartY + strLen * 0.68f;
        Path stringPath = new Path();
        stringPath.moveTo(stringStartX, stringStartY);
        stringPath.cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, stringEndX, stringEndY);
        stringPaint.setStyle(Paint.Style.STROKE);
        stringPaint.setStrokeWidth(1.8f);
        stringPaint.setColor(Color.argb(190, 90, 90, 90));
        stringPaint.setShader(null);
        canvas.drawPath(stringPath, stringPaint);
    }

    // ── Color helpers ──

    /** Blend the given color with white by [amount] (0=original, 1=white). */
    private int blendWithWhite(int color, float amount) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        r = (int) (r + (255 - r) * amount);
        g = (int) (g + (255 - g) * amount);
        b = (int) (b + (255 - b) * amount);
        return Color.rgb(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    /** Darken the given color by [amount] (0=unchanged, 1=black). */
    private int darkenColor(int color, float amount) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= (1f - amount);
        return Color.HSVToColor(hsv);
    }
}
