package br.gov.caixa.loterias.feature.payment.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec

interface PaymentPaymentFailureScreenEntry {
    @Composable
    fun PaymentFailureScreen(spec: WorkflowSpec, onNavigate: (String) -> Unit)
}
