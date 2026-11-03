package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentUiEntry
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentTerminalUiState

@Composable
fun PixGuidanceScreen(
    spec: WorkflowSpec,
    state: PaymentTerminalUiState,
    onBack: () -> Unit,
    onTogglePixTerms: () -> Unit,
    onSubmit: () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.PAYMENT,
        entryType = PaymentUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.PixGuidanceScreen(spec, state, onBack, onTogglePixTerms, onSubmit)
    }
}
