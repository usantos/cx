package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

internal val homeResultTextStyle = TextStyle(
    fontFamily = LoteriasCaixaDSFontFamily,
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

internal fun homeResultContentColor(code: String): Color =
    if (normalizeModality(code) == "timemania") LoteriasCaixaDSColorTokens.TimemaniaContent else LoteriasCaixaDSColorTokens.White
