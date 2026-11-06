package br.gov.caixa.loterias.apostas.model.config;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ColorInt;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.Shape;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

public class LotteryGridStyleConfig {

    private final Shape shape;
    private final CellStyle normal;
    private final CellStyle intersection;
    private final boolean announceColumns;

    public LotteryGridStyleConfig(Shape shape, CellStyle normal, CellStyle intersection, boolean announceColumns) {
        this.shape = shape;
        this.normal = normal;
        this.intersection = intersection;
        this.announceColumns = announceColumns;
    }

    public static LotteryGridStyleConfig circular(Context context, EstiloModalidadeMKP style) {
        return new LotteryGridStyleConfig(
                Shape.CIRCULO,
                new CellStyle (
                        context.getColor(style.getCorFonteFundoEscuro()),
                        context.getColor(style.getCorFonteFundoEscuro()),
                        context.getColor(style.getCorEscura())
                ),
                new CellStyle(
                        context.getColor(style.getCorEscura()),
                        context.getColor(style.getCorFonteFundoEscuro()),
                        context.getColor(style.getCorFonteFundoEscuro())
                ),
                false);
    }

    public static LotteryGridStyleConfig circular7(Context context, EstiloModalidadeMKP style) {
        return new LotteryGridStyleConfig(
                Shape.CIRCULO,
                new CellStyle (
                        context.getColor(style.getCorFonteFundoEscuro()),
                        context.getColor(style.getCorFonteFundoEscuro()),
                        context.getColor(style.getCorEscura())
                ),
                new CellStyle(
                        context.getColor(style.getCorEscura()),
                        context.getColor(style.getCorFonteFundoEscuro()),
                        context.getColor(style.getCorFonteFundoEscuro())
                ),
                false);
    }

    public static LotteryGridStyleConfig rectangular(Context context, EstiloModalidadeMKP style) {
        return new LotteryGridStyleConfig(
                Shape.RETANGULO,
                new CellStyle (
                        context.getColor(style.getCorFonteFundoEscuro()),
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                ),
                new CellStyle(
                        context.getColor(style.getCorFonteFundoEscuro()),
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                ),
                true);
    }

    public static LotteryGridStyleConfig rectangular7(Context context, EstiloModalidadeMKP style) {
        return new LotteryGridStyleConfig(
                Shape.RETANGULO,
                new CellStyle (
                        context.getColor(style.getCorEscura()),
                        context.getColor(style.getCorClara()),
                        context.getColor(style.getCorClara())
                ),
                new CellStyle(
                        context.getColor(style.getCorEscura()),
                        context.getColor(style.getCorClara()),
                        context.getColor(style.getCorClara())
                ),
                true);
    }

    public static LotteryGridStyleConfig trevo(Context context, EstiloModalidadeMKP style) {
        return new LotteryGridStyleConfig(
                Shape.TREVO,
                new CellStyle (
                        context.getColor(style.getCorFonteFundoEscuro()),
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                ),
                new CellStyle(
                        context.getColor(style.getCorEscura()),
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                ),
                false);
    }

    public static LotteryGridStyleConfig noShape(Context context, EstiloModalidadeMKP style) {
        return new LotteryGridStyleConfig(
                Shape.NO_SHAPE,
                new CellStyle (
                        context.getColor(style.getCorFonteFundoEscuro()),
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                ),
                new CellStyle(
                        context.getColor(style.getCorFonteFundoEscuro()),
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                ),
                false);
    }

    public Shape getShape() {
        return shape;
    }

    public CellStyle getNormal() {
        return normal;
    }

    public CellStyle getIntersection() {
        return intersection;
    }

    public boolean isAnnounceColumns() {
        return announceColumns;
    }

    public static class CellStyle {
        @ColorInt
        private final int textColor;
        @ColorInt
        private final int borderColor;
        @ColorInt
        private final int backgroundColor;

        public CellStyle(int textColor, int borderColor, int backgroundColor) {
            this.textColor = textColor;
            this.borderColor = borderColor;
            this.backgroundColor = backgroundColor;
        }

        public int getTextColor() {
            return textColor;
        }

        public int getBorderColor() {
            return borderColor;
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }
    }
}
