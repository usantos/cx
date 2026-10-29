package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.runtime.getValue
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/** Os LayerDrawables originais preservam exatamente o retângulo girado em 20 graus e os recuos negativos. */
@Composable
internal fun rememberHomeTrapezoidPainter(@DrawableRes resource: Int): Painter {
    return br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter(resource)
}

