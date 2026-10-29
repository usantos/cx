package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.components.DrawPublicSuperSevenResult
import br.gov.caixa.loterias.feature.results.presentation.components.DrawSportsResults
import br.gov.caixa.loterias.feature.results.presentation.components.ResultPayouts
import br.gov.caixa.loterias.feature.results.presentation.mapper.modalityName
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultDateSpokenDescription
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultDrawSpokenDescription
import kotlin.math.roundToInt

@Composable
internal fun ResultContent(result: DrawResult, modality: String) {
    val style = caixaDSLotteryStyle(0, modalityName(modality))
    val numberColor = if (style !=
        resolveCaixaDSLotteryStyle(0, modalityName(modality), CaixaDSCampaign.DEFAULT)
    ) {
        style.numeral
    } else {
        style.light
    }
    val gap16 = with(LocalDensity.current) { 16.toDp() }
    val gap18 = with(LocalDensity.current) { 18.toDp() }
    val bottom180 = with(LocalDensity.current) { 180.toDp() }
    val bodySize = with(LocalDensity.current) { 14.2.sp.toPx().roundToInt().toSp() }
    val bodyStyle = TextStyle(
        fontFamily = CaixaDSFontFamily,
        fontSize = bodySize,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
    val payoutStyle = bodyStyle.copy(fontSize = 16.sp)
    Column(Modifier.fillMaxWidth().padding(top = 10.dp)) {
        val drawReadout = if (result.lotecaMatches.isNotEmpty() || result.lotogolMatches.isNotEmpty()) {
            Modifier
        } else {
            Modifier.clearAndSetSemantics { contentDescription = resultDrawSpokenDescription(result, modality) }
        }
        Column(Modifier.fillMaxWidth().then(drawReadout)) {
            ResultPrimaryDraw(result, modality, style, bodyStyle, numberColor)
            ResultDrawExtras(result, modality, style, bodyStyle)
            ResultDrawDate(result, modality, style.darkContent, bodyStyle)
        }
        Box(Modifier.fillMaxWidth().padding(top = 10.dp).height(1.dp).background(style.light))
        ResultPayouts(result, modality, style, payoutStyle, gap16, gap18, bottom180)
    }
}

@Composable
private fun NumberRows(numbers: List<Int>, modifier: Modifier = Modifier, color: Color) {
    val columns = if (numbers.size > 6) 4 else 3
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        numbers.chunked(columns).forEach { row ->
            Row(Modifier.width((columns * 56).dp).padding(bottom = 10.dp)) {
                row.forEach { number ->
                    Box(Modifier.width(56.dp).height(40.dp), contentAlignment = Alignment.Center) {
                        Box(Modifier.size(40.dp).background(CaixaDSColorTokens.White, CircleShape), contentAlignment = Alignment.Center) {
                            Text(
                                number.toString().padStart(2, '0'),
                                color = color,
                                style = TextStyle(
                                    fontFamily = CaixaDSFontFamily,
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
private fun ResultPrimaryDraw(result: DrawResult, modality: String, style: CaixaDSLotteryStyle, bodyStyle: TextStyle, numberColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (result.lotecaMatches.isNotEmpty() || result.lotogolMatches.isNotEmpty()) {
        Column(Modifier.padding(horizontal = 30.dp)) { DrawSportsResults(result, style.darkContent) }
    } else if (modality == "supersete") {
        DrawPublicSuperSevenResult(
            result.firstDrawNumberColumns.takeIf { it.size == 7 }
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

@Composable
private fun ColumnScope.ResultDrawExtras(result: DrawResult, modality: String, style: CaixaDSLotteryStyle, bodyStyle: TextStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (result.clovers.isNotEmpty()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_trevos_sorteados_2645f8),
            (Modifier.padding(start = 30.dp, top = 10.dp)).uiId("results.0076"),
            color = style.darkContent,
            style = bodyStyle
        )
        Row(Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)) {
            result.clovers.sorted().forEach { clover ->
                Box(
                    Modifier.width(56.dp).height(40.dp).semantics(mergeDescendants = true) {
                        contentDescription = uiStrings.getString(
                            LocalizedR.string.format_clover_description,
                            clover
                        )
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(R.drawable.legacy_result_ic_item_trevo_selecionado), null, Modifier.size(40.dp))
                    Text(
                        clover.toString().padStart(2, '0'),
                        color = style.light,
                        style = bodyStyle.copy(fontWeight = FontWeight.Bold, fontSize = 14.1.sp),
                        modifier = Modifier.uiId("results.0077")
                    )
                }
            }
        }
    }
    val drawnSelection = when (modality) {
        "timemania" -> uiStrings.getString(LocalizedR.string.ui_time_do_coracao_918360) to result.favoriteTeamReward?.selection

        "diadesorte" -> uiStrings.getString(LocalizedR.string.ui_mes_de_sorte_4fd420) to
            (result.luckyMonthReward?.selection ?: "???")

        else -> null
    }
    drawnSelection?.let { (label, selection) ->
        Row(Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp)) {
            if (modality == "timemania") {
                Image(
                    painterResource(R.drawable.legacy_result_ic_timecoracao),
                    null,
                    Modifier.width(41.dp).height(34.dp)
                )
            }
            Column(Modifier.padding(start = 8.dp)) {
                Text(label, color = style.darkContent, style = bodyStyle, modifier = Modifier.uiId("results.0078"))
                Text(
                    selection.orEmpty(),
                    color = style.darkContent,
                    style = bodyStyle.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.uiId("results.0079")
                )
            }
        }
    }
}

@Composable
private fun ResultDrawDate(result: DrawResult, modality: String, color: Color, bodyStyle: TextStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.clearAndSetSemantics { contentDescription = resultDateSpokenDescription(result.drawDate, modality) }) {
        Text(
            buildAnnotatedString {
                append(
                    if (modality == "loteca") {
                        uiStrings.getString(LocalizedR.string.ui_data_do_resultado_1ae295)
                    } else {
                        uiStrings.getString(LocalizedR.string.ui_data_do_sorteio_088973)
                    }
                )
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(result.drawDate) }
            },
            (Modifier.padding(start = 30.dp, top = 10.dp)).uiId("results.0080"),
            color = color,
            style = bodyStyle
        )
    }
}
