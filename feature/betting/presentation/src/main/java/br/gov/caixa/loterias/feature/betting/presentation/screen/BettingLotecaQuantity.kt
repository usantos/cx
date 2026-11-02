package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.style.lotecaNativeTextHeight

@Composable
internal fun BettingLotecaQuantity(state: BettingGameUiState, onQuantity: () -> Unit, metrics: TextStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().padding(start = ContentInset, end = ContentInset, top = ContentInset).height(SelectorHeight),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(Modifier.width(NumberSelectorWidth).height(SelectorHeight).legacyAutomationId("loteca-quantity")) {
            LoteriasCaixaDSBettingButton(
                "${state.numberQuantity} palpites", onQuantity,
                Modifier.fillMaxWidth().fillMaxHeight(),
                LoteriasCaixaDSColorTokens.Loteca,
                LoteriasCaixaDSColorTokens.White,
                textSize = QUANTITY_TEXT_SIZE,
                corner = QUANTITY_CORNER_RADIUS,
                icon = R.drawable.ic_arrow_down, semiBold = true,
                verticalInset = LoteriasCaixaDSBettingButtonDefaults.NativeVerticalInset
            )
        }
        Box(Modifier.height(ControlSize), contentAlignment = Alignment.Center) {
            Text(
                uiStrings.getString(
                    LocalizedR.string.ui_selecionados_1_s_2_s_0a48ce,
                    state.lotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size).toString(),
                    state.numberQuantity.toString()
                ),
                (
                    Modifier.then(
                        lotecaNativeTextHeight(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular, ControlTextFontSize)
                    )
                    )
                    .uiId("bet.0250"),
                fontFamily = LoteriasCaixaDSRegularFontFamily,
                fontSize = ControlTextFontSize,
                color = LoteriasCaixaDSColorTokens.SocialGraphite,
                style = metrics
            )
        }
    }
}

private val NumberSelectorWidth = 112.dp
private val ControlTextFontSize = 14.sp
private val ContentInset = 16.dp
private val ControlSize = 40.dp
private val SelectorHeight = 45.dp

private const val QUANTITY_TEXT_SIZE = 14f

private const val QUANTITY_CORNER_RADIUS = 12f
