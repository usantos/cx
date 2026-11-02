package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

@Composable
internal fun CircularCounterButton(text: String, onClick: () -> Unit, muted: Boolean = false) {
    LoteriasCaixaDSBettingButton(
        text,
        onClick,
        Modifier.size(
            CounterHeight
        ),
        color = if (muted) LoteriasCaixaDS.colors.line else LoteriasCaixaDSColorTokens.SocialGraphite,
        content = LoteriasCaixaDSColorTokens.White,
        textSize = COUNTER_BUTTON_TEXT_SIZE,
        textSizeInDp = true,
        verticalInset = LoteriasCaixaDSBettingButtonDefaults.NativeVerticalInset
    )
}

@Composable
internal fun SurpriseNumberQuantity(state: BettingGameUiState, actions: BettingGameActions, plusMillion: Boolean) {
    Row(Modifier.padding(top = QuantityTopGap), verticalAlignment = Alignment.CenterVertically) {
        CircularCounterButton("-", {
            if (plusMillion) {
                state.numberQuantityOptions.lastOrNull { it < state.numberQuantity }?.let(actions.chooseNumberQuantity)
            } else {
                actions.decreaseSurpriseQuantity()
            }
        }, muted = if (plusMillion) state.numberQuantity == state.numberQuantityOptions.firstOrNull() else state.surpriseQuantity == 1)
        Column(
            Modifier.padding(horizontal = ContentInset).height(CounterHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                (if (plusMillion) state.numberQuantity else state.surpriseQuantity).toString(),
                (Modifier.width(ControlSize))
                    .uiId("bet.0242"),
                textAlign = TextAlign.Center,
                fontFamily = LoteriasCaixaDSFontFamily,
                fontSize = QuantityTextFontSize,
                color = LoteriasCaixaDSColorTokens.SocialGraphite
            )
            Box(
                Modifier.padding(
                    top = QuantityUnderlineGap
                ).width(ControlSize).height(2.dp).background(LoteriasCaixaDSColorTokens.SocialGraphite)
            )
        }
        CircularCounterButton("+", {
            if (plusMillion) {
                state.numberQuantityOptions.firstOrNull { it > state.numberQuantity }?.let(actions.chooseNumberQuantity)
            } else {
                actions.increaseSurpriseQuantity()
            }
        })
    }
}

@Composable
internal fun SurpriseCloverQuantity(state: BettingGameUiState, actions: BettingGameActions, minimumClovers: Int) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_escolha_a_quantidade_de_trevos_ff6a2c),
        (Modifier.fillMaxWidth().padding(start = SelectorHeight, end = SelectorHeight, top = SectionGap)).uiId("bet.0243"),
        fontFamily = LoteriasCaixaDSFontFamily,
        fontSize = ContentInsetFontSize,
        color = LoteriasCaixaDSColorTokens.SocialGraphite,
        textAlign = TextAlign.Center,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
    )
    Row(Modifier.padding(top = QuantityTopGap), verticalAlignment = Alignment.CenterVertically) {
        CircularCounterButton(
            "-",
            actions.decreaseSurpriseCloverQuantity,
            muted = state.surpriseCloverQuantity == minimumClovers
        )
        Column(
            Modifier.padding(horizontal = ContentInset).height(CounterHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                state.surpriseCloverQuantity.toString(),
                (Modifier.width(ControlSize)).uiId("bet.0244"),
                textAlign = TextAlign.Center,
                fontFamily = LoteriasCaixaDSFontFamily,
                fontSize = QuantityTextFontSize,
                color = LoteriasCaixaDSColorTokens.SocialGraphite
            )
            Box(
                Modifier.padding(
                    top = QuantityUnderlineGap
                ).width(ControlSize).height(2.dp).background(LoteriasCaixaDSColorTokens.SocialGraphite)
            )
        }
        CircularCounterButton("+", actions.increaseSurpriseCloverQuantity)
    }
}

private val ContentInset = 16.dp
private val ContentInsetFontSize = 16.sp
private val QuantityTextFontSize = 18.sp
private val SectionGap = 20.dp
private val QuantityTopGap = 30.dp
private val ControlSize = 40.dp
private val SelectorHeight = 45.dp
private val CounterHeight = 50.dp
private val QuantityUnderlineGap = 6.dp

private const val COUNTER_BUTTON_TEXT_SIZE = 12f
