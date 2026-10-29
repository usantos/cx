package br.gov.caixa.loterias.feature.home.presentation.state

import java.math.BigDecimal

internal sealed interface HomeLoadingAction : HomeStateAction

internal sealed interface HomeSessionAction : HomeStateAction

internal sealed interface HomeEntryAction : HomeStateAction

internal sealed interface HomeContentAction : HomeStateAction

internal fun reduceHomeState(state: HomeUiState, action: HomeStateAction): HomeUiState = when (action) {
    is HomeLoadingAction -> reduceHomeLoadingState(state, action)
    is HomeSessionAction -> reduceHomeSessionState(state, action)
    is HomeEntryAction -> reduceHomeEntryState(state, action)
    is HomeContentAction -> reduceHomeContentState(state, action)
    is HomeResultsAction -> reduceHomeResultsState(state, action)
    is HomeNotificationAction -> reduceHomeNotificationState(state, action)
}

private fun reduceHomeLoadingState(state: HomeUiState, action: HomeLoadingAction): HomeUiState = when (action) {
    HomeStateAction.ParameterRetryDismissed -> state.copy(parameterRetryMessage = null)

    HomeStateAction.UnavailableParametersDismissed -> state.copy(parametersUnavailable = false, parameterRetryMessage = null)

    HomeStateAction.ParametersLoading -> state.copy(loading = true, errorMessage = null, parameterRetryMessage = null)

    is HomeStateAction.EmptyParametersReceived -> state.copy(
        parametersUnavailable = true,
        errorMessage = action.message,
        parameterRetryMessage = action.retryMessage
    )

    is HomeStateAction.ParametersReceived -> state.copy(
        parameters = action.parameters,
        features = action.features,
        combos = action.combos,
        resaleOffers = action.resaleOffers,
        parametersUnavailable = false,
        errorMessage = null
    )

    is HomeStateAction.ParametersFailed -> state.copy(
        parametersUnavailable = true,
        errorMessage = action.message,
        parameterRetryMessage = action.retryMessage
    )

    HomeStateAction.ParametersLoadingFinished -> state.copy(loading = false)
}

private fun reduceHomeSessionState(state: HomeUiState, action: HomeSessionAction): HomeUiState = when (action) {
    is HomeStateAction.SessionContentObserved -> state.copy(
        localCartReviewRequired =
            action.content?.localCartReviewRequired == true,
        localCartReviewAcknowledged = if (action.content?.localCartReviewRequired ==
            true
        ) {
            state.localCartReviewAcknowledged
        } else {
            false
        },
        unidentifiedPayment = action.content?.unidentifiedPayment == true,
        paymentNoticeAcknowledged = false, latestNotification = action.content?.latestNotification,
        notificationDismissed = if (state.latestNotification ===
            action.content?.latestNotification
        ) {
            state.notificationDismissed
        } else {
            false
        },
        notificationReadError = null,
        notificationNotice = null, notificationRedirect = null,
        unreadNotificationCount = 0,
        cartTotal = action.content?.cart?.total ?: BigDecimal.ZERO,
        cartItemCount = action.content?.cart?.betCount ?: 0, loadingCart = false
    )

    HomeStateAction.LocalCartReviewAcknowledged -> state.copy(localCartReviewAcknowledged = true)

    HomeStateAction.PaymentNoticeAcknowledged -> state.copy(paymentNoticeAcknowledged = true)

    HomeStateAction.HeaderActionsRevealed -> state.copy(headerActionsRevealed = true)
}

private fun reduceHomeEntryState(state: HomeUiState, action: HomeEntryAction): HomeUiState = when (action) {
    HomeStateAction.BetEntryStarted -> state.copy(preparingBet = true, betEntryError = null)
    is HomeStateAction.BetEntryPrepared -> state.copy(preparingBet = false, betEntryDecision = action.decision)
    HomeStateAction.BetEntryConsumed -> state.copy(betEntryDecision = null)
    HomeStateAction.BetEntryErrorDismissed -> state.copy(betEntryError = null)
    HomeStateAction.PoolEntryStarted -> state.copy(preparingBet = true, betEntryError = null)
    is HomeStateAction.PoolEntryPrepared -> state.copy(preparingBet = false, poolEntryDecision = action.decision)
    HomeStateAction.PoolEntryConsumed -> state.copy(poolEntryDecision = null)
    HomeStateAction.OtherBettingEntryStarted -> state.copy(preparingBet = true, betEntryError = null)
    is HomeStateAction.OtherBettingEntryPrepared -> state.copy(preparingBet = false, otherBettingDecision = action.decision)
    HomeStateAction.OtherBettingEntryConsumed -> state.copy(otherBettingDecision = null)
    is HomeStateAction.ExternalBettingFailed -> state.copy(betEntryError = action.message)
    is HomeStateAction.BetEntryFailed -> reduceHomeEntryStateBetEntryFailed(state, action)
}

private fun reduceHomeContentState(state: HomeUiState, action: HomeContentAction): HomeUiState = when (action) {
    HomeStateAction.CartLoading -> state.copy(loadingCart = true)

    is HomeStateAction.CartReceived -> if (!action.requestCurrent) {
        state
    } else {
        state.copy(
            loadingCart = false,
            cartTotal = action.cart.total,
            cartItemCount = action.cart.betCount
        )
    }

    is HomeStateAction.CartFailed -> if (!action.requestCurrent) state else state.copy(loadingCart = false)

    HomeStateAction.SocialTransfersLoading -> state.copy(loadingSocialTransfers = true)

    is HomeStateAction.SocialTransfersReceived -> state.copy(loadingSocialTransfers = false, socialTransfers = action.transfers)

    HomeStateAction.SocialTransfersFailed -> state.copy(loadingSocialTransfers = false)
}

private fun reduceHomeEntryStateBetEntryFailed(state: HomeUiState, action: HomeStateAction.BetEntryFailed): HomeUiState = state.copy(
    preparingBet = false,
    betEntryError = action.message
)
