package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequestUseCase
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartCheckoutAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartLocationAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Consentimento e encaminhamento às permissões e configurações Android; decisões de país pertencem à localização. */
internal class CartCheckoutPermissions(
    private val mutableState: ReduxStateStore<CartUiState, CartAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val recordLocationRequest: RecordCheckoutLocationRequestUseCase,
    private val blockLocation: () -> Unit,
    private val retryCheckoutLocation: () -> Unit,
) {

    fun acceptLocationConsent() {
        val action = when (mutableState.value.locationPrompt) {
            CheckoutLocationStatus.PERMISSION_REQUIRED -> CartLocationAction.REQUEST_PERMISSION
            CheckoutLocationStatus.PERMISSION_SETTINGS_REQUIRED -> CartLocationAction.PERMISSION_SETTINGS
            CheckoutLocationStatus.PROVIDER_DISABLED -> CartLocationAction.PROVIDER_SETTINGS
            else -> return
        }
        mutableState.dispatch(CartCheckoutAction.LocationConsentAccepted)
        scope.launch(dispatchers.io) {
            try {
                if (action == CartLocationAction.REQUEST_PERMISSION && recordLocationRequest() is AppResult.Failure) {
                    blockLocation()
                } else {
                    mutableState.dispatch(CartCheckoutAction.LocationActionPrepared(action))
                }
            } catch (cancelled: CancellationException) {
                blockLocation()
                throw cancelled
            } finally {
                mutableState.dispatch(CartCheckoutAction.LocationCheckFinished)
            }
        }
    }

    /** Consome antes de abrir a interface Android para que a recomposição não a abra duas vezes. */
    fun takeLocationAction(action: CartLocationAction): Boolean = synchronized(mutableState) {
        val current = mutableState.value
        if (current.locationAction != action || current.awaitingLocationAction != null) return@synchronized false
        mutableState.dispatch(CartCheckoutAction.LocationActionTaken(action))
        true
    }

    fun locationPermissionResult(granted: Boolean, permanentlyDenied: Boolean) {
        if (mutableState.value.awaitingLocationAction != CartLocationAction.REQUEST_PERMISSION) return
        mutableState.dispatch(CartCheckoutAction.LocationActionReturned)
        when {
            granted -> retryCheckoutLocation()
            permanentlyDenied -> mutableState.dispatch(CartCheckoutAction.LocationActionPrepared(CartLocationAction.PERMISSION_SETTINGS))
            else -> blockLocation()
        }
    }

    fun locationSettingsReturned() {
        if (mutableState.value.awaitingLocationAction !in
            setOf(CartLocationAction.PERMISSION_SETTINGS, CartLocationAction.PROVIDER_SETTINGS)
        ) {
            return
        }
        mutableState.dispatch(CartCheckoutAction.LocationActionReturned)
        retryCheckoutLocation()
    }

    fun locationLaunchFailed() {
        if (mutableState.value.locationAction != null || mutableState.value.awaitingLocationAction != null) blockLocation()
    }
}
