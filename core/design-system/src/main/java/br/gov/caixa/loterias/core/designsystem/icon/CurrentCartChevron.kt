package br.gov.caixa.loterias.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/** Caminho e área de desenho da seta do recurso chevron_right do carrinho nativo atual. */
val CaixaDSCurrentCartChevron: ImageVector = ImageVector.Builder(
    "CaixaDSCurrentCartChevron", 17.dp, 17.dp, 17f, 17f,
).apply {
    addPath(
        PathParser().parsePathString("M6.51,4.024L5.564,4.97L8.636,8.049L5.564,11.128L6.51,12.073L10.534,8.049L6.51,4.024Z").toNodes(),
        fill = SolidColor(Color(0xFF0066B3)),
    )
}.build()
