package br.gov.caixa.loterias.feature.betting.presentation.style

import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit

/** Usa as mesmas métricas inteiras que o BoringLayout do TextView com includeFontPadding. */
@Composable
fun Modifier.lotecaNativeTextHeight(fontResource: Int, fontSize: TextUnit): Modifier {
    val context = LocalContext.current
    val density = LocalDensity.current
    val height = remember(context, density, fontResource, fontSize) {
        val paint = android.graphics.Paint().apply {
            typeface = androidx.core.content.res.ResourcesCompat.getFont(context, fontResource)
            textSize = with(density) { fontSize.toPx() }
        }
        val metrics = paint.fontMetricsInt
        metrics.bottom - metrics.top
    }
    return heightIn(min = with(density) { height.toDp() })
}

/** Grade de partidas da Loteca em Compose, com bordas unidas e estados de seleção do legado. */
val lotecaLinearLayoutCenter = object : Alignment.Vertical {
    // O alinhamento de LinearLayout no Android trunca o resto interno; Flow arredonda o centro externo.
    override fun align(size: Int, space: Int): Int = (space - size) / 2
}
