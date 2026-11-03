package br.gov.caixa.loterias.feature.payment.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentPaymentFailureScreenEntry

internal class PaymentPaymentFailureScreenRenderer : PaymentPaymentFailureScreenEntry {
    @Composable
    override fun PaymentFailureScreen(spec: WorkflowSpec, onNavigate: (String) -> Unit) {
        br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentFailureScreenDelivered(spec, onNavigate)
    }
}
