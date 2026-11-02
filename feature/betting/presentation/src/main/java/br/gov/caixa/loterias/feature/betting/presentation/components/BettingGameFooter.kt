package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.presentation.state.BetSelectionStep
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

@Composable
internal fun BettingLegacyFooter(
    state: BettingGameUiState,
    actions: BettingGameActions,
    cartTotal: java.math.BigDecimal,
    cartCount: Int,
    onCart: (() -> Unit)?,
    onClear: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = loteriasCaixaDSLotteryStyle(state.game?.modality?.id ?: 0)
    val hasSelection = bettingFooterHasSelection(state)
    val randomEnabled = bettingFooterRandomEnabled(state)
    val randomLabel = bettingFooterRandomLabel(state, hasSelection, uiStrings)
    Column(Modifier.fillMaxWidth().padding(horizontal = FooterHorizontalInset).background(LoteriasCaixaDSColorTokens.White)) {
        BettingFooterManualActions(state, actions, hasSelection, randomEnabled, randomLabel, onClear)
        LoteriasCaixaDSBettingButton(
            state.primaryActionLabel, actions.primaryAction, Modifier.fillMaxWidth().height(ActionHeight),
            if (state.canPerformPrimaryAction) style.light else LoteriasCaixaDSColorTokens.FavoriteFilterCanvas,
            if (state.canPerformPrimaryAction) style.content else LoteriasCaixaDSColorTokens.SimulationMuted,
            stroke = if (state.canPerformPrimaryAction) style.light else LoteriasCaixaDSColorTokens.SimulationMuted,
            enabled = state.canPerformPrimaryAction,
            semiBold = state.canPerformPrimaryAction,
            textSize = if (state.selectionStep == BetSelectionStep.NUMBERS &&
                state.requiresManualComplement
            ) {
                COMPLEMENT_ACTION_TEXT_SIZE
            } else {
                PRIMARY_ACTION_TEXT_SIZE
            },
            verticalInset = LoteriasCaixaDSBettingButtonDefaults.NativeVerticalInset
        )
        BettingFooterCartSummary(state, cartTotal, cartCount, onCart)
    }
}

private val FooterHorizontalInset = 24.dp
private val ActionHeight = 52.dp

private const val COMPLEMENT_ACTION_TEXT_SIZE = 14f
private const val PRIMARY_ACTION_TEXT_SIZE = 13f
