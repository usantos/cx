package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveLoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.components.ResultPayouts
import br.gov.caixa.loterias.feature.results.presentation.mapper.modalityName
import br.gov.caixa.loterias.feature.results.presentation.mapper.resultDrawSpokenDescription
import kotlin.math.roundToInt

@Composable
internal fun ResultContent(result: DrawResult, modality: String) {
    val style = loteriasCaixaDSLotteryStyle(0, modalityName(modality))
    val numberColor = if (style !=
        resolveLoteriasCaixaDSLotteryStyle(0, modalityName(modality), LoteriasCaixaDSCampaign.DEFAULT)
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
        fontFamily = LoteriasCaixaDSFontFamily,
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
