package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.components.DrawNumberColumns
import br.gov.caixa.loterias.feature.results.presentation.components.DrawResultSupplement
import br.gov.caixa.loterias.feature.results.presentation.components.DrawSportsResults
import br.gov.caixa.loterias.feature.results.presentation.components.drawResultTitle
import br.gov.caixa.loterias.feature.results.ui.R as DeliveryR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

@Composable
internal fun ConfirmedBetDrawResult(bet: ConfirmedBet, result: DrawResult, campaignStyle: LoteriasCaixaDSLotteryStyle?, resultText: Color) {
    ConfirmedDrawHeader(bet, result, campaignStyle)
    ConfirmedDrawNumbers(bet, result, campaignStyle, resultText)
    ConfirmedBetSelectionResult(bet, result, campaignStyle)
    DrawResultSupplement(result, resultText, includeFirstPayouts = true)
}

@Composable
private fun ConfirmedDrawHeader(bet: ConfirmedBet, result: DrawResult, campaignStyle: LoteriasCaixaDSLotteryStyle?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (campaignStyle != null) {
        Column(Modifier.fillMaxWidth().background(campaignStyle.light).padding(16.dp)) {
            Text(
                uiStrings.getString(
                    DeliveryR.string.ui_1_s_concurso_2_s_9f3926,
                    drawResultTitle(result, result.modality.ifBlank { bet.modality }),
                    result.contest.toString()
                ),
                color = campaignStyle.content,
                style = LoteriasCaixaDS.typography.titleLarge,
                modifier = Modifier.uiId("results.0055")
            )
            Text(
                uiStrings.getString(DeliveryR.string.ui_sorteio_realizado_em_1_s_d632f3, result.drawDate),
                color = campaignStyle.content,
                modifier = Modifier.uiId("results.0056")
            )
        }
    } else {
        LoteriasCaixaDSSectionHeader(
            "${drawResultTitle(result, result.modality.ifBlank { bet.modality })} • Concurso ${result.contest}",
            subtitle = uiStrings.getString(DeliveryR.string.ui_sorteio_realizado_em_1_s_d632f3, result.drawDate),
            modifier = Modifier
                .uiId("results.0057")
        )
    }
}

@Composable
private fun ConfirmedDrawNumbers(bet: ConfirmedBet, result: DrawResult, campaignStyle: LoteriasCaixaDSLotteryStyle?, resultText: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (result.lotecaMatches.isNotEmpty() || result.lotogolMatches.isNotEmpty()) {
        DrawSportsResults(result, resultText)
    } else {
        Text(
            uiStrings.getString(DeliveryR.string.ui_numeros_sorteados_513d0e),
            color = resultText,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("results.0058")
        )
        if (result.firstDrawNumberColumns.size > 1) {
            DrawNumberColumns(result.firstDrawNumberColumns, resultText)
        } else {
            NumberGrid(result.firstDrawNumbers, emptySet(), campaignStyle?.dark)
        }
    }
    if (result.clovers.isNotEmpty()) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_trevos_sorteados_2645f8),
            color = resultText,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("results.0059")
        )
        NumberGrid(result.clovers, bet.clovers.filter(result.clovers::contains).toSet(), campaignStyle?.dark)
    }
    if (result.secondDrawNumbers.isNotEmpty()) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_segundo_sorteio_5c48d6),
            color = resultText,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("results.0060")
        )
        NumberGrid(
            result.secondDrawNumbers,
            bet.numbers.filter(result.secondDrawNumbers::contains).toSet(),
            campaignStyle?.dark
        )
    }
}

@Composable
internal fun NumberGrid(numbers: List<Int>, hits: Set<Int>, campaignAccent: Color? = null) {
    FlowRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        numbers.forEach { number ->
            Box(
                Modifier.background(
                    if (campaignAccent !=
                        null
                    ) {
                        if (number in
                            hits
                        ) {
                            LoteriasCaixaDSColorTokens.White
                        } else {
                            campaignAccent
                        }
                    } else if (number in
                        hits
                    ) {
                        LoteriasCaixaDS.colors.success
                    } else {
                        LoteriasCaixaDS.colors.brand
                    },
                    CircleShape
                ).padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    number.toString().padStart(2, '0'),
                    color = if (campaignAccent != null && number in hits) campaignAccent else LoteriasCaixaDSColorTokens.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("results.0070")
                )
            }
        }
    }
}
