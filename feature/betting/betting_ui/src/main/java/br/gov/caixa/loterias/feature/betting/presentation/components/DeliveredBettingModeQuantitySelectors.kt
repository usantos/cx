package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.mapper.recurringLabel
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.MAIS_MILIONARIA_ID
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
internal fun BettingModeQuantitySelectors(state: BettingGameUiState, onNumberQuantity: () -> Unit, onRecurring: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val graphite = LoteriasCaixaDSColorTokens.SocialGraphite
    Row(
        Modifier.fillMaxWidth().padding(start = ContentInset, top = ContentInset),
        horizontalArrangement = Arrangement.spacedBy(ControlCorner)
    ) {
        val style = loteriasCaixaDSLotteryStyle(state.game?.modality?.id ?: 0)
        val cloverSelector =
            state.game?.modality?.id == MAIS_MILIONARIA_ID && state.selectionStep == BetSelectionStep.COMPLEMENT &&
                state.mode == BetEntryMode.MANUAL
        LoteriasCaixaDSBettingButton(
            if (cloverSelector) {
                "${state.manualCloverQuantity} trevos"
            } else {
                uiStrings.getString(
                    LocalizedR.string.ui_1_s_numeros_f145a3,
                    state.numberQuantity.toString()
                )
            },
            onNumberQuantity,
            Modifier.width(
                if (cloverSelector) CloverSelectorWidth else NumberSelectorWidth
            ).height(SelectorHeight),
            color = style.light, content = style.content,
            stroke = style.light,
            textSize = SELECTOR_TEXT_SIZE,
            corner = SELECTOR_CORNER_RADIUS,
            icon = DeliveryR.drawable.ic_arrow_down,
            semiBold = !cloverSelector,
            verticalInset = LoteriasCaixaDSBettingButtonDefaults.NativeVerticalInset
        )
        LoteriasCaixaDSBettingButton(
            if (state.recurringDraws == 0) "Teimosinha" else recurringLabel(state.recurringDraws), onRecurring,
            Modifier.width(NumberSelectorWidth).height(SelectorHeight), color = LoteriasCaixaDSColorTokens.White, content = graphite,
            stroke = LoteriasCaixaDS.colors.graphite,
            textSize = SELECTOR_TEXT_SIZE,
            corner = SELECTOR_CORNER_RADIUS,
            icon = DeliveryR.drawable.ic_arrow_down,
            verticalInset = LoteriasCaixaDSBettingButtonDefaults.NativeVerticalInset
        )
        if (state.requiresManualComplement) {
            Spacer(Modifier.weight(1f))
            Text(
                if (state.selectionStep ==
                    BetSelectionStep.COMPLEMENT
                ) {
                    uiStrings.getString(LocalizedR.string.format_selection_step, 2, 2)
                } else {
                    uiStrings.getString(LocalizedR.string.format_selection_step, 1, 2)
                },
                (Modifier.padding(end = ContentInset).height(ControlSize).wrapContentHeight(Alignment.CenterVertically)).uiId("bet.0046"),
                fontFamily = LoteriasCaixaDSFontFamily,
                fontSize = ControlTextFontSize,
                color = graphite,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
            )
        }
    }
}

private val CloverSelectorWidth = 100.dp
private val NumberSelectorWidth = 112.dp
private val ControlCorner = 12.dp
private val ControlTextFontSize = 14.sp
private val ContentInset = 16.dp
private val ControlSize = 40.dp
private val SelectorHeight = 45.dp

private const val SELECTOR_TEXT_SIZE = 14f

private const val SELECTOR_CORNER_RADIUS = 12f
