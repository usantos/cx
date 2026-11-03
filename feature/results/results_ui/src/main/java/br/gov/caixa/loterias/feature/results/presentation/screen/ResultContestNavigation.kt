package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.presentation.mapper.contestButtonLabel

@Composable
internal fun ResultContestNavigation(
    currentContest: Int?,
    previousEnabled: Boolean,
    nextEnabled: Boolean,
    onPreviousContest: () -> Unit,
    onNextContest: () -> Unit,
    onBack: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.fillMaxWidth().height(94.dp).background(LoteriasCaixaDSColorTokens.BetFooter)) {
        Row(
            Modifier.align(Alignment.CenterStart).padding(start = 5.dp)
                .alpha(if (previousEnabled) 1f else DISABLED_CONTEST_ALPHA)
                .uiId("results.0081").clickable(enabled = previousEnabled, onClick = onPreviousContest),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                LoteriasCaixaDSIcons.Back,
                contentDescription = null,
                modifier = (Modifier.size(12.dp, 18.dp)).uiId("results.0082"),
                tint = LoteriasCaixaDSColorTokens.BetLink
            )
            Text(
                contestButtonLabel(currentContest?.minus(1), "anterior"),
                (Modifier.padding(start = 13.dp)).uiId("results.0083"),
                color = LoteriasCaixaDSColorTokens.BetLink,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = LoteriasCaixaDSFontFamily,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
        }
        Row(
            Modifier.align(Alignment.CenterEnd).padding(end = 5.dp)
                .alpha(if (nextEnabled) 1f else DISABLED_CONTEST_ALPHA)
                .uiId("results.0084").clickable(enabled = nextEnabled, onClick = onNextContest),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                contestButtonLabel(currentContest?.plus(1), "seguinte"),
                modifier = (Modifier.padding(end = 13.dp)).uiId("results.0085"),
                color = LoteriasCaixaDSColorTokens.BetLink,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = LoteriasCaixaDSFontFamily,
                    textAlign = TextAlign.End,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)
                )
            )
            Icon(
                LoteriasCaixaDSIcons.Back,
                contentDescription = null,
                modifier = (Modifier.size(12.dp, 18.dp).rotate(NEXT_CONTEST_ARROW_ROTATION_DEGREES))
                    .uiId("results.0086"),
                tint = LoteriasCaixaDSColorTokens.BetLink
            )
        }
        Box(
            Modifier.align(Alignment.TopCenter).offset(y = (-41).dp).width(184.dp).height(84.dp)
                .background(LoteriasCaixaDSColorTokens.NotificationText).uiId("results.0087").clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_voltar_59dc92),
                color = LoteriasCaixaDSColorTokens.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("results.0088")
            )
        }
    }
}

private const val DISABLED_CONTEST_ALPHA = .5f
private const val NEXT_CONTEST_ARROW_ROTATION_DEGREES = 180f
