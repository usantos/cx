package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.feature.betting.presentation.route.ShakeTutorialFrameTransform

@Composable
internal fun ShakeTutorialArtwork(painter: Painter, description: String) {
    Canvas(
        Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 8.dp).semantics {
            contentDescription = description
        }
    ) {
        val sourceSize = painter.intrinsicSize
        if (sourceSize.width.isFinite() && sourceSize.height.isFinite() &&
            sourceSize.width > 0f && sourceSize.height > 0f
        ) {
            val frame = ShakeTutorialFrameTransform.fromSourceSize(sourceSize)
            val fit = minOf(size.width / frame.outputWidth, size.height / frame.outputHeight)
            val frameWidth = frame.outputWidth * fit
            val frameHeight = frame.outputHeight * fit
            val frameLeft = (size.width - frameWidth) / 2f
            val frameTop = (size.height - frameHeight) / 2f
            val imageScale = fit * frame.bitmapScale
            clipRect(frameLeft, frameTop, frameLeft + frameWidth, frameTop + frameHeight) {
                withTransform({
                    translate(
                        left = frameLeft + frame.destinationLeft * fit - frame.sourceLeft * imageScale,
                        top = frameTop + frame.destinationTop * fit - frame.sourceTop * imageScale
                    )
                    scale(imageScale, imageScale, pivot = Offset.Zero)
                }) {
                    with(painter) { draw(sourceSize) }
                }
            }
        }
    }
}
