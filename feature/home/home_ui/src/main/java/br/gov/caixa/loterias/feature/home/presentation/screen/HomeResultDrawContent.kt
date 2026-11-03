package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

private const val RESULT_NUMBER_CELL_SIZE = 43
private const val LOTOMANIA_HUNDRED_SENTINEL = 100
private const val SUPER_SETE_COLUMN_COUNT = 7

@Composable
private fun HomeResultNumbers(numbers: List<Int>, columns: Int, color: Color, top: Int = 15) {
    val numberSize = 16.sp
    Column(Modifier.fillMaxWidth().padding(top = top.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        numbers.sorted().chunked(columns).forEach { row ->
            Row(Modifier.width((columns * RESULT_NUMBER_CELL_SIZE).dp)) {
                row.forEach { number ->
                    Box(Modifier.size(RESULT_NUMBER_CELL_SIZE.dp), contentAlignment = Alignment.Center) {
                        Text(
                            (if (number == LOTOMANIA_HUNDRED_SENTINEL) 0 else number).toString().padStart(2, '0'),
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
            repeat(SUPER_SETE_COLUMN_COUNT) { index ->
                Box(
                    Modifier.weight(1f).height(30.dp).padding(3.dp).background(lottery.color, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_1_s_d020a2, (index + 1).toString()),
                        color = LoteriasCaixaDSColorTokens.SuperSeteContent,
                        style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.uiId("home.0117")
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            repeat(SUPER_SETE_COLUMN_COUNT) { index ->
                Box(Modifier.weight(1f).height(30.dp).padding(3.dp), contentAlignment = Alignment.Center) {
                    Text(
                        result.firstDrawNumberColumns.getOrNull(index)?.firstOrNull()?.toString().orEmpty(),
                        color = LoteriasCaixaDSColorTokens.White,
                        style = homeResultTextStyle.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.uiId("home.0118")
                    )
                }
            }
        }
    }
}

@Composable
internal fun HomeResultDraws(result: DrawResult, lottery: HomeLottery, code: String, color: Color) {
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
internal fun androidx.compose.foundation.layout.ColumnScope.HomeResultClovers(result: DrawResult, lottery: HomeLottery, color: Color) {
    if (result.clovers.isNotEmpty()) {
        HomeResultCenteredText("Trevos", color, top = 10, fontSize = 14.2f)
        Row(Modifier.align(Alignment.CenterHorizontally).padding(start = 28.dp, top = 19.dp, bottom = 10.dp)) {
            result.clovers.sorted().forEach { number ->
                Box(Modifier.size(width = 56.dp, height = 40.dp), contentAlignment = Alignment.Center) {
                    Image(painterResource(DeliveryR.drawable.legacy_home_result_trevo), null, Modifier.size(40.dp))
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
internal fun HomeResultComplement(result: DrawResult, code: String, color: Color) {
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
                    painterResource(DeliveryR.drawable.legacy_home_result_ic_timecoracao),
                    null,
                    Modifier.size(width = 41.dp, height = 34.dp)
                )
            }
            Column(Modifier.padding(start = if (code == "timemania") 16.dp else 0.dp)) {
                Text(
                    if (
                        code == uiStrings.getString(DeliveryR.string.ui_timemania_9b0b6c)
                    ) {
                        uiStrings.getString(
                            DeliveryR.string.ui_time_do_coracao_918360
                        )
                    } else {
                        uiStrings.getString(DeliveryR.string.ui_mes_de_sorte_4fd420)
                    },
                    color = color,
                    style = homeResultTextStyle.copy(fontSize = 14.2.sp),
                    modifier = Modifier.uiId("home.0127")
                )
                Text(
                    (
                        if (
                            code == uiStrings.getString(DeliveryR.string.ui_timemania_9b0b6c)
                        ) {
                            result.favoriteTeamReward?.selection
                        } else {
                            result.luckyMonthReward?.selection
                        }
                        ) ?: uiStrings.getString(DeliveryR.string.ui_texto_a03b22),
                    color = color,
                    style = homeResultTextStyle.copy(fontSize = 14.2.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .uiId("home.0128")
                )
            }
        }
    }
}
