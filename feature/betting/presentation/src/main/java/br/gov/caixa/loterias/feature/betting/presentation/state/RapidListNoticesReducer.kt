package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceRapidListNotices(state: RapidListUiState, action: RapidListAction): RapidListUiState = when (action) {
    is RapidListAction.ServiceRedirectConsumed -> if (state.serviceFailure.redirect !== action.expected) {
        state
    } else {
        state.copy(serviceFailure = state.serviceFailure.copy(redirect = null))
    }

    is RapidListAction.ValidationRedirectConsumed -> if (state.validationFailure.redirect !== action.expected) {
        state
    } else {
        state.copy(validationFailure = state.validationFailure.copy(redirect = null))
    }

    is RapidListAction.ServiceFailureReceived -> reduceRapidListServiceFailureReceived(state, action)

    is RapidListAction.ValidationRejected -> reduceRapidListValidationRejected(state, action)

    is RapidListAction.ServiceNoticeDismissed -> reduceRapidListServiceNoticeDismissed(state, action)

    is RapidListAction.ServiceNoticeAccepted -> reduceRapidListServiceNoticeAccepted(state, action)

    is RapidListAction.ValidationNoticeDismissed -> reduceRapidListValidationNoticeDismissed(state, action)

    is RapidListAction.ValidationNoticeAccepted -> reduceRapidListValidationNoticeAccepted(state, action)

    else -> state
}

private fun reduceRapidListServiceFailureReceived(
    state: RapidListUiState,
    action: RapidListAction.ServiceFailureReceived,
): RapidListUiState = run {
    val it = state
    it.copy(loading = false, serviceFailure = action.presentation)
}

private fun reduceRapidListValidationRejected(state: RapidListUiState, action: RapidListAction.ValidationRejected): RapidListUiState = run {
    val it = state
    it.copy(loading = false, readyForPayment = false, validationFailure = action.presentation)
}

private fun reduceRapidListServiceNoticeDismissed(
    state: RapidListUiState,
    action: RapidListAction.ServiceNoticeDismissed,
): RapidListUiState = run {
    val it = state
    it.copy(serviceFailure = it.serviceFailure.dismiss(action.expected))
}

private fun reduceRapidListServiceNoticeAccepted(state: RapidListUiState, action: RapidListAction.ServiceNoticeAccepted): RapidListUiState =
    run {
        val it = state
        if (it.serviceFailure.notice !== action.expected) it else it.copy(serviceFailure = action.presentation)
    }

private fun reduceRapidListValidationNoticeDismissed(
    state: RapidListUiState,
    action: RapidListAction.ValidationNoticeDismissed,
): RapidListUiState = run {
    val it = state
    it.copy(validationFailure = it.validationFailure.dismiss(action.expected))
}

private fun reduceRapidListValidationNoticeAccepted(
    state: RapidListUiState,
    action: RapidListAction.ValidationNoticeAccepted,
): RapidListUiState = run {
    val it = state
    if (it.validationFailure.notice !== action.expected) it else it.copy(validationFailure = action.presentation)
}
