package br.gov.caixa.loterias.feature.betting.presentation.route

import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

internal fun requestBettingCart(viewModel: BettingGameViewModel, cartBetCount: Int, onCart: (() -> Unit)?, onEmptyCart: () -> Unit) {
    if (cartBetCount > 0) viewModel.checkpointNavigation { onCart?.invoke() } else onEmptyCart()
}

internal fun requestBettingBack(viewModel: BettingGameViewModel, onBack: () -> Unit, onPendingEdits: () -> Unit) {
    viewModel.requestBack {
        if (viewModel.state.value.hasPendingEdits) onPendingEdits() else viewModel.checkpointNavigation(onBack)
    }
}

internal fun clearBettingStep(viewModel: BettingGameViewModel, currentState: () -> BettingGameUiState, onLotecaCleared: () -> Unit) {
    viewModel.clearCurrentStep()
    if (currentState().game?.modality?.id == LOTECA_ID) onLotecaCleared()
}

internal fun selectBettingNumberQuantity(
    viewModel: BettingGameViewModel,
    currentState: () -> BettingGameUiState,
    quantity: Int,
    onLotecaReduced: () -> Unit,
) {
    viewModel.chooseNumberQuantity(quantity)
    if (currentState().game?.modality?.id == LOTECA_ID && quantity < currentState().numberQuantity) onLotecaReduced()
}
