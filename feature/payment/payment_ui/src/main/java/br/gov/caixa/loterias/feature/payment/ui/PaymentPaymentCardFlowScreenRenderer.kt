package br.gov.caixa.loterias.feature.payment.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentPaymentCardFlowScreenEntry
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentCardActions
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState

internal class PaymentPaymentCardFlowScreenRenderer : PaymentPaymentCardFlowScreenEntry {
    @Composable
    override fun PaymentCardFlowScreen(state: PaymentCardUiState, actions: PaymentCardActions, onBack: () -> Unit) {
        br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentCardFlowScreenDelivered(state, actions, onBack)
    }
}
