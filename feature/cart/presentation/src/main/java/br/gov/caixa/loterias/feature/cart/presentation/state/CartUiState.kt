package br.gov.caixa.loterias.feature.cart.presentation.state

import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import java.math.BigDecimal
import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation

enum class CartLocationAction { REQUEST_PERMISSION, PERMISSION_SETTINGS, PROVIDER_SETTINGS }

data class CartUiState(
    val loading: Boolean = true,
    val expirationRemovalInFlight: Boolean = false,
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

