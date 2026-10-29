package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.rule.CartReservationExpiry.hasExpiredPoolShares
import br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocationUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequestUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.VerifyCartPaymentAccountUseCase
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartCheckoutAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartLocationAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import java.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class CartCheckoutCoordinator(
    private val mutableState: ReduxStateStore<CartUiState, CartAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val serverTime: ServerTimeSource,
    private val verifyPaymentAccount: VerifyCartPaymentAccountUseCase,
    private val getCurrentCart: GetCurrentCartUseCase,
    checkCheckoutLocation: CheckCheckoutLocationUseCase,
    recordLocationRequest: RecordCheckoutLocationRequestUseCase,
    hasRecentPurchase: HasRecentPurchaseUseCase,
    private val validation: CartCheckoutValidation,
    private val messageFor: (AppError) -> String,
) {
    private val notices = validation.notices
    private val redirects = CartCheckoutRedirects(mutableState)
    private val recentPurchase = CartCheckoutRecentPurchase(
        mutableState,
        scope,
        dispatchers,
        hasRecentPurchase,
        validation::continuePayment,
        ::requestPayment,
        messageFor
    )
    private val location = CartCheckoutLocation(
        mutableState,
        scope,
        dispatchers,
        checkCheckoutLocation,
        recentPurchase::checkRecentPurchase
    )
    private val permissions = CartCheckoutPermissions(
        mutableState,
        scope,
        dispatchers,
        recordLocationRequest,
        location::blockLocation,
        location::retryCheckoutLocation
    )

    fun requestPayment() {
        if (mutableState.value.validationFailure.pending) return
        if (location.pending || mutableState.value.locationPrompt != null) return
        if (mutableState.value.recentPurchaseConfirmation) return
        if (mutableState.value.paymentAccountDecision != null) return
        if (mutableState.value.loading || mutableState.value.paymentReady) return
        if (mutableState.value.parametersUnavailable || mutableState.value.loadingParameters) return
        // Um carrinho carregado vazio também segue as verificações legadas e a validação do servidor.
        if (mutableState.value.cart == null) return
        mutableState.dispatch(CartCheckoutAction.Started)
        scope.launch(dispatchers.io) {
            val account = when (val result = verifyPaymentAccount()) {
                is AppResult.Success -> result.value

                is AppResult.Failure -> {
                    mutableState.dispatch(CartCheckoutAction.Failed(messageFor(result.error)))
                    return@launch
                }
            }
            if (account != CartPaymentAccountDecision.Allowed && account != CartPaymentAccountDecision.LoginRequired) {
                mutableState.dispatch(CartCheckoutAction.AccountDecisionReceived(account))
                return@launch
            }
            if (hasExpiredPoolShares(mutableState.value.cart, serverTime.now())) {
                when (val refreshed = getCurrentCart()) {
                    is AppResult.Success -> mutableState.dispatch(CartCheckoutAction.CartRefreshed(refreshed.value))

                    // Como no fluxo legado, a validação autoritativa do pagamento continua sendo executada quando a atualização falha.
                    is AppResult.Failure -> Unit
                }
            }
            location.check(account)
        }
    }

    fun acceptLocationConsent() = permissions.acceptLocationConsent()
    fun takeLocationAction(action: CartLocationAction): Boolean = permissions.takeLocationAction(action)
    fun locationPermissionResult(granted: Boolean, permanentlyDenied: Boolean) =
        permissions.locationPermissionResult(granted, permanentlyDenied)
    fun locationSettingsReturned() = permissions.locationSettingsReturned()
    fun declineLocationConsent() = location.declineLocationConsent()
    fun locationLaunchFailed() = permissions.locationLaunchFailed()
    fun dismissLocationBlock(): Boolean = location.dismissLocationBlock()
    fun reviewRecentPurchase() = recentPurchase.reviewRecentPurchase()
    fun continueRecentPurchase() = recentPurchase.continueRecentPurchase()
    fun consumePaymentReady() = redirects.consumePaymentReady()
    fun dismissValidationNotice(expected: ServiceNoticeUiState) = notices.dismissValidationNotice(expected)
    fun acceptValidationNotice(expected: ServiceNoticeUiState) = notices.acceptValidationNotice(expected)
    fun consumeValidationRedirect(expected: ServiceRedirectEvent): Boolean = redirects.consumeValidationRedirect(expected)
    fun consumePaymentAccountDecision() = redirects.consumePaymentAccountDecision()
}
