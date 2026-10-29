package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceRapidListCart(state: RapidListUiState, action: RapidListAction): RapidListUiState = when (action) {
    is RapidListAction.CartReloaded -> reduceRapidListCartReloaded(state, action)
    is RapidListAction.RecoveryRequired -> reduceRapidListRecoveryRequired(state, action)
    RapidListAction.CartOperationStarted -> reduceRapidListCartOperationStarted(state)
    is RapidListAction.CartLoaded -> reduceRapidListCartLoaded(state, action)
    RapidListAction.LoadingStarted -> reduceRapidListLoadingStarted(state)
    is RapidListAction.EmptyCartRejected -> reduceRapidListEmptyCartRejected(state, action)
    RapidListAction.PaymentReady -> reduceRapidListPaymentReady(state)
    RapidListAction.ErrorDismissed -> reduceRapidListErrorDismissed(state)
    RapidListAction.PaymentReadyConsumed -> reduceRapidListPaymentReadyConsumed(state)
    is RapidListAction.Failed -> reduceRapidListFailed(state, action)
    else -> state
}

private fun reduceRapidListCartReloaded(state: RapidListUiState, action: RapidListAction.CartReloaded): RapidListUiState = run {
    val it = state
    it.copy(loading = false, cart = action.cart, readyForPayment = false)
}

private fun reduceRapidListRecoveryRequired(state: RapidListUiState, action: RapidListAction.RecoveryRequired): RapidListUiState = run {
    val it = state
    it.copy(loading = false, readyForPayment = false, errorMessage = action.message)
}

private fun reduceRapidListCartOperationStarted(state: RapidListUiState): RapidListUiState = run {
    val it = state
    it.copy(loading = true, errorMessage = null, readyForPayment = false)
}

private fun reduceRapidListCartLoaded(state: RapidListUiState, action: RapidListAction.CartLoaded): RapidListUiState = run {
    val it = state
    it.copy(loading = false, cart = action.cart)
}

private fun reduceRapidListLoadingStarted(state: RapidListUiState): RapidListUiState = run {
    val it = state
    it.copy(loading = true, errorMessage = null)
}

private fun reduceRapidListEmptyCartRejected(state: RapidListUiState, action: RapidListAction.EmptyCartRejected): RapidListUiState = run {
    val it = state
    it.copy(errorMessage = action.message)
}

private fun reduceRapidListPaymentReady(state: RapidListUiState): RapidListUiState = run {
    val it = state
    it.copy(loading = false, readyForPayment = true)
}

private fun reduceRapidListErrorDismissed(state: RapidListUiState): RapidListUiState = run {
    val it = state
    it.copy(errorMessage = null)
}

private fun reduceRapidListPaymentReadyConsumed(state: RapidListUiState): RapidListUiState = run {
    val it = state
    it.copy(readyForPayment = false)
}

private fun reduceRapidListFailed(state: RapidListUiState, action: RapidListAction.Failed): RapidListUiState = run {
    val it = state
    it.copy(
        loading = false,
        errorMessage = action.message
    )
}
