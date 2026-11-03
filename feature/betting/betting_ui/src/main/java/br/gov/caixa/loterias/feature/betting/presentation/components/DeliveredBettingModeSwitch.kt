package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
internal fun BettingSurpriseModeSwitch(state: BettingGameUiState, onManual: () -> Unit, onSurprise: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val surprise = state.mode == BetEntryMode.SURPRISE
    val modalityStyle = loteriasCaixaDSLotteryStyle(state.game?.modality?.id ?: 0)
    val graphite = LoteriasCaixaDSColorTokens.SocialGraphite
    Row(
        Modifier.padding(ControlPadding).uiId("bet.0040").toggleable(
            value = surprise,
            role = Role.Switch,
            onValueChange = { checked -> if (checked) onSurprise() else onManual() }
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(width = SelectorHeight, height = SwitchTrackHeight)
                .legacyAutomationId("betting-surprise-switch-control")
                .background(
                    color = if (surprise) modalityStyle.light else LoteriasCaixaDSColorTokens.White,
                    shape = RoundedCornerShape(SwitchCorner)
                )
                .border(
                    width = 1.dp,
                    color = if (surprise) modalityStyle.light else graphite,
                    shape = RoundedCornerShape(SwitchCorner)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                Modifier.offset(x = if (surprise) ThumbOffset else 0.dp)
                    .size(IconSize)
                    .background(
                        color = if (surprise) LoteriasCaixaDSColorTokens.White else graphite,
                        shape = CircleShape
                    )
                    .then(
                        if (surprise) Modifier.border(2.dp, graphite, CircleShape) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(DeliveryR.drawable.ic_surpresinha),
                    contentDescription = null,
                    tint = if (surprise) modalityStyle.light else LoteriasCaixaDSColorTokens.White,
                    modifier = (Modifier.size(width = ControlText, height = SwitchCorner)).uiId("bet.0041")
                )
            }
        }
        // O interruptor nativo ocupa 48dp; a trilha desenhada acima ocupa 45dp.
        Text(
            uiStrings.getString(LocalizedR.string.ui_surpresinha_34302e),
            (Modifier.padding(start = SwitchLabelGap)).uiId("bet.0042"),
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = ContentInsetFontSize,
            color = graphite,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
    }
}

@Composable
internal fun BettingModeTutorialAction(onTutorial: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val graphite = LoteriasCaixaDSColorTokens.SocialGraphite
    Row(
        Modifier.uiId("bet.0043").clickable(enabled = onTutorial != null) {
            onTutorial?.invoke()
        }.padding(ControlPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_como_jogar_ceb452),
            (Modifier.padding(end = ContentGap)).uiId("bet.0044"),
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = ContentInsetFontSize,
            color = graphite,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
        Icon(painterResource(DeliveryR.drawable.ic_info), null, (Modifier.size(IconSize)).uiId("bet.0045"), tint = graphite)
    }
}

private val SwitchLabelGap = 11.dp
private val SwitchCorner = 13.dp
private val ControlText = 14.dp
private val ContentInsetFontSize = 16.sp
private val ThumbOffset = 21.dp
private val IconSize = 24.dp
private val SwitchTrackHeight = 26.dp
private val ControlPadding = 4.dp
private val SelectorHeight = 45.dp
private val ContentGap = 8.dp
