package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.serviceFailurePresentation
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartCheckoutAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import java.time.Clock
import java.time.LocalDateTime

internal class CartCheckoutNotices(
    private val mutableState: ReduxStateStore<CartUiState, CartAction>,
    private val networkAvailability: NetworkAvailability,
    private val localClock: Clock,
) {
    fun show(failure: ServiceFailure) {
        val presentation = serviceFailurePresentation(failure, networkAvailability, LocalDateTime.now(localClock))
        mutableState.dispatch(CartCheckoutAction.ValidationRejected(presentation))
    }

    fun dismissValidationNotice(expected: ServiceNoticeUiState) =
        mutableState.dispatch(CartCheckoutAction.ValidationNoticeDismissed(expected))

    fun acceptValidationNotice(expected: ServiceNoticeUiState) = synchronized(mutableState) {
        val current = mutableState.value.validationFailure
        if (current.notice !== expected) return@synchronized
        val accepted = current.accept(expected, networkAvailability)
        mutableState.dispatch(CartCheckoutAction.ValidationNoticeAccepted(expected, accepted))
    }
}
