package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome

@Composable
fun BettingLotecaContentDelivered(
    state: BettingGameUiState,
    onOutcomeSelected: (Int, LotecaOutcome) -> Unit,
    onQuantity: () -> Unit,
    onHelp: () -> Unit,
    descriptionBindingKey: Int = 0,
) {
    requireNotNull(state.game)
    val metrics = TextStyle(lineHeight = ContentInsetFontSize, platformStyle = PlatformTextStyle(includeFontPadding = true))
    // TextView lê textSize do XML em pixels inteiros; a largura fracionária muda a quebra do Flow.
    val counterTextSize = with(androidx.compose.ui.platform.LocalDensity.current) { ControlTextFontSize.roundToPx().toSp() }
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.SimulationToolbar).padding(horizontal = ContentInset)
        ) {
            BettingLotecaHeadingDelivered(onHelp, metrics)
            BettingLotecaCountersDelivered(state, metrics, counterTextSize)
        }
        BettingLotecaQuantityDelivered(state, onQuantity, metrics)
        Spacer(Modifier.height(ContentGap))
        BettingLotecaMatchesDelivered(
            state,
            onOutcomeSelected,
            descriptionBindingKey,
            metrics,
            Modifier.fillMaxWidth().padding(horizontal = ContentGap).weight(1f).uiId("bet.0251").verticalScroll(rememberScrollState())
        )
    }
}

private val ControlTextFontSize = 14.sp
private val ContentInset = 16.dp
private val ContentInsetFontSize = 16.sp
private val ContentGap = 8.dp
