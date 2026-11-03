package br.gov.caixa.loterias.feature.payment.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentTerminalUiState

interface PaymentPaymentPixGuidanceScreenEntry {
    @Composable
    fun PixGuidanceScreen(
        spec: WorkflowSpec,
        state: PaymentTerminalUiState,
        onBack: () -> Unit,
        onTogglePixTerms: () -> Unit,
        onSubmit: () -> Unit,
    )
}
