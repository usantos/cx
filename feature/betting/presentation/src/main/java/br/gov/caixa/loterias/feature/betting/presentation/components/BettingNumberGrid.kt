package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCheckbox as Checkbox
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R

@Composable
internal fun BettingLegacyNumberGrid(
    numbers: List<Int>,
    selected: Set<Int>,
    onNumberClick: (Int) -> Unit,
    modifier: Modifier,
    modalityId: Int,
) {
    val style = caixaDSLotteryStyle(modalityId)
    val accent = if (style != resolveCaixaDSLotteryStyle(modalityId, "", CaixaDSCampaign.DEFAULT)) {
        style.numeral
    } else if (style.content == CaixaDSColorTokens.White) {
        style.light
    } else {
        style.content
    }
    Layout(modifier = modifier, content = {
        numbers.forEach { number ->
            BettingNumberCell(number, number in selected, accent) { onNumberClick(number) }
        }
    }) { measurables, constraints ->
        // GridLayoutManager.calculateItemBorders distribui o resto inteiro
        // entre as colunas; dividir uma largura em dp arredonda todas as colunas para a mesma largura em pixels.
        val spanCount = 6
        val totalWidth = constraints.maxWidth
        val borders = IntArray(spanCount + 1)
        val baseWidth = totalWidth / spanCount
        val remainder = totalWidth % spanCount
        var extra = 0
        for (span in 1..spanCount) {
            var width = baseWidth
            extra += remainder
            if (extra > 0 && spanCount - extra < remainder) {
                width++
                extra -= spanCount
            }
            borders[span] = borders[span - 1] + width
        }
        val placeables = measurables.mapIndexed { index, measurable ->
            val column = index % spanCount
            measurable.measure(
                Constraints(
                    minWidth = borders[column + 1] - borders[column],
                    maxWidth = borders[column + 1] - borders[column]
                )
            )
        }
        val rows = placeables.chunked(spanCount)
        val rowHeights = rows.map { row -> row.maxOf { it.height } }
        layout(totalWidth, constraints.constrainHeight(rowHeights.sum())) {
            var top = 0
            rows.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { column, child -> child.placeRelative(borders[column], top) }
                top += rowHeights[rowIndex]
            }
        }
    }
}

@Composable
private fun BettingNumberCell(number: Int, checked: Boolean, accent: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Box(Modifier.padding(horizontal = 2.dp, vertical = 4.dp), contentAlignment = Alignment.CenterStart) {
        val label = if (number == 100) "00" else number.toString().padStart(2, '0')
        Box(
            Modifier.size(45.dp)
                .legacyAutomationId("betting-number-$label")
                .semantics {
                    contentDescription = label
                    this.selected = checked
                }
                .uiId("bet.0047").toggleable(value = checked, role = Role.Checkbox, onValueChange = { onClick() }),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(if (checked) R.drawable.ic_item_dezena_selecionado else R.drawable.ic_item_dezena),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                colorFilter = if (checked) ColorFilter.tint(accent) else null
            )
            Text(
                label,
                color = if (checked) CaixaDSColorTokens.White else CaixaDSColorTokens.SocialGraphite,
                fontFamily = CaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                modifier = Modifier
                    .uiId("bet.0048")
            )
        }
    }
}
