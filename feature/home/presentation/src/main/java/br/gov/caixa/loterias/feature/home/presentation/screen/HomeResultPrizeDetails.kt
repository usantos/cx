package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

private const val REGIONAL_WINNER_PREVIEW_LIMIT = 30

@Composable
internal fun HomeResultPrizeDetails(result: DrawResult, code: String, color: Color, modalityName: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val gap = with(LocalDensity.current) { 16.toDp() }
    val (firstPayouts, secondPayouts) = homeResultPayouts(result)
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        fun description(value: String): String {
            val base = homeResultPayoutDescription(code, value)
            val selection = when (base) {
                "Time do Coração" -> result.favoriteTeamReward?.selection
                "Mês de sorte" -> result.luckyMonthReward?.selection
                else -> null
            }
            return if (selection.isNullOrBlank()) base else "$base\n$selection"
        }
        Text(
            if (
                code == uiStrings.getString(LocalizedR.string.ui_duplasena_8bb473)
            ) {
                uiStrings.getString(
                    LocalizedR.string.ui_premiacao_1o_sorteio_e21086
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_premiacao_b81f73)
            },
            (Modifier.padding(top = gap)).uiId("home.0119"),
            color = color,
            style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold)
        )
        HomeResultPayoutRows(firstPayouts, color, ::description)
        if (secondPayouts.isNotEmpty()) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_premiacao_2o_sorteio_e50c1d),
                (Modifier.padding(top = gap)).uiId("home.0120"),
                color = color,
                style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold)
            )
            HomeResultPayoutRows(secondPayouts, color, ::description)
        }
        HomeRegionalWinners(result, code, color, modalityName)
        Text(
            uiStrings.getString(LocalizedR.string.ui_arrecadacao_total_968b74),
            (Modifier.padding(top = with(LocalDensity.current) { 18.toDp() })).uiId("home.0121"),
            color = color,
            style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            homeResultMoney(
                result.totalRevenue.ifBlank {
                    "0"
                }
            ),
            (Modifier.padding(top = gap).legacyAutomationId("home-result-revenue")).uiId("home.0122"),
            color = color,
            style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun HomeResultPayoutRows(
    payouts: List<br.gov.caixa.loterias.feature.results.domain.model.DrawPayout>,
    color: Color,
    description: (String) -> String,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val gap = with(LocalDensity.current) { 16.toDp() }
    payouts.forEach { payout ->
        Text(
            description(payout.description),
            (Modifier.padding(top = gap)).uiId("home.0123"),
            color = color,
            style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            homeResultWinners(payout.winners),
            color = color,
            style = homeResultTextStyle,
            minLines = 2,
            maxLines = 2,
            modifier = Modifier
                .uiId("home.0124")
        )
        if (payout.winners > 0) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_premio_1_s_eb3e48, homeResultMoney(payout.value)),
                color = color,
                style = homeResultTextStyle,
                minLines = 2,
                maxLines = 2,
                modifier = Modifier.uiId("home.0125")
            )
        }
    }
}

@Composable
private fun HomeRegionalWinners(result: DrawResult, code: String, color: Color, modalityName: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val gap = with(LocalDensity.current) { 16.toDp() }
    listOf(
        (
            if (code == "duplasena") {
                uiStrings.getString(
                    LocalizedR.string.ui_ganhadores_por_regiao_1o_sorteio_7ad564
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_ganhadores_por_regiao_dcf6c4)
            }
            ) to result.firstDrawRegionalWinners,
        uiStrings.getString(LocalizedR.string.ui_ganhadores_por_regiao_2o_sorteio_659964) to result.secondDrawRegionalWinners
    ).forEach { (title, winners) ->
        if (winners.isNotEmpty()) {
            Text(
                title,
                (Modifier.padding(top = gap)).uiId("home.0129"),
                color = color,
                style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold)
            )
            winners.take(REGIONAL_WINNER_PREVIEW_LIMIT).forEach { winner ->
                Text(
                    winner.location.replace(" - ", " / "),
                    (Modifier.padding(top = gap)).uiId("home.0130"),
                    color = color,
                    style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    winner.description,
                    color = color,
                    style = homeResultTextStyle,
                    minLines = 2,
                    maxLines = 2,
                    modifier = Modifier
                        .uiId("home.0131")
                )
            }
            if (winners.size >= REGIONAL_WINNER_PREVIEW_LIMIT) {
                val uriHandler = LocalUriHandler.current
                val url = "https://loterias.caixa.gov.br/Paginas/Locais-Sorte.aspx?modalidade=${android.net.Uri.encode(
                    result.modality
                )}&concurso=${result.contest}&titulo=${android.net.Uri.encode(modalityName)}"
                Text(
                    uiStrings.getString(LocalizedR.string.ui_consulte_aqui_o_restante_da_lista_de_62bf78),
                    (Modifier.padding(top = 10.dp).uiId("home.0132").clickable { uriHandler.openUri(url) }).uiId("home.0133"),
                    color = color,
                    style = homeResultTextStyle.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                )
            }
        }
    }
}
