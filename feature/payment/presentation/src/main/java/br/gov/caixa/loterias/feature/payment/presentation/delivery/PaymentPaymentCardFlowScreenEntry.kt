package br.gov.caixa.loterias.feature.payment.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentCardActions
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState

interface PaymentPaymentCardFlowScreenEntry {
    @Composable
    fun PaymentCardFlowScreen(state: PaymentCardUiState, actions: PaymentCardActions, onBack: () -> Unit)
}
