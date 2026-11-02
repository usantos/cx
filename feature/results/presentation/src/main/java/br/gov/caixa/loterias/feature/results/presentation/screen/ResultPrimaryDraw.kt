package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.components.DrawPublicSuperSevenResult
import br.gov.caixa.loterias.feature.results.presentation.components.DrawSportsResults

@Composable
private fun NumberRows(numbers: List<Int>, modifier: Modifier = Modifier, color: Color) {
    val columns = if (numbers.size > COMPACT_DRAW_LIMIT) EXPANDED_DRAW_COLUMNS else COMPACT_DRAW_COLUMNS
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        numbers.chunked(columns).forEach { row ->
            Row(Modifier.width((columns * NUMBER_CELL_WIDTH).dp).padding(bottom = 10.dp)) {
                row.forEach { number ->
                    Box(Modifier.width(56.dp).height(40.dp), contentAlignment = Alignment.Center) {
                        Box(
                            Modifier.size(40.dp).background(LoteriasCaixaDSColorTokens.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                number.toString().padStart(2, '0'),
                                color = color,
                                style = TextStyle(
                                    fontFamily = LoteriasCaixaDSFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.1.sp,
                                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                                ),
                                modifier = Modifier.uiId("results.0071")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ResultPrimaryDraw(
    result: DrawResult,
    modality: String,
    style: LoteriasCaixaDSLotteryStyle,
    bodyStyle: TextStyle,
    numberColor: Color,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (result.lotecaMatches.isNotEmpty() || result.lotogolMatches.isNotEmpty()) {
        Column(Modifier.padding(horizontal = 30.dp)) { DrawSportsResults(result, style.darkContent) }
    } else if (modality == "supersete") {
        DrawPublicSuperSevenResult(
            result.firstDrawNumberColumns.takeIf { it.size == SUPER_SEVEN_COLUMN_COUNT }
                ?: result.firstDrawNumbers.map { listOf(it) },
            style.darkContent
        )
    } else {
        Text(
            uiStrings.getString(LocalizedR.string.ui_numeros_sorteados_5e6cc1),
            (Modifier.padding(start = 30.dp)).uiId("results.0072"),
            color = style.darkContent,
            style = bodyStyle
        )
        NumberRows(result.firstDrawNumbers.sorted(), (Modifier.fillMaxWidth().padding(top = 20.dp)).uiId("results.0073"), numberColor)
    }
    if (result.secondDrawNumbers.isNotEmpty()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_2o_sorteio_b21ce2),
            (Modifier.padding(top = 16.dp)).uiId("results.0074"),
            color = style.darkContent,
            style = bodyStyle
        )
        NumberRows(result.secondDrawNumbers.sorted(), (Modifier.fillMaxWidth().padding(top = 20.dp)).uiId("results.0075"), numberColor)
    }
}

private const val COMPACT_DRAW_LIMIT = 6

private const val EXPANDED_DRAW_COLUMNS = 4

private const val COMPACT_DRAW_COLUMNS = 3

private const val NUMBER_CELL_WIDTH = 56

private const val SUPER_SEVEN_COLUMN_COUNT = 7
