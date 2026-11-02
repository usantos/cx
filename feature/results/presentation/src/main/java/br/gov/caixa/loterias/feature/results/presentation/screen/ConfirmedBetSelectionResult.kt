package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.components.DrawNumberColumns
import br.gov.caixa.loterias.feature.results.presentation.components.confirmedBetFirstDrawHitCount
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

@Composable
internal fun ConfirmedBetSelectionResult(bet: ConfirmedBet, result: DrawResult, campaignStyle: LoteriasCaixaDSLotteryStyle?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val drawn = result.firstDrawNumbers.toSet()
    val hits = bet.numbers.filter(drawn::contains).toSet()
    val hitCount = confirmedBetFirstDrawHitCount(bet, result)
    LoteriasCaixaDSCard {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_sua_aposta_3bcfc2),
                color = campaignStyle?.listText ?: LoteriasCaixaDS.colors.brand,
                style = LoteriasCaixaDS.typography.titleMedium,
                modifier = Modifier.uiId("results.0061")
            )
            when {
                bet.lotecaSelections.isNotEmpty() -> ConfirmedLotecaSelectionResult(bet)
                bet.lotogolSelections.isNotEmpty() -> ConfirmedLotogolSelectionResult(bet)
                bet.numberColumns.size > 1 -> DrawNumberColumns(bet.numberColumns, LoteriasCaixaDS.colors.text)
                else -> NumberGrid(bet.numbers, hits, campaignStyle?.dark)
            }
            bet.favoriteTeam?.let {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_time_do_coracao_1_s_7e407c, it.name),
                    color = LoteriasCaixaDS.colors.text,
                    modifier = Modifier.uiId("results.0064")
                )
            }
            bet.luckyMonth?.let {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_mes_de_sorte_1_s_42ca39, it.name),
                    color = LoteriasCaixaDS.colors.text,
                    modifier = Modifier.uiId("results.0065")
                )
            }
            if (bet.clovers.isNotEmpty()) {
                Text(
                    uiStrings.getString(
                        LocalizedR.string.ui_seus_trevos_1_s_e266c5,
                        bet.clovers.joinToString(" • ")
                    ),
                    color = LoteriasCaixaDS.colors.text,
                    modifier = Modifier.uiId("results.0066")
                )
            }
            if (bet.lotecaSelections.isEmpty() && bet.lotogolSelections.isEmpty()) {
                Text(
                    if (hitCount == 0) {
                        uiStrings.getString(LocalizedR.string.format_first_draw_no_hits, bet.status)
                    } else {
                        uiStrings.getQuantityString(
                            LocalizedR.plurals.format_first_draw_hits,
                            hitCount,
                            hitCount,
                            bet.status
                        )
                    },
                    color = if (bet.prize.signum() > 0) LoteriasCaixaDS.colors.success else LoteriasCaixaDS.colors.mutedText,
                    modifier = Modifier.uiId("results.0067")
                )
            }
        }
    }
}

@Composable
private fun ConfirmedLotecaSelectionResult(bet: ConfirmedBet) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    bet.lotecaSelections.forEach { selection ->
        val chosen = listOfNotNull(
            selection.match.home.name.takeIf { selection.homeWin },
            "Empate".takeIf {
                selection.draw
            },
            selection.match.away.name.takeIf { selection.awayWin }
        ).joinToString(" / ")
        Text(
            uiStrings.getString(
                LocalizedR.string.ui_1_s_2_s_3_s_4_s_65b6b0,
                selection.match.number.toString(),
                selection.match.home.name,
                selection.match.away.name,
                chosen
            ),
            color = LoteriasCaixaDS.colors.text,
            modifier = Modifier.uiId("results.0062")
        )
    }
}

@Composable
private fun ConfirmedLotogolSelectionResult(bet: ConfirmedBet) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    bet.lotogolSelections.forEach { selection ->
        val homeScore = if (selection.homeScore >= LOTOGOL_OPEN_SCORE_THRESHOLD) {
            uiStrings.getString(
                LocalizedR.string.ui_4_ou_mais_4bb81b
            )
        } else {
            selection.homeScore.toString()
        }
        val awayScore = if (selection.awayScore >= LOTOGOL_OPEN_SCORE_THRESHOLD) {
            uiStrings.getString(
                LocalizedR.string.ui_4_ou_mais_4bb81b
            )
        } else {
            selection.awayScore.toString()
        }
        Text(
            uiStrings.getString(
                LocalizedR.string.ui_1_s_2_s_3_s_4_s_2295e5,
                selection.match.home.name,
                homeScore,
                awayScore,
                selection.match.away.name
            ),
            color = LoteriasCaixaDS.colors.text,
            modifier = Modifier.uiId("results.0063")
        )
    }
}

private const val LOTOGOL_OPEN_SCORE_THRESHOLD = 4
