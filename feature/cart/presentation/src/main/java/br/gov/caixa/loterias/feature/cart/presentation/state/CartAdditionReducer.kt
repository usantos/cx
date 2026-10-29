package br.gov.caixa.loterias.feature.cart.presentation.state

sealed interface CartAdditionAction {
    data class ReservationRefreshed(val remainingSeconds: Int) : CartAdditionAction
}

fun reduceCartAdditionState(state: CartAdditionUiState, action: CartAdditionAction): CartAdditionUiState = when (action) {
    is CartAdditionAction.ReservationRefreshed -> state.copy(
        remainingSeconds = action.remainingSeconds,
        expired =
            action.remainingSeconds == 0
    )
}
