package br.gov.caixa.loterias.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.ClearCartUseCase
import br.gov.caixa.loterias.core.domain.cart.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.cart.RemoveCartItemUseCase
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.domain.cart.SaveCurrentCartFavoriteUseCase
import br.gov.caixa.loterias.core.domain.cart.ValidateCartUseCase
import br.gov.caixa.loterias.core.domain.cart.VerifyCartPaymentAccountUseCase
import br.gov.caixa.loterias.core.domain.cart.CartPaymentAccountDecision
import br.gov.caixa.loterias.core.domain.cart.HasRecentPurchaseUseCase
import br.gov.caixa.loterias.core.domain.cart.CheckCheckoutLocationUseCase
import br.gov.caixa.loterias.core.domain.cart.CheckoutLocationStatus
import br.gov.caixa.loterias.core.domain.cart.RecordCheckoutLocationRequestUseCase
import br.gov.caixa.loterias.core.domain.cart.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import java.math.BigDecimal
import java.time.Clock
import java.time.LocalDateTime
import br.gov.caixa.loterias.core.common.NetworkAvailability
import br.gov.caixa.loterias.core.common.ConnectionAvailability
import br.gov.caixa.loterias.core.domain.cart.CartValidationResult
import br.gov.caixa.loterias.core.presentation.ServiceFailurePresentation
import br.gov.caixa.loterias.core.presentation.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.ServiceRedirectEvent
import br.gov.caixa.loterias.core.presentation.serviceFailurePresentation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

enum class CartLocationAction { REQUEST_PERMISSION, PERMISSION_SETTINGS, PROVIDER_SETTINGS }

data class CartUiState(
    val loading: Boolean = true,
    val cart: ShoppingCart? = null,
    val pendingRemoval: CartItem? = null,
    val clearConfirmation: Boolean = false,
    val minimumCartAmount: BigDecimal = BigDecimal.ZERO,
    val maximumCartBets: Int = 0,
    val authenticated: Boolean = false,
    val favoriteNameDialog: Boolean = false,
    val favoriteName: String = "",
    val favoriteOverwriteConfirmation: Boolean = false,
    val favoriteSaved: Boolean = false,
    val paymentReady: Boolean = false,
    val validationFailure: ServiceFailurePresentation = ServiceFailurePresentation(),
    val paymentAccountDecision: CartPaymentAccountDecision? = null,
    val recentPurchaseConfirmation: Boolean = false,
    val locationPrompt: CheckoutLocationStatus? = null,
    val locationAction: CartLocationAction? = null,
    val awaitingLocationAction: CartLocationAction? = null,
    val errorMessage: String? = null,
    val loadingParameters: Boolean = false,
    val parametersUnavailable: Boolean = false,
    val parameterRetryMessage: String? = null,
)

class CartViewModel(
    private val getCurrentCart: GetCurrentCartUseCase,
    private val removeCartItem: RemoveCartItemUseCase,
    private val clearCart: ClearCartUseCase,
    private val serverTime: br.gov.caixa.loterias.core.common.ServerTimeSource,
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
) : ViewModel() {
    private var cartRequestRunning = false
    private var recentPurchaseContinuation: Pair<ShoppingCart, CartPaymentAccountDecision>? = null
    private var locationAccount: CartPaymentAccountDecision? = null
    private val mutableState = MutableStateFlow(CartUiState(authenticated = authenticated))
    val state = mutableState.asStateFlow()

    init {
        load()
        loadParameters()
    }

    fun load() {
        if (cartRequestRunning) return
        cartRequestRunning = true
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            try { applyResult(getCurrentCart()) } finally {
                cartRequestRunning = false
                mutableState.update { it.copy(loading = false) }
            }
        }
    }

    fun reservationNow(): java.time.Instant = serverTime.now()

    fun refreshExpiredReservations() {
        if (state.value.loading || state.value.loadingParameters || state.value.parametersUnavailable) return
        if (hasExpiredPoolShares(state.value.cart, serverTime.now())) load()
    }

    fun requestRemoval(item: CartItem) = mutableState.update { it.copy(pendingRemoval = item) }
    fun requestClear() = mutableState.update { it.copy(clearConfirmation = true) }
    fun requestFavorite() = mutableState.update {
        if (canFavoriteCart(it.cart, it.authenticated)) it.copy(favoriteNameDialog = true, favoriteName = "", errorMessage = null) else it
    }
    fun updateFavoriteName(name: String) = mutableState.update { if (it.loading) it else it.copy(favoriteName = name.take(30), errorMessage = null) }
    fun dismissConfirmation() = mutableState.update {
        it.copy(
            pendingRemoval = null,
            clearConfirmation = false,
            favoriteNameDialog = false,
            favoriteOverwriteConfirmation = false,
            favoriteSaved = false,
            favoriteName = "",
            errorMessage = null,
        )
    }

    fun confirmRemoval() {
        val item = state.value.pendingRemoval ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loading = true, pendingRemoval = null, errorMessage = null) }
            applyResult(removeCartItem(item))
        }
    }

    fun confirmClear() = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, clearConfirmation = false, errorMessage = null) }
        applyResult(clearCart())
    }

    fun submitFavoriteName() {
        if (state.value.loading) return
        val name = state.value.favoriteName.trim()
        if (name.isBlank()) {
            mutableState.update { it.copy(errorMessage = "Informe o nome do carrinho favorito.") }
            return
        }
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            val validation = validateFavoriteCartName?.invoke(name) ?: AppResult.Success(Unit)
            when (validation) {
                is AppResult.Success -> saveFavorite(name)
                is AppResult.Failure -> when (validation.error) {
                    is AppError.Validation -> mutableState.update {
                        it.copy(loading = false, favoriteNameDialog = false, favoriteOverwriteConfirmation = true)
                    }
                    else -> mutableState.update { it.copy(loading = false, errorMessage = validation.error.userMessage()) }
                }
            }
        }
    }

    fun confirmFavoriteOverwrite() = viewModelScope.launch(dispatchers.io) {
        val name = state.value.favoriteName.trim()
        mutableState.update { it.copy(loading = true, favoriteOverwriteConfirmation = false, errorMessage = null) }
        saveFavorite(name)
    }

    fun requestPayment() {
        if (state.value.validationFailure.pending) return
        if (locationAccount != null || state.value.locationPrompt != null) return
        if (state.value.recentPurchaseConfirmation) return
        if (state.value.paymentAccountDecision != null) return
        if (state.value.loading || state.value.paymentReady) return
        if (state.value.parametersUnavailable || state.value.loadingParameters) return
        // An empty loaded cart follows the legacy gates and server validation too.
        if (state.value.cart == null) return
        mutableState.update { it.copy(loading = true, errorMessage = null, paymentReady = false) }
        viewModelScope.launch(dispatchers.io) {
            val account = when (val result = verifyPaymentAccount()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> {
                    mutableState.update { it.copy(loading = false, errorMessage = result.error.userMessage()) }
                    return@launch
                }
            }
            if (account != CartPaymentAccountDecision.Allowed && account != CartPaymentAccountDecision.LoginRequired) {
                mutableState.update { it.copy(loading = false, paymentAccountDecision = account) }
                return@launch
            }
            if (hasExpiredPoolShares(state.value.cart, serverTime.now())) {
                when (val refreshed = getCurrentCart()) {
                    is AppResult.Success -> mutableState.update { it.copy(cart = refreshed.value) }
                    // As in the legacy flow, the authoritative payment validation still runs on refresh failure.
                    is AppResult.Failure -> Unit
                }
            }
            locationAccount = account
            checkLocationAndContinue()
        }
    }

    private suspend fun checkLocationAndContinue() {
        val account = locationAccount ?: return
        try {
            val status = when (val result = checkCheckoutLocation()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> CheckoutLocationStatus.UNAVAILABLE
            }
            if (status == CheckoutLocationStatus.IN_BRAZIL) {
                locationAccount = null
                checkRecentPurchase(account)
            } else {
                mutableState.update { it.copy(locationPrompt = status) }
            }
        } catch (cancelled: CancellationException) {
            blockLocation()
            throw cancelled
        } finally {
            mutableState.update { it.copy(loading = false) }
        }
    }

    fun acceptLocationConsent() {
        val action = when (state.value.locationPrompt) {
            CheckoutLocationStatus.PERMISSION_REQUIRED -> CartLocationAction.REQUEST_PERMISSION
            CheckoutLocationStatus.PERMISSION_SETTINGS_REQUIRED -> CartLocationAction.PERMISSION_SETTINGS
            CheckoutLocationStatus.PROVIDER_DISABLED -> CartLocationAction.PROVIDER_SETTINGS
            else -> return
        }
        mutableState.update { it.copy(locationPrompt = null, loading = true) }
        viewModelScope.launch(dispatchers.io) {
            try {
                if (action == CartLocationAction.REQUEST_PERMISSION && recordLocationRequest() is AppResult.Failure) {
                    blockLocation()
                } else mutableState.update { it.copy(locationAction = action) }
            } catch (cancelled: CancellationException) {
                blockLocation()
                throw cancelled
            } finally {
                mutableState.update { it.copy(loading = false) }
            }
        }
    }

    /** Consume before launching Android UI so recomposition cannot launch it twice. */
    fun takeLocationAction(action: CartLocationAction): Boolean {
        if (state.value.locationAction != action || state.value.awaitingLocationAction != null) return false
        mutableState.update { it.copy(locationAction = null, awaitingLocationAction = action) }
        return true
    }

    fun locationPermissionResult(granted: Boolean, permanentlyDenied: Boolean) {
        if (state.value.awaitingLocationAction != CartLocationAction.REQUEST_PERMISSION) return
        mutableState.update { it.copy(awaitingLocationAction = null) }
        when {
            granted -> retryCheckoutLocation()
            permanentlyDenied -> mutableState.update { it.copy(locationAction = CartLocationAction.PERMISSION_SETTINGS) }
            else -> blockLocation()
        }
    }

    fun locationSettingsReturned() {
        if (state.value.awaitingLocationAction !in setOf(CartLocationAction.PERMISSION_SETTINGS, CartLocationAction.PROVIDER_SETTINGS)) return
        mutableState.update { it.copy(awaitingLocationAction = null) }
        retryCheckoutLocation()
    }

    private fun retryCheckoutLocation() {
        mutableState.update { it.copy(loading = true, locationPrompt = null) }
        viewModelScope.launch(dispatchers.io) { checkLocationAndContinue() }
    }

    fun declineLocationConsent() {
        if (state.value.locationPrompt != null) blockLocation()
    }

    fun locationLaunchFailed() {
        if (state.value.locationAction != null || state.value.awaitingLocationAction != null) blockLocation()
    }

    fun dismissLocationBlock(): Boolean {
        if (state.value.locationPrompt !in setOf(CheckoutLocationStatus.OUTSIDE_BRAZIL, CheckoutLocationStatus.UNAVAILABLE)) return false
        locationAccount = null
        mutableState.update { it.copy(locationPrompt = null) }
        return true
    }

    private fun blockLocation() {
        locationAccount = null
        mutableState.update { it.copy(
            loading = false,
            locationPrompt = CheckoutLocationStatus.UNAVAILABLE,
            locationAction = null,
            awaitingLocationAction = null,
        ) }
    }

    private suspend fun checkRecentPurchase(account: CartPaymentAccountDecision) {
        val cart = state.value.cart ?: return
        when (val result = hasRecentPurchase(cart.total)) {
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.userMessage())
            }
            is AppResult.Success -> if (result.value) {
                recentPurchaseContinuation = cart to account
                mutableState.update { it.copy(loading = false, recentPurchaseConfirmation = true) }
            } else continuePayment(account)
        }
    }

    /** Sim means review the cart, not authorize a duplicate purchase. */
    fun reviewRecentPurchase() {
        recentPurchaseContinuation = null
        mutableState.update { it.copy(recentPurchaseConfirmation = false) }
    }

    /** Não resumes this attempt only; a later attempt must consult the history again. */
    fun continueRecentPurchase() {
        if (state.value.loading) return
        val (cart, account) = recentPurchaseContinuation ?: return
        reviewRecentPurchase()
        if (state.value.cart != cart) {
            requestPayment()
            return
        }
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) { continuePayment(account) }
    }

    private suspend fun continuePayment(account: CartPaymentAccountDecision) {
        if (account == CartPaymentAccountDecision.LoginRequired) {
            mutableState.update { it.copy(loading = false, paymentAccountDecision = account) }
            return
        }
        when (val result = validateCart()) {
            is AppResult.Success -> when (val validation = result.value) {
                CartValidationResult.Accepted -> mutableState.update { it.copy(loading = false, paymentReady = true) }
                is CartValidationResult.Rejected -> mutableState.update { it.copy(loading = false, paymentReady = false,
                    validationFailure = serviceFailurePresentation(validation.failure, networkAvailability, LocalDateTime.now(localClock))) }
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.userMessage())
            }
        }
    }

    fun consumePaymentReady() = mutableState.update { it.copy(paymentReady = false) }
    fun dismissValidationNotice(expected: ServiceNoticeUiState) = mutableState.update {
        it.copy(validationFailure = it.validationFailure.dismiss(expected))
    }
    fun acceptValidationNotice(expected: ServiceNoticeUiState) = mutableState.update {
        it.copy(validationFailure = it.validationFailure.accept(expected, networkAvailability))
    }
    fun consumeValidationRedirect(expected: ServiceRedirectEvent): Boolean {
        val previous = mutableState.getAndUpdate {
            if (it.validationFailure.redirect !== expected) it else it.copy(validationFailure = it.validationFailure.copy(redirect = null))
        }
        return previous.validationFailure.redirect === expected
    }
    fun consumePaymentAccountDecision() = mutableState.update { it.copy(paymentAccountDecision = null) }

    fun retryParameters() = loadParameters()
    fun dismissParameterRetryMessage() = mutableState.update { it.copy(parameterRetryMessage = null) }
    fun dismissParametersUnavailable() {
        if (!state.value.loadingParameters) mutableState.update {
            it.copy(parametersUnavailable = false, parameterRetryMessage = null)
        }
    }

    private fun loadParameters() {
        val getParameters = getBettingParameters ?: return
        if (state.value.loadingParameters) return
        val recovering = state.value.parametersUnavailable
        mutableState.update { it.copy(loadingParameters = true, parameterRetryMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            try {
                when (val result = getParameters()) {
                    is AppResult.Success -> mutableState.update {
                        if (result.value.games.isEmpty()) {
                            it.copy(parametersUnavailable = true,
                                parameterRetryMessage = if (recovering) PARAMETER_RETRY_FAILURE else null)
                        } else it.copy(
                            minimumCartAmount = result.value.minimumCartAmount,
                            maximumCartBets = result.value.maximumCartBets,
                            parametersUnavailable = false,
                        )
                    }
                    // The legacy cart keeps its current content on the initial transport failure.
                    is AppResult.Failure -> if (recovering) mutableState.update {
                        it.copy(parameterRetryMessage = PARAMETER_RETRY_FAILURE)
                    }
                }
            } finally {
                mutableState.update { it.copy(loadingParameters = false) }
            }
        }
    }

    private suspend fun saveFavorite(name: String) {
        when (val result = saveCurrentCartFavorite?.invoke(name) ?: AppResult.Success(Unit)) {
            is AppResult.Success -> mutableState.update {
                it.copy(
                    loading = false,
                    favoriteNameDialog = false,
                    favoriteOverwriteConfirmation = false,
                    favoriteSaved = true,
                    favoriteName = "",
                )
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.userMessage())
            }
        }
    }

    private fun applyResult(result: AppResult<ShoppingCart>) {
        when (result) {
            is AppResult.Success -> mutableState.update {
                it.copy(loading = false, cart = result.value, errorMessage = null)
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(loading = false, errorMessage = result.error.userMessage())
            }
        }
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> "Não foi possível carregar o carrinho. Verifique sua internet."
        is AppError.Validation -> reason.ifBlank { "Não foi possível atualizar o carrinho." }
        AppError.Unexpected -> "Ocorreu um erro inesperado ao atualizar o carrinho."
    }
}

private const val PARAMETER_RETRY_FAILURE = "Não foi possível concluir esta operação. Por favor, tente novamente."
