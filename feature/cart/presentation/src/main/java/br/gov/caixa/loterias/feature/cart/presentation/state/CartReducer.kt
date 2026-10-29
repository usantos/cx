package br.gov.caixa.loterias.feature.cart.presentation.state

import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation
import br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.mapper.canFavoriteCart
import java.math.BigDecimal

sealed interface CartAction

sealed interface CartContentsAction : CartAction {
    data object LoadStarted : CartContentsAction
    data object LoadFinished : CartContentsAction
    data class RemovalRequested(val item: CartItem) : CartContentsAction
    data object ClearRequested : CartContentsAction
    data object ConfirmationDismissed : CartContentsAction
    data object RemovalStarted : CartContentsAction
    data object ClearStarted : CartContentsAction
    data class Cleared(val cart: ShoppingCart) : CartContentsAction
    data class ClearFailed(val message: String) : CartContentsAction
    data class Loaded(val cart: ShoppingCart) : CartContentsAction
    data class LoadFailed(val message: String) : CartContentsAction
}

sealed interface CartFavoriteAction : CartAction {
    data object Requested : CartFavoriteAction
    data class NameChanged(val name: String) : CartFavoriteAction
    data class NameRejected(val message: String) : CartFavoriteAction
    data object ValidationStarted : CartFavoriteAction
    data object OverwriteRequested : CartFavoriteAction
    data class ValidationFailed(val message: String) : CartFavoriteAction
    data object OverwriteStarted : CartFavoriteAction
    data object Saved : CartFavoriteAction
    data class SaveFailed(val message: String) : CartFavoriteAction
}

sealed interface CartParametersAction : CartAction {
    data object RetryMessageDismissed : CartParametersAction
    data object UnavailableDismissed : CartParametersAction
    data object LoadStarted : CartParametersAction
    data class Unavailable(val retryMessage: String?) : CartParametersAction
    data class Loaded(val minimumAmount: BigDecimal, val maximumBets: Int) : CartParametersAction
    data class RetryFailed(val message: String) : CartParametersAction
    data object LoadFinished : CartParametersAction
}

sealed interface CartReservationAction : CartAction {
    data object SessionInvalidated : CartReservationAction
    data object ExpirationRemovalStarted : CartReservationAction
    data object ExpirationRemovalFinished : CartReservationAction
}

sealed interface CartCheckoutAction : CartAction {
    data object Started : CartCheckoutAction
    data class Failed(val message: String) : CartCheckoutAction
    data class AccountDecisionReceived(val decision: CartPaymentAccountDecision) : CartCheckoutAction
    data class CartRefreshed(val cart: ShoppingCart) : CartCheckoutAction
    data class LocationPrompted(val status: CheckoutLocationStatus) : CartCheckoutAction
    data object LocationCheckFinished : CartCheckoutAction
    data object LocationRetryStarted : CartCheckoutAction
    data object LocationBlockDismissed : CartCheckoutAction
    data object LocationBlocked : CartCheckoutAction
    data object LocationConsentAccepted : CartCheckoutAction
    data class LocationActionPrepared(val action: CartLocationAction) : CartCheckoutAction
    data class LocationActionTaken(val action: CartLocationAction) : CartCheckoutAction
    data object LocationActionReturned : CartCheckoutAction
    data class ValidationRejected(val presentation: ServiceFailurePresentation) : CartCheckoutAction
    data class ValidationNoticeDismissed(val expected: ServiceNoticeUiState) : CartCheckoutAction
    data class ValidationNoticeAccepted(val expected: ServiceNoticeUiState, val presentation: ServiceFailurePresentation) :
        CartCheckoutAction
    data object RecentPurchaseDetected : CartCheckoutAction
    data object RecentPurchaseReviewed : CartCheckoutAction
    data object RecentPurchaseContinued : CartCheckoutAction
    data object PaymentValidated : CartCheckoutAction
    data class ValidationFailed(val message: String) : CartCheckoutAction
    data object PaymentReadyConsumed : CartCheckoutAction
    data class ValidationRedirectConsumed(val expected: ServiceRedirectEvent) : CartCheckoutAction
    data object AccountDecisionConsumed : CartCheckoutAction
}

fun reduceCartState(state: CartUiState, action: CartAction): CartUiState = when (action) {
    is CartContentsAction -> reduceCartContentsState(state, action)
    is CartFavoriteAction -> reduceCartFavoriteState(state, action)
    is CartParametersAction -> reduceCartParametersState(state, action)
    is CartReservationAction -> reduceCartReservationState(state, action)
    is CartCheckoutAction -> reduceCartCheckoutState(state, action)
}

private fun reduceCartContentsState(state: CartUiState, action: CartContentsAction): CartUiState = when (action) {
    CartContentsAction.LoadStarted -> state.copy(loading = true, errorMessage = null)

    CartContentsAction.LoadFinished -> state.copy(loading = false)

    is CartContentsAction.RemovalRequested -> if (state.loading) state else state.copy(pendingRemoval = action.item)

    CartContentsAction.ClearRequested -> if (state.loading) state else state.copy(clearConfirmation = true)

    CartContentsAction.ConfirmationDismissed -> state.copy(
        pendingRemoval = null,
        clearConfirmation = false,
        favoriteNameDialog = false,
        favoriteOverwriteConfirmation = false,
        favoriteSaved = false,
        favoriteName = "",
        errorMessage = null
    )

    CartContentsAction.RemovalStarted -> state.copy(loading = true, pendingRemoval = null, errorMessage = null)

    CartContentsAction.ClearStarted -> state.copy(loading = true, clearConfirmation = false, errorMessage = null)

    is CartContentsAction.Cleared -> state.copy(
        loading = state.expirationRemovalInFlight,
        cart = action.cart.copy(total = BigDecimal.ZERO, items = emptyList(), totalBetCount = 0),
        errorMessage = null
    )

    is CartContentsAction.ClearFailed -> state.copy(
        loading = state.expirationRemovalInFlight,
        cart = state.cart?.copy(total = BigDecimal.ZERO, items = emptyList(), totalBetCount = 0),
        errorMessage = action.message
    )

    is CartContentsAction.Loaded -> state.copy(loading = state.expirationRemovalInFlight, cart = action.cart, errorMessage = null)

    is CartContentsAction.LoadFailed -> state.copy(loading = state.expirationRemovalInFlight, errorMessage = action.message)
}

private const val FAVORITE_NAME_LENGTH = 30

private fun reduceCartFavoriteState(state: CartUiState, action: CartFavoriteAction): CartUiState = when (action) {
    CartFavoriteAction.Requested -> if (!state.loading && canFavoriteCart(state.cart, state.authenticated)) {
        state.copy(favoriteNameDialog = true, favoriteName = "", errorMessage = null)
    } else {
        state
    }

    is CartFavoriteAction.NameChanged -> if (state.loading) {
        state
    } else {
        state.copy(
            favoriteName = action.name.take(FAVORITE_NAME_LENGTH),
            errorMessage = null
        )
    }

    is CartFavoriteAction.NameRejected -> state.copy(errorMessage = action.message)

    CartFavoriteAction.ValidationStarted -> state.copy(loading = true, errorMessage = null)

    CartFavoriteAction.OverwriteRequested -> state.copy(loading = false, favoriteNameDialog = false, favoriteOverwriteConfirmation = true)

    is CartFavoriteAction.ValidationFailed -> state.copy(loading = false, errorMessage = action.message)

    CartFavoriteAction.OverwriteStarted -> state.copy(loading = true, favoriteOverwriteConfirmation = false, errorMessage = null)

    CartFavoriteAction.Saved -> state.copy(
        loading = false,
        favoriteNameDialog = false,
        favoriteOverwriteConfirmation = false,
        favoriteSaved = true,
        favoriteName = ""
    )

    is CartFavoriteAction.SaveFailed -> state.copy(loading = state.expirationRemovalInFlight, errorMessage = action.message)
}

private fun reduceCartParametersState(state: CartUiState, action: CartParametersAction): CartUiState = when (action) {
    CartParametersAction.RetryMessageDismissed -> state.copy(parameterRetryMessage = null)

    CartParametersAction.UnavailableDismissed -> if (state.loadingParameters) {
        state
    } else {
        state.copy(
            parametersUnavailable = false,
            parameterRetryMessage = null
        )
    }

    CartParametersAction.LoadStarted -> state.copy(loadingParameters = true, parameterRetryMessage = null)

    is CartParametersAction.Unavailable -> state.copy(parametersUnavailable = true, parameterRetryMessage = action.retryMessage)

    is CartParametersAction.Loaded -> state.copy(
        minimumCartAmount = action.minimumAmount,
        maximumCartBets = action.maximumBets,
        parametersUnavailable = false
    )

    is CartParametersAction.RetryFailed -> state.copy(parameterRetryMessage = action.message)

    CartParametersAction.LoadFinished -> state.copy(loadingParameters = false)
}

private fun reduceCartReservationState(state: CartUiState, action: CartReservationAction): CartUiState = when (action) {
    CartReservationAction.SessionInvalidated -> CartUiState(loading = false)

    CartReservationAction.ExpirationRemovalStarted -> state.copy(
        loading = true,
        expirationRemovalInFlight = true,
        pendingRemoval = null,
        clearConfirmation = false
    )

    CartReservationAction.ExpirationRemovalFinished -> state.copy(loading = false, expirationRemovalInFlight = false)
}

private fun reduceCartCheckoutState(state: CartUiState, action: CartCheckoutAction): CartUiState = when (action) {
    CartCheckoutAction.Started -> state.copy(loading = true, errorMessage = null, paymentReady = false)
    is CartCheckoutAction.Failed -> state.copy(loading = false, errorMessage = action.message)
    is CartCheckoutAction.AccountDecisionReceived -> state.copy(loading = false, paymentAccountDecision = action.decision)
    is CartCheckoutAction.CartRefreshed -> state.copy(cart = action.cart)
    CartCheckoutAction.RecentPurchaseDetected -> state.copy(loading = false, recentPurchaseConfirmation = true)
    CartCheckoutAction.RecentPurchaseReviewed -> state.copy(recentPurchaseConfirmation = false)
    CartCheckoutAction.RecentPurchaseContinued -> state.copy(loading = true, errorMessage = null)
    CartCheckoutAction.PaymentValidated -> state.copy(loading = false, paymentReady = true)
    is CartCheckoutAction.ValidationFailed -> state.copy(loading = state.expirationRemovalInFlight, errorMessage = action.message)
    else -> reduceCartCheckoutLocationState(state, action)
}

private fun reduceCartCheckoutLocationState(state: CartUiState, action: CartCheckoutAction): CartUiState = when (action) {
    is CartCheckoutAction.LocationPrompted -> state.copy(locationPrompt = action.status)

    CartCheckoutAction.LocationCheckFinished -> state.copy(loading = false)

    CartCheckoutAction.LocationRetryStarted -> state.copy(loading = true, locationPrompt = null)

    CartCheckoutAction.LocationBlockDismissed -> state.copy(locationPrompt = null)

    CartCheckoutAction.LocationBlocked -> state.copy(
        loading = false,
        locationPrompt = CheckoutLocationStatus.UNAVAILABLE,
        locationAction = null,
        awaitingLocationAction = null
    )

    CartCheckoutAction.LocationConsentAccepted -> state.copy(locationPrompt = null, loading = true)

    is CartCheckoutAction.LocationActionPrepared -> state.copy(locationAction = action.action)

    is CartCheckoutAction.LocationActionTaken -> if (state.locationAction == action.action && state.awaitingLocationAction == null) {
        state.copy(locationAction = null, awaitingLocationAction = action.action)
    } else {
        state
    }

    CartCheckoutAction.LocationActionReturned -> state.copy(awaitingLocationAction = null)

    else -> reduceCartCheckoutNoticeState(state, action)
}

private fun reduceCartCheckoutNoticeState(state: CartUiState, action: CartCheckoutAction): CartUiState = when (action) {
    is CartCheckoutAction.ValidationRejected -> state.copy(loading = false, paymentReady = false, validationFailure = action.presentation)

    is CartCheckoutAction.ValidationNoticeDismissed -> state.copy(validationFailure = state.validationFailure.dismiss(action.expected))

    is CartCheckoutAction.ValidationNoticeAccepted -> if (state.validationFailure.notice === action.expected) {
        state.copy(validationFailure = action.presentation)
    } else {
        state
    }

    CartCheckoutAction.PaymentReadyConsumed -> state.copy(paymentReady = false)

    is CartCheckoutAction.ValidationRedirectConsumed -> if (state.validationFailure.redirect === action.expected) {
        state.copy(validationFailure = state.validationFailure.copy(redirect = null))
    } else {
        state
    }

    CartCheckoutAction.AccountDecisionConsumed -> state.copy(paymentAccountDecision = null)

    else -> state
}
