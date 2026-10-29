package br.gov.caixa.loterias.feature.payment.presentation.state

internal fun reducePaymentState(state: PaymentUiState, action: PaymentAction): PaymentUiState = when (action) {
    is PaymentAction.MethodsLoaded,
    is PaymentAction.CheckoutMethodsLoaded,
    PaymentAction.RegistrationFinished,
    is PaymentAction.NoticeConsumed,
    PaymentAction.PrePurchaseAccepted,
    is PaymentAction.DamValidated,
    -> reducePaymentMethods(state, action)

    PaymentAction.PixGenerationStarted,
    PaymentAction.PixRecoveryRequired,
    is PaymentAction.PixGenerated,
    PaymentAction.PixStatusCheckStarted,
    is PaymentAction.PixRedirectReceived,
    is PaymentAction.PixNoticeReceived,
    is PaymentAction.PixStatusReceived,
    PaymentAction.PixStatusDismissed,
    -> reducePaymentPix(state, action)

    PaymentAction.CardDeletionStarted,
    is PaymentAction.CardDeleted,
    PaymentAction.LoadingStarted,
    PaymentAction.ErrorDismissed,
    PaymentAction.DamMessageDismissed,
    PaymentAction.RedirectConsumed,
    PaymentAction.RedirectResumed,
    is PaymentAction.CardDeletionRequested,
    PaymentAction.CardDeletionCancelled,
    is PaymentAction.CardAdditionRequested,
    PaymentAction.CardAdditionCancelled,
    is PaymentAction.Failed,
    -> reducePaymentCards(state, action)
}

private fun reducePaymentMethods(state: PaymentUiState, action: PaymentAction): PaymentUiState = when (action) {
    is PaymentAction.MethodsLoaded -> state.copy(loading = false, methods = action.methods, cards = action.cards)

    is PaymentAction.CheckoutMethodsLoaded -> state.copy(
        loading = false,
        methods = action.methods,
        cards = action.cards,
        cartTotal = action.total,
        showPrePurchaseConfirmation = true
    )

    PaymentAction.RegistrationFinished -> state.copy(registrationPending = false)

    is PaymentAction.NoticeConsumed -> state.copy(
        notice = null,
        redirectPaused = action.moreBets,
        registrationPending = action.registration
    )

    PaymentAction.PrePurchaseAccepted -> state.copy(showPrePurchaseConfirmation = false, loading = true, errorMessage = null)

    is PaymentAction.DamValidated -> state.copy(
        loading = false,
        damMessage = action.message.takeIf { action.blocked }
    )

    else -> state
}

private fun reducePaymentPix(state: PaymentUiState, action: PaymentAction): PaymentUiState = when (action) {
    PaymentAction.PixGenerationStarted -> state.copy(loading = true, errorMessage = null, pixApproved = false, pixRejected = false)

    PaymentAction.PixRecoveryRequired -> state.copy(pixRecoveryRequired = true)

    is PaymentAction.PixGenerated -> state.copy(loading = false, pix = action.pix)

    PaymentAction.PixStatusCheckStarted -> state.copy(checkingPix = true, errorMessage = null)

    is PaymentAction.PixRedirectReceived -> state.copy(checkingPix = false, pixRejected = false, redirect = action.destination)

    is PaymentAction.PixNoticeReceived -> state.copy(checkingPix = false, pixRejected = false, notice = action.presentation)

    is PaymentAction.PixStatusReceived -> state.copy(
        checkingPix = false,
        pixApproved = action.completed,
        pixRejected = action.rejected
    )

    PaymentAction.PixStatusDismissed -> state.copy(pixRejected = false)

    else -> state
}

private fun reducePaymentCards(state: PaymentUiState, action: PaymentAction): PaymentUiState = when (action) {
    PaymentAction.CardDeletionStarted -> state.copy(loading = true, deleteCandidate = null)
    is PaymentAction.CardDeleted -> state.copy(loading = false, cards = state.cards + (action.methodId to action.cards))
    PaymentAction.LoadingStarted -> state.copy(loading = true, errorMessage = null)
    PaymentAction.ErrorDismissed -> state.copy(errorMessage = null)
    PaymentAction.DamMessageDismissed -> state.copy(damMessage = null)
    PaymentAction.RedirectConsumed -> state.copy(redirect = null, redirectPaused = true)
    PaymentAction.RedirectResumed -> state.copy(redirectPaused = false)
    is PaymentAction.CardDeletionRequested -> state.copy(deleteCandidate = action.card)
    PaymentAction.CardDeletionCancelled -> state.copy(deleteCandidate = null)
    is PaymentAction.CardAdditionRequested -> state.copy(pendingAddMethodId = action.methodId)
    PaymentAction.CardAdditionCancelled -> state.copy(pendingAddMethodId = null)
    is PaymentAction.Failed -> failedPaymentState(state, action)
    else -> state
}

private fun failedPaymentState(state: PaymentUiState, action: PaymentAction.Failed): PaymentUiState = state.copy(
    loading = false,
    checkingPix = false,
    errorMessage = action.message
)
