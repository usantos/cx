package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentUiEntry

@Composable
fun PaymentFailureScreen(spec: WorkflowSpec, onNavigate: (String) -> Unit) {
    FeatureModuleContent(
        module = AppModule.PAYMENT,
        entryType = PaymentUiEntry::class.java,
        onBack = { onNavigate("home") },
        preserveActive = false
    ) { entry ->
        entry.PaymentFailureScreen(spec, onNavigate)
    }
}
