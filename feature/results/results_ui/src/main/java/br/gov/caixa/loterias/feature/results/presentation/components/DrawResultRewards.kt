package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.presentation.accessibility.PortugueseSpeech
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultMoney
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultPayoutDescription
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultPayoutSpokenDescription
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultWinners
import br.gov.caixa.loterias.feature.results.presentation.mapper.spokenResultMoney
import br.gov.caixa.loterias.feature.results.presentation.mapper.spokenWinners
import br.gov.caixa.loterias.feature.results.ui.R as DeliveryR

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
                        DeliveryR.string.ui_premio_1_s_eb3e48,
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
internal fun DrawSelectionRewards(result: DrawResult, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    listOf(
        uiStrings.getString(DeliveryR.string.ui_time_do_coracao_918360) to result.favoriteTeamReward,
        uiStrings.getString(DeliveryR.string.ui_mes_de_sorte_4fd420) to result.luckyMonthReward
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
                    uiStrings.getString(DeliveryR.string.ui_1_s_2_s_13326c, title, it.selection),
                    (Modifier.padding(top = 16.dp)).uiId("results.0020"),
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Text(resultWinners(it.winners), color = color, modifier = Modifier.uiId("results.0021"))
                if (it.winners > 0 && it.value.isNotBlank()) {
                    Text(
                        uiStrings.getString(
                            DeliveryR.string.ui_premio_1_s_eb3e48,
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
internal fun DrawRegionalWinners(result: DrawResult, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    listOf(
        uiStrings.getString(DeliveryR.string.ui_ganhadores_por_regiao_dcf6c4) to result.firstDrawRegionalWinners,
        uiStrings.getString(
            DeliveryR.string.ui_ganhadores_por_regiao_do_2o_sorteio_d8beb0
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
