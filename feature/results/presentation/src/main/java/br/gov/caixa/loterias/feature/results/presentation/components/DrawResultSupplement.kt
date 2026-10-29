package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.presentation.accessibility.PortugueseSpeech
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultModalityCode
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultMoney
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultPayoutDescription
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultPayoutSpokenDescription
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultSportsSpokenDescription
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultTitle
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultWinners
import br.gov.caixa.loterias.feature.results.presentation.mapper.spokenResultMoney
import br.gov.caixa.loterias.feature.results.presentation.mapper.spokenWinners

internal fun sportsResultLabel(match: DrawSportsMatch, lotogol: Boolean): String {
    fun score(value: String, index: Int?) = value.ifBlank {
        when {
            index == null -> ""
            index >= 4 -> "4 ou mais"
            else -> index.toString()
        }
    }
    val homeScore = score(match.home.score, if (lotogol) match.homeScoreIndex else null)
    val awayScore = score(match.away.score, if (lotogol) match.awayScoreIndex else null)
    return listOfNotNull(
        match.number?.let { "$it." },
        match.home.name,
        homeScore.takeIf(String::isNotBlank),
        "×",
        awayScore.takeIf(String::isNotBlank),
        match.away.name
    ).joinToString(" ")
}

internal fun sportsWinnerLabel(match: DrawSportsMatch): String = when {
    match.draw == true -> "Empate"
    match.home.won == true -> "Vencedor: ${match.home.name}"
    match.away.won == true -> "Vencedor: ${match.away.name}"
    else -> ""
}

@Composable
internal fun DrawSportsResults(result: DrawResult, color: Color) {
    if (result.lotecaMatches.isNotEmpty()) DrawLotecaResults(result.lotecaMatches, color)
    result.lotogolMatches.forEach { match ->
        Column(
            Modifier.fillMaxWidth().padding(vertical = 8.dp).clearAndSetSemantics {
                contentDescription = resultSportsSpokenDescription(match, lotogol = true)
            }
        ) {
            Text(sportsResultLabel(match, true), color = color, fontWeight = FontWeight.Bold, modifier = Modifier.uiId("results.0011"))
            sportsWinnerLabel(match).takeIf(String::isNotBlank)?.let { Text(it, color = color, modifier = Modifier.uiId("results.0012")) }
            if (match.legendDescription.isNotBlank()) {
                Text(
                    listOf(match.legendLetter, match.legendDescription).filter(String::isNotBlank).joinToString(" • "),
                    color = color,
                    modifier = Modifier.uiId("results.0013")
                )
            }
        }
    }
}

@Composable
internal fun DrawPayoutRows(title: String, payouts: List<DrawPayout>, color: Color, modality: String = "") {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (payouts.isEmpty()) return
    Text(title, (Modifier.padding(top = 16.dp)).uiId("results.0014"), color = color, fontWeight = FontWeight.Bold)
    payouts.forEach { payout ->
        Column(Modifier.clearAndSetSemantics { contentDescription = resultPayoutSpokenDescription(title, modality, payout) }) {
            Text(
                resultPayoutDescription(modality, payout.description),
                (Modifier.padding(top = 12.dp)).uiId("results.0015"),
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(resultWinners(payout.winners), color = color, modifier = Modifier.uiId("results.0016"))
            if (payout.winners > 0 && payout.value.isNotBlank()) {
                Text(
                    uiStrings.getString(
                        LocalizedR.string.ui_premio_1_s_eb3e48,
                        resultMoney(payout.value)
                    ),
                    color = color,
                    modifier = Modifier.uiId("results.0017")
                )
            }
        }
    }
}

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

@Composable
private fun DrawSelectionRewards(result: DrawResult, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    listOf(
        uiStrings.getString(LocalizedR.string.ui_time_do_coracao_918360) to result.favoriteTeamReward,
        uiStrings.getString(LocalizedR.string.ui_mes_de_sorte_4fd420) to result.luckyMonthReward
    ).forEach { (
        title,
        reward,
    ),
        ->
        reward?.let {
            Column(
                Modifier.clearAndSetSemantics {
                    contentDescription = listOf(
                        "${PortugueseSpeech.expand(title)}: ${PortugueseSpeech.expand(it.selection)}",
                        spokenWinners(it.winners)
                    ).plus(if (it.winners > 0 && it.value.isNotBlank()) listOf("Prêmio: ${spokenResultMoney(it.value)}") else emptyList())
                        .joinToString(". ", postfix = ".")
                }
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_1_s_2_s_13326c, title, it.selection),
                    (Modifier.padding(top = 16.dp)).uiId("results.0020"),
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Text(resultWinners(it.winners), color = color, modifier = Modifier.uiId("results.0021"))
                if (it.winners > 0 && it.value.isNotBlank()) {
                    Text(
                        uiStrings.getString(
                            LocalizedR.string.ui_premio_1_s_eb3e48,
                            resultMoney(it.value)
                        ),
                        color = color,
                        modifier = Modifier.uiId("results.0022")
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawRegionalWinners(result: DrawResult, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    listOf(
        uiStrings.getString(LocalizedR.string.ui_ganhadores_por_regiao_dcf6c4) to result.firstDrawRegionalWinners,
        uiStrings.getString(
            LocalizedR.string.ui_ganhadores_por_regiao_do_2o_sorteio_d8beb0
        ) to result.secondDrawRegionalWinners
    ).forEach { (
        title,
        winners,
    ),
        ->
        if (winners.isNotEmpty()) {
            Text(
                title,
                (Modifier.padding(top = 16.dp)).uiId("results.0023"),
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        winners.forEach { winner ->
            Column(
                Modifier.clearAndSetSemantics {
                    contentDescription = "${PortugueseSpeech.expand(title)}. ${PortugueseSpeech.expand(winner.location)}. " +
                        "${PortugueseSpeech.expand(winner.description)}."
                }
            ) {
                Text(winner.location, (Modifier.padding(top = 10.dp)).uiId("results.0024"), color = color, fontWeight = FontWeight.Bold)
                Text(winner.description, color = color, modifier = Modifier.uiId("results.0025"))
            }
        }
    }
}

@Composable
private fun DrawContestSummary(result: DrawResult, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    // VisualizarResultadosActivity renderiza somente detalhes dos prêmios e arrecadação.
    // O resumo do serviço pertence a outras apresentações de resultado na CX.
    if (result.specialContest) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_concurso_especial_f9b7c4),
            (Modifier.padding(top = 12.dp)).uiId("results.0026"),
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
    if (result.drawLocation.isNotBlank()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_local_do_sorteio_1_s_101c6d, result.drawLocation),
            (Modifier.padding(top = 16.dp)).uiId("results.0027"),
            color = color
        )
    }
    if (result.notice.isNotBlank()) Text(result.notice, (Modifier.padding(top = 12.dp)).uiId("results.0028"), color = color)
    if (result.accumulated) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_acumulou_d5cf3d),
            (Modifier.padding(top = 12.dp)).uiId("results.0029"),
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
    if (result.accumulatedNextContest.isNotBlank()) {
        Text(
            uiStrings.getString(
                LocalizedR.string.ui_acumulado_para_o_proximo_concurso_1_942144,
                resultMoney(result.accumulatedNextContest)
            ),
            color = color,
            modifier = Modifier.uiId("results.0030")
        )
    }
    listOf(
        uiStrings.getString(LocalizedR.string.ui_proximo_concurso_aab095) to result.nextContest,
        uiStrings.getString(LocalizedR.string.ui_proximo_concurso_final_zero_7ffe7d) to result.nextFinalZeroContest,
        uiStrings.getString(LocalizedR.string.ui_proximo_concurso_final_cinco_be1e62) to result.nextFinalFiveContest,
        uiStrings.getString(LocalizedR.string.ui_proximo_concurso_especial_3276ac) to result.nextSpecialContest
    ).forEach { (
        title,
        contest,
    ),
        ->
        contest?.let {
            Column(
                Modifier.clearAndSetSemantics {
                    contentDescription = buildList {
                        add("${PortugueseSpeech.expand(title)}: ${PortugueseSpeech.number(it.number)}")
                        if (it.date.isNotBlank()) add(PortugueseSpeech.date(it.date))
                        if (it.estimatedPrize.isNotBlank()) add("Estimativa de prêmio: ${spokenResultMoney(it.estimatedPrize)}")
                    }.joinToString(". ", postfix = ".")
                }
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_1_s_2_s_13326c, title, it.number.toString()),
                    (Modifier.padding(top = 12.dp)).uiId("results.0031"),
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                if (it.date.isNotBlank()) Text(it.date, color = color, modifier = Modifier.uiId("results.0032"))
                if (it.estimatedPrize.isNotBlank()) {
                    Text(
                        uiStrings.getString(
                            LocalizedR.string.ui_estimativa_de_premio_1_s_aa5411,
                            resultMoney(it.estimatedPrize)
                        ),
                        color = color,
                        modifier = Modifier.uiId("results.0033")
                    )
                }
            }
        }
    }
}
