package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.presentation.state.BetEntryMode
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

@Composable
internal fun BettingModeToolbar(
    state: BettingGameUiState,
    onManual: () -> Unit,
    onSurprise: () -> Unit,
    onNumberQuantity: () -> Unit,
    onRecurring: () -> Unit,
    onTutorial: (() -> Unit)?,
) {
    val modalityStyle = loteriasCaixaDSLotteryStyle(state.game?.modality?.id ?: 0)
    val surprise = state.mode == BetEntryMode.SURPRISE
    // SwitchMaterial mede 48dp; seu contêiner nativo adiciona 4dp de cada lado.
    val nativeRowHeight = with(androidx.compose.ui.platform.LocalDensity.current) {
        (TouchTargetHeight.roundToPx() + 2 * ControlPadding.roundToPx()).toDp()
    }
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = nativeRowHeight).legacyAutomationId("betting-mode-toolbar").background(
                if (surprise) {
                    modalityStyle.light.copy(
                        alpha =
                        SURPRISE_BACKGROUND_ALPHA
                    )
                } else {
                    LoteriasCaixaDSColorTokens.SimulationToolbar
                }
            ).padding(horizontal = ContentInset),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BettingSurpriseModeSwitch(state, onManual, onSurprise)
            Spacer(Modifier.weight(1f))
            BettingModeTutorialAction(onTutorial)
        }
        BettingModeQuantitySelectors(state, onNumberQuantity, onRecurring)
    }
}

private val ContentInset = 16.dp
private val ControlPadding = 4.dp
private val TouchTargetHeight = 48.dp

private const val SURPRISE_BACKGROUND_ALPHA = 26f / 255f
