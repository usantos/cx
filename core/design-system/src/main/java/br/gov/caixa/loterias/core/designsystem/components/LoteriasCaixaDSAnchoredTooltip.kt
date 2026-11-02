package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.size
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

/** Posiciona dentro do Box do ícone para o balão e a seta acompanharem essa âncora. */
@Composable
fun LoteriasCaixaDSAnchoredTooltip(
    text: String,
    onDismiss: () -> Unit,
    color: Color = LoteriasCaixaDSColorTokens.CaixaBlue,
    rowBounds: IntRect? = null,
) {
    val density = LocalDensity.current
    var arrowCenter by remember { mutableIntStateOf(0) }
    val position = remember(density, rowBounds) {
        LoteriasCaixaDSTooltipPositionProvider(
            gap = 0,
            margin = with(density) { 12.dp.roundToPx() },
            onArrowPosition = { arrowCenter = it },
            rowBounds = rowBounds
        )
    }
    Popup(popupPositionProvider = position, onDismissRequest = onDismiss) {
        Column(Modifier.width(with(density) { rowBounds?.width?.toDp() } ?: 252.dp)) {
            Canvas(Modifier.fillMaxWidth().height(8.dp)) {
                val half = 8.dp.toPx()
                val center = arrowCenter.toFloat()
                val path = Path().apply {
                    moveTo(center, 0f)
                    lineTo(center - half, size.height)
                    lineTo(center + half, size.height)
                    close()
                }
                drawPath(path, color)
            }
            Surface(color = color, shape = RoundedCornerShape(6.dp)) {
                Text(
                    text,
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    color = LoteriasCaixaDSColorTokens.White,
                    style = TextStyle(
                        fontFamily = LoteriasCaixaDSRegularFontFamily,
                        fontSize = 14.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = true)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

internal class LoteriasCaixaDSTooltipPositionProvider(
    private val gap: Int,
    private val margin: Int,
    private val rowBounds: IntRect? = null,
    private val onArrowPosition: (Int) -> Unit,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val center = anchorBounds.center.x
        val available = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val inset = margin.coerceAtMost(available / 2)
        val left = (rowBounds?.left ?: (center - popupContentSize.width / 2)).coerceIn(inset, available - inset)
        onArrowPosition(center - left)
        return IntOffset(left, (rowBounds?.bottom ?: anchorBounds.bottom) + gap)
    }
}
