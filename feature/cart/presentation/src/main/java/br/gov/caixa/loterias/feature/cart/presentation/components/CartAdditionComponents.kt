package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R as LocalizedR

@Composable
internal fun PoolCartArrow(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val y = 18.dp.toPx()
        val right = size.width
        val bottom = size.height - 4.dp.toPx()
        drawPath(
            Path().apply {
                moveTo(0f, y)
                lineTo(right, y)
                lineTo(right, bottom - 8.dp.toPx())
            },
            color = color,
            style = Stroke(width = 0.65.dp.toPx(), cap = StrokeCap.Square)
        )
        drawPath(
            Path().apply {
                moveTo(right - 4.dp.toPx(), bottom - 8.dp.toPx())
                lineTo(right + 4.dp.toPx(), bottom - 8.dp.toPx())
                lineTo(right, bottom)
                close()
            },
            color = color
        )
    }
}

@Composable
internal fun LegacyHomeButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        modifier.width(184.dp).height(84.dp).background(CaixaDSColorTokens.BetAction).uiId("cart.0001").clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_voltar_ao_inicio_372787),
            fontFamily = CaixaDSFuturaFontFamily,
            color = CaixaDSColorTokens.White,
            fontSize = 17.7.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("cart.0002")
        )
    }
}
