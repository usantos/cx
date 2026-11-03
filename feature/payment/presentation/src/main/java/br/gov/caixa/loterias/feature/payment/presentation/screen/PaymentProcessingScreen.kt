package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.navigation.state.PurchaseRecoverySession
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentUiEntry

@Composable
fun ProcessingScreen(spec: WorkflowSpec, onNavigate: (String) -> Unit, purchaseRecovery: PurchaseRecoverySession? = null) {
    val recovering = purchaseRecovery?.current() != null
    FeatureModuleContent(
        module = AppModule.PAYMENT,
        entryType = PaymentUiEntry::class.java,
        onBack = { onNavigate("home") },
        access = if (recovering) FeatureEntryAccess.CONTINUATION else FeatureEntryAccess.OPTIONAL,
        preserveActive = recovering
    ) { entry ->
        entry.ProcessingScreen(spec, onNavigate)
    }
}
