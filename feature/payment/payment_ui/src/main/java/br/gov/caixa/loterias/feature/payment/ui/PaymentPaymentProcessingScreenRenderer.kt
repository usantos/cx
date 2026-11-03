package br.gov.caixa.loterias.feature.payment.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentPaymentProcessingScreenEntry

internal class PaymentPaymentProcessingScreenRenderer : PaymentPaymentProcessingScreenEntry {
    @Composable
    override fun ProcessingScreen(spec: WorkflowSpec, onNavigate: (String) -> Unit) {
        br.gov.caixa.loterias.feature.payment.presentation.screen.ProcessingScreenDelivered(spec, onNavigate)
    }
}
