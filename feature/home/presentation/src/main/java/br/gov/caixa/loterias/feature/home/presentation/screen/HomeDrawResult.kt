package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

internal val homeResultTextStyle = TextStyle(
    fontFamily = CaixaDSFontFamily,
    fontSize = 16.sp,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

@Composable
internal fun HomeDrawResult(result: DrawResult, lottery: HomeLottery, modifier: Modifier = Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val code = normalizeModality(result.modality)
    val color = homeResultContentColor(code)
    // O conteúdo legado do resultado mede 280dp de largura dentro do cartão e rola de forma independente.
    Column(
        modifier.legacyAutomationId("home-result-scroll").uiId("home.0113").verticalScroll(
            remember(result.modality, result.contest) {
                ScrollState(0)
            }
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(Modifier.widthIn(max = 280.dp).fillMaxWidth().padding(top = 15.dp, bottom = 16.dp)) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Nº ${result.contest}") }
                    append(" | ${homeResultDate(result.drawDate)}")
                },
                (Modifier.align(Alignment.CenterHorizontally)).uiId("home.0114"),
                color = color,
                style = homeResultTextStyle
            )
            HomeResultDraws(result, lottery, code, color)
            HomeResultClovers(result, lottery, color)
            HomeResultComplement(result, code, color)
            Box(
                Modifier.fillMaxWidth().padding(
                    top = when (code) {
                        "supersete" -> 0.dp
                        "loteca", "lotogol" -> 20.dp
                        else -> 10.dp
                    }
                ).height(1.dp).background(
                    if (code ==
                        "supersete" ||
                        code == "loteca" ||
                        code == "lotogol"
                    ) {
                        lottery.color
                    } else if (code == "megasena" &&
                        lottery.octoberRoseTheme
                    ) {
                        lottery.headerContentColor
                    } else {
                        color
                    }
                )
            )
            if (code == "duplasena") Spacer(Modifier.height(20.dp))
            HomeResultPrizeDetails(result, code, color, lottery.name)
        }
    }
}

@Composable
internal fun HomeResultCenteredText(text: String, color: Color, bold: Boolean = false, top: Int = 0, fontSize: Float = 16f) {
    Box(Modifier.fillMaxWidth().padding(top = top.dp), contentAlignment = Alignment.Center) {
        Text(
            text,
            color = color,
            style = homeResultTextStyle.copy(fontSize = fontSize.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal),
            modifier = Modifier.uiId("home.0115")
        )
    }
}

@Composable
private fun HomeResultNumbers(numbers: List<Int>, columns: Int, color: Color, top: Int = 15) {
    val numberSize = 16.sp
    Column(Modifier.fillMaxWidth().padding(top = top.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        numbers.sorted().chunked(columns).forEach { row ->
            Row(Modifier.width((columns * 43).dp)) {
                row.forEach { number ->
                    Box(Modifier.size(43.dp), contentAlignment = Alignment.Center) {
                        Text(
                            (if (number == 100) 0 else number).toString().padStart(2, '0'),
                            color = color,
                            style = homeResultTextStyle.copy(fontSize = numberSize, fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .uiId("home.0116")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSuperSeteNumbers(result: DrawResult, lottery: HomeLottery) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp, bottom = 16.dp)) {
        Row(Modifier.fillMaxWidth()) {
            repeat(7) { index ->
                Box(
                    Modifier.weight(1f).height(30.dp).padding(3.dp).background(lottery.color, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_1_s_d020a2, (index + 1).toString()),
                        color = CaixaDSColorTokens.SuperSeteContent,
                        style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.uiId("home.0117")
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            repeat(7) { index ->
                Box(Modifier.weight(1f).height(30.dp).padding(3.dp), contentAlignment = Alignment.Center) {
                    Text(
                        result.firstDrawNumberColumns.getOrNull(index)?.firstOrNull()?.toString().orEmpty(),
                        color = CaixaDSColorTokens.White,
                        style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.uiId("home.0118")
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeResultPrizeDetails(result: DrawResult, code: String, color: Color, modalityName: String) {
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

internal fun homeResultContentColor(code: String): Color =
    if (normalizeModality(code) == "timemania") CaixaDSColorTokens.TimemaniaContent else CaixaDSColorTokens.White

@Composable
private fun HomeResultDraws(result: DrawResult, lottery: HomeLottery, code: String, color: Color) {
    when {
        result.lotecaMatches.isNotEmpty() || result.lotogolMatches.isNotEmpty() -> HomeSportsResults(result, color)

        code == "supersete" -> HomeSuperSeteNumbers(result, lottery)

        else -> {
            if (code == "duplasena") HomeResultCenteredText("1º sorteio", color, bold = true, top = 20, fontSize = 15.8f)
            HomeResultNumbers(
                result.firstDrawNumbers,
                homeResultColumns(code, result.firstDrawNumbers.size),
                color,
                top = if (code == "duplasena" || code == "timemania" || code == "diadesorte") 19 else 15
            )
            if (result.secondDrawNumbers.isNotEmpty()) {
                HomeResultCenteredText("2º sorteio", color, bold = true, top = 20, fontSize = 15.8f)
                HomeResultNumbers(result.secondDrawNumbers, homeResultColumns(code, result.secondDrawNumbers.size), color, top = 19)
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.HomeResultClovers(result: DrawResult, lottery: HomeLottery, color: Color) {
    if (result.clovers.isNotEmpty()) {
        HomeResultCenteredText("Trevos", color, top = 10, fontSize = 14.2f)
        Row(Modifier.align(Alignment.CenterHorizontally).padding(start = 28.dp, top = 19.dp, bottom = 10.dp)) {
            result.clovers.sorted().forEach { number ->
                Box(Modifier.size(width = 56.dp, height = 40.dp), contentAlignment = Alignment.Center) {
                    Image(painterResource(R.drawable.legacy_home_result_trevo), null, Modifier.size(40.dp))
                    Text(
                        number.toString().padStart(2, '0'),
                        color = lottery.color,
                        style = homeResultTextStyle.copy(fontSize = 14.1.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .uiId("home.0126")
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeResultComplement(result: DrawResult, code: String, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (code == "timemania" || code == "diadesorte") {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 33.dp).padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (code ==
                "timemania"
            ) {
                Image(
                    painterResource(R.drawable.legacy_home_result_ic_timecoracao),
                    null,
                    Modifier.size(width = 41.dp, height = 34.dp)
                )
            }
            Column(Modifier.padding(start = if (code == "timemania") 16.dp else 0.dp)) {
                Text(
                    if (
                        code == uiStrings.getString(LocalizedR.string.ui_timemania_9b0b6c)
                    ) {
                        uiStrings.getString(
                            LocalizedR.string.ui_time_do_coracao_918360
                        )
                    } else {
                        uiStrings.getString(LocalizedR.string.ui_mes_de_sorte_4fd420)
                    },
                    color = color,
                    style = homeResultTextStyle.copy(fontSize = 14.2.sp),
                    modifier = Modifier.uiId("home.0127")
                )
                Text(
                    (
                        if (
                            code == uiStrings.getString(LocalizedR.string.ui_timemania_9b0b6c)
                        ) {
                            result.favoriteTeamReward?.selection
                        } else {
                            result.luckyMonthReward?.selection
                        }
                        ) ?: uiStrings.getString(LocalizedR.string.ui_texto_a03b22),
                    color = color,
                    style = homeResultTextStyle.copy(fontSize = 14.2.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .uiId("home.0128")
                )
            }
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
            winners.take(30).forEach { winner ->
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
            if (winners.size >= 30) {
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
