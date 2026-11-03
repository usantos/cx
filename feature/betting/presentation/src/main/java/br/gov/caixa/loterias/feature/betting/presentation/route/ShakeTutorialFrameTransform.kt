package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.geometry.Size

/** Geometria aplicada a cada quadro GIF decodificado, seguindo a transformação de bitmap do Glide legado. */
data class ShakeTutorialFrameTransform(
    val outputWidth: Float,
    val outputHeight: Float,
    val sourceLeft: Float,
    val sourceTop: Float,
    val sourceWidth: Float,
    val sourceHeight: Float,
    val bitmapScale: Float,
    val destinationLeft: Float,
    val destinationTop: Float,
    val destinationWidth: Float,
    val destinationHeight: Float,
) {
    companion object {
        fun fromSourceSize(source: Size): ShakeTutorialFrameTransform {
            val outputWidth = FRAME_WIDTH
            val outputHeight = FRAME_HEIGHT
            val hasLegacyMargins = source.width == LEGACY_BITMAP_WIDTH && source.height == LEGACY_BITMAP_HEIGHT
            val sourceLeft = if (hasLegacyMargins) LEGACY_SOURCE_LEFT else 0f
            val sourceTop = if (hasLegacyMargins) LEGACY_SOURCE_TOP else 0f
            val sourceWidth = if (hasLegacyMargins) FRAME_WIDTH else source.width
            val sourceHeight = if (hasLegacyMargins) FRAME_HEIGHT else source.height
            val bitmapScale = minOf(outputWidth / sourceWidth, outputHeight / sourceHeight)
            val destinationWidth = kotlin.math.round(sourceWidth * bitmapScale)
            val destinationHeight = kotlin.math.round(sourceHeight * bitmapScale)
            return ShakeTutorialFrameTransform(
                outputWidth = outputWidth,
                outputHeight = outputHeight,
                sourceLeft = sourceLeft,
                sourceTop = sourceTop,
                sourceWidth = sourceWidth,
                sourceHeight = sourceHeight,
                bitmapScale = bitmapScale,
                destinationLeft = (outputWidth - destinationWidth) / 2f,
                destinationTop = (outputHeight - destinationHeight) / 2f,
                destinationWidth = destinationWidth,
                destinationHeight = destinationHeight
            )
        }
    }
}

private const val FRAME_WIDTH = 306f
private const val FRAME_HEIGHT = 580f
private const val LEGACY_BITMAP_WIDTH = 412f
private const val LEGACY_BITMAP_HEIGHT = 804f
private const val LEGACY_SOURCE_LEFT = 53f
private const val LEGACY_SOURCE_TOP = 119f
