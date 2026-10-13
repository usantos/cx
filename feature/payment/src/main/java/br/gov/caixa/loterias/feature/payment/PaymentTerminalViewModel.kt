package br.gov.caixa.loterias.feature.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.presentation.WorkflowSpec
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PaymentTerminalUiState(
    val spec: WorkflowSpec,
    val pixTermsAccepted: Boolean = false,
)

sealed interface PaymentTerminalEffect {
    data object NavigateBack : PaymentTerminalEffect
    data object Completed : PaymentTerminalEffect
}

/** Owns only local UI state for Pix acknowledgement and terminal payment screens. */
class PaymentTerminalViewModel(spec: WorkflowSpec) : ViewModel() {
    private val mutableState = MutableStateFlow(PaymentTerminalUiState(spec))
    val state = mutableState.asStateFlow()

    private val mutableEffects = MutableSharedFlow<PaymentTerminalEffect>()
    val effects = mutableEffects.asSharedFlow()

    fun togglePixTerms() {
        mutableState.value = mutableState.value.copy(
            pixTermsAccepted = !mutableState.value.pixTermsAccepted,
        )
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
