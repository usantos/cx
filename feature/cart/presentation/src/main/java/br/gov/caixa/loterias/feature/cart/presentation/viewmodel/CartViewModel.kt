package br.gov.caixa.loterias.feature.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocationUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequestUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.SaveCurrentCartFavoriteUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.VerifyCartPaymentAccountUseCase
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.CartCheckoutCoordinator
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.CartCheckoutNotices
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.CartCheckoutValidation
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.CartContentsCoordinator
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.CartFavoritesCoordinator
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.CartParametersCoordinator
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.CartReservationCoordinator
import br.gov.caixa.loterias.feature.cart.presentation.coordinator.CartReservationSession
import br.gov.caixa.loterias.feature.cart.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartLocationAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import br.gov.caixa.loterias.feature.cart.presentation.state.reduceCartState
import java.time.Clock

class CartViewModel(
    private val getCurrentCart: GetCurrentCartUseCase,
    private val removeCartItem: RemoveCartItemUseCase,
    private val clearCart: ClearCartUseCase,
    private val serverTime: br.gov.caixa.loterias.core.common.time.ServerTimeSource,
    private val verifyPaymentAccount: VerifyCartPaymentAccountUseCase,
    private val validateCart: ValidateCartUseCase,
    private val hasRecentPurchase: HasRecentPurchaseUseCase,
    private val checkCheckoutLocation: CheckCheckoutLocationUseCase,
    private val recordLocationRequest: RecordCheckoutLocationRequestUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val getBettingParameters: GetBettingParametersUseCase? = null,
    private val validateFavoriteCartName: ValidateFavoriteCartNameUseCase? = null,
    private val saveCurrentCartFavorite: SaveCurrentCartFavoriteUseCase? = null,
    authenticated: Boolean = false,
    private val networkAvailability: NetworkAvailability = NetworkAvailability { ConnectionAvailability.UNKNOWN },
    private val localClock: Clock = Clock.systemDefaultZone(),
    private val reservationSessions: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider? = null,
    private val reservationSessionChanges:
    kotlinx.coroutines.flow.Flow<br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity?>? = null,
    private val removeExpiredPoolShare: br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShareUseCase? = null,
) : ViewModel() {
    private var cartRequestRunning = false
    private val mutableState = reduxStateStore<CartUiState, CartAction>(
        CartUiState(authenticated = authenticated),
        ::reduceCartState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state

    private val checkoutNotices = CartCheckoutNotices(mutableState, networkAvailability, localClock)
    private val checkoutValidation = CartCheckoutValidation(mutableState, validateCart, checkoutNotices) { it.userMessage() }
    private val checkout = CartCheckoutCoordinator(
        mutableState, viewModelScope, dispatchers, serverTime, verifyPaymentAccount,
        getCurrentCart, checkCheckoutLocation, recordLocationRequest, hasRecentPurchase,
        checkoutValidation, { it.userMessage() }
    )
    private val reservations: CartReservationCoordinator = CartReservationCoordinator(
        mutableState, viewModelScope, dispatchers, serverTime, getCurrentCart, removeCartItem,
        removeExpiredPoolShare, CartReservationSession(reservationSessions, reservationSessionChanges),
        ::applyResult, ::loadParameters, { cartRequestRunning = it }
    )

    private val contents = CartContentsCoordinator(
        mutableState, viewModelScope, dispatchers, getCurrentCart, removeCartItem, clearCart,
        ::reservationSessionCurrent, { cartRequestRunning }, { cartRequestRunning = it }, { it.userMessage() }
    )
    private val favorites = CartFavoritesCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        validateFavoriteCartName,
        saveCurrentCartFavorite,
        { it.userMessage() }
    )
    private val parameters = CartParametersCoordinator(
        mutableState,
        viewModelScope,
        dispatchers,
        getBettingParameters,
        ::reservationSessionCurrent,
        { reservations.invalidateReservationSession() }
    )

    private fun reservationSessionCurrent() = reservations.reservationSessionCurrent()

    init {
        load()
        loadParameters()
    }

    fun load() = contents.load()

    fun reservationNow(): java.time.Instant = reservations.reservationNow()

    fun refreshExpiredReservations() = reservations.refreshExpiredReservations()

    fun requestRemoval(item: CartItem) = contents.requestRemoval(item)
    fun requestClear() = contents.requestClear()
    fun requestFavorite() = favorites.requestFavorite()
    fun updateFavoriteName(name: String) = favorites.updateFavoriteName(name)
    fun dismissConfirmation() = contents.dismissConfirmation()

    fun confirmRemoval() = contents.confirmRemoval()

    fun confirmClear() = contents.confirmClear()

    fun submitFavoriteName() = favorites.submitFavoriteName()

    fun confirmFavoriteOverwrite() = favorites.confirmFavoriteOverwrite()

    fun requestPayment() = checkout.requestPayment()

    fun acceptLocationConsent() = checkout.acceptLocationConsent()

    fun takeLocationAction(action: CartLocationAction): Boolean = checkout.takeLocationAction(action)

    fun locationPermissionResult(granted: Boolean, permanentlyDenied: Boolean) =
        checkout.locationPermissionResult(granted, permanentlyDenied)

    fun locationSettingsReturned() = checkout.locationSettingsReturned()

    fun declineLocationConsent() = checkout.declineLocationConsent()

    fun locationLaunchFailed() = checkout.locationLaunchFailed()

    fun dismissLocationBlock(): Boolean = checkout.dismissLocationBlock()

    fun reviewRecentPurchase() = checkout.reviewRecentPurchase()

    fun continueRecentPurchase() = checkout.continueRecentPurchase()

    fun consumePaymentReady() = checkout.consumePaymentReady()

    fun dismissValidationNotice(expected: ServiceNoticeUiState) = checkout.dismissValidationNotice(expected)

    fun acceptValidationNotice(expected: ServiceNoticeUiState) = checkout.acceptValidationNotice(expected)

    fun consumeValidationRedirect(expected: ServiceRedirectEvent): Boolean = checkout.consumeValidationRedirect(expected)

    fun consumePaymentAccountDecision() = checkout.consumePaymentAccountDecision()

    fun retryParameters() = parameters.retryParameters()
    fun dismissParameterRetryMessage() = parameters.dismissParameterRetryMessage()
    fun dismissParametersUnavailable() = parameters.dismissParametersUnavailable()

    private fun loadParameters() = parameters.loadParameters()

    private fun applyResult(result: AppResult<ShoppingCart>) = contents.applyResult(result)

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> messageText("nao_foi_possivel_carregar_o_carr_0383b6")
        is AppError.Validation -> reason.ifBlank { messageText("nao_foi_possivel_atualizar_o_car_12bae5") }
        AppError.Unexpected -> messageText("ocorreu_um_erro_inesperado_ao_at_fbdd3e")
    }
}
