package br.gov.caixa.loterias.feature.payment.presentation.state

internal sealed interface PaymentTerminalAction {
    data object PixTermsToggled : PaymentTerminalAction
}

internal fun reducePaymentTerminalState(state: PaymentTerminalUiState, action: PaymentTerminalAction): PaymentTerminalUiState =
    when (action) {
        PaymentTerminalAction.PixTermsToggled -> state.copy(pixTermsAccepted = !state.pixTermsAccepted)
    }
