package br.gov.caixa.loterias.feature.payment.presentation.state

import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec

data class PaymentTerminalUiState(val spec: WorkflowSpec, val pixTermsAccepted: Boolean = false)

sealed interface PaymentTerminalEffect {
    data object NavigateBack : PaymentTerminalEffect
    data object Completed : PaymentTerminalEffect
}
