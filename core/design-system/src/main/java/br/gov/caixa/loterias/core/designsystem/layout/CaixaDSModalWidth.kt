package br.gov.caixa.loterias.core.designsystem.layout

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp

object CaixaDSModalTokens {
    val HorizontalMargin = 24.dp
    val MaxWidth = 560.dp
}

/**
 * Largura compartilhada dos diálogos flutuantes. Aplica à superfície externa com
 * `DialogProperties(usePlatformDefaultWidth = false)` para o Android não a limitar antes.
 * Usa as restrições disponíveis da janela, inclusive tela dividida e janelas redimensionadas.
 * Substitutos de Activities em tela cheia e sobreposições bloqueantes de carregamento não usam este modificador.
 */
fun Modifier.caixaDSModalWidth(): Modifier = layout { measurable, constraints ->
    val maxWidth = CaixaDSModalTokens.MaxWidth.roundToPx()
    val width = if (constraints.hasBoundedWidth) {
        (constraints.maxWidth - 2 * CaixaDSModalTokens.HorizontalMargin.roundToPx())
            .coerceIn(0, maxWidth)
    } else {
        maxWidth
    }
    val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
    layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
}
