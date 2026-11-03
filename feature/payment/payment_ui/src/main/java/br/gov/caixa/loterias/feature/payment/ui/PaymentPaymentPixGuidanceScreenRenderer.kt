package br.gov.caixa.loterias.feature.payment.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentPaymentPixGuidanceScreenEntry
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentTerminalUiState

internal class PaymentPaymentPixGuidanceScreenRenderer : PaymentPaymentPixGuidanceScreenEntry {
    @Composable
    override fun PixGuidanceScreen(
        spec: WorkflowSpec,
        state: PaymentTerminalUiState,
        onBack: () -> Unit,
        onTogglePixTerms: () -> Unit,
        onSubmit: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.payment.presentation.screen.PixGuidanceScreenDelivered(
            spec,
            state,
            onBack,
            onTogglePixTerms,
            onSubmit
        )
    }
}
