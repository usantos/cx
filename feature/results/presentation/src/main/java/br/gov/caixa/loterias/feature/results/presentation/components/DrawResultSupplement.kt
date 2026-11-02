package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultModalityCode
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultTitle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.results.R as LocalizedR

@Composable
internal fun DrawResultSupplement(
    result: DrawResult,
    color: Color,
    includeFirstPayouts: Boolean = false,
    includeContestSummary: Boolean = true,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (includeFirstPayouts) {
        DrawPayoutRows(
            if (result.secondDrawPayouts.isNotEmpty()) {
                uiStrings.getString(
                    LocalizedR.string.ui_premiacao_do_1o_sorteio_7b478e
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_premiacao_b81f73)
            },
            result.payouts,
            color,
            drawModalityCode(result.modality)
        )
    }
    DrawPayoutRows(
        uiStrings.getString(LocalizedR.string.ui_premiacao_do_2o_sorteio_c067ce),
        result.secondDrawPayouts,
        color,
        drawModalityCode(result.modality)
    )
    DrawSelectionRewards(result, color)
    DrawRegionalWinners(result, color)
    if (includeContestSummary) DrawContestSummary(result, color)
}

@Composable
internal fun DrawNumberColumns(columns: List<List<Int>>, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        columns.forEachIndexed { index, numbers ->
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_1_sa_coluna_bdc185, (index + 1).toString()),
                    color = color,
                    modifier = Modifier.uiId("results.0018")
                )
                numbers.forEach {
                    Text(
                        it.toString(),
                        color = color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .uiId("results.0019")
                    )
                }
            }
        }
    }
}

internal fun drawModalityCode(modality: String): String = resultModalityCode(modality)

internal fun drawResultTitle(result: DrawResult, ordinaryName: String): String = resultTitle(result, ordinaryName)

internal fun confirmedBetFirstDrawHitCount(bet: br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet, result: DrawResult): Int =
    if (bet.modalityId == 7 || drawModalityCode(bet.modality) == "supersete" || drawModalityCode(result.modality) == "supersete") {
        bet.numberColumns.zip(result.firstDrawNumberColumns).count { (selected, drawn) -> selected.any(drawn::contains) }
    } else {
        bet.numbers.toSet().intersect(result.firstDrawNumbers.toSet()).size
    }
