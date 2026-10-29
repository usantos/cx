package br.gov.caixa.loterias.feature.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentTerminalAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentTerminalEffect
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentTerminalUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentTerminalState
import kotlinx.coroutines.launch

/** Controla apenas o estado local da interface para confirmação Pix e telas terminais de pagamento. */
class PaymentTerminalViewModel(spec: WorkflowSpec) : ViewModel() {
    private val mutableState = reduxStateStore<PaymentTerminalUiState, PaymentTerminalAction>(
        PaymentTerminalUiState(spec),
        ::reducePaymentTerminalState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state

    private val mutableEffects = SingleConsumerEventQueue<PaymentTerminalEffect>()
    val effects = mutableEffects.events

    fun togglePixTerms() {
        mutableState.dispatch(PaymentTerminalAction.PixTermsToggled)
    }

    fun back() = emit(PaymentTerminalEffect.NavigateBack)

    fun submit() {
        val current = mutableState.value
        if (current.spec.route != "payment/pix-guidance" || current.pixTermsAccepted) {
            emit(PaymentTerminalEffect.Completed)
        }
    }

    private fun emit(effect: PaymentTerminalEffect) {
        viewModelScope.launch { mutableEffects.emit(effect) }
    }
}
