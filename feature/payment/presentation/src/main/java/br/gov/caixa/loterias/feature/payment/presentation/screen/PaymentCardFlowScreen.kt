package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentUiEntry
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState

data class PaymentCardActions(
    val changeNumber: (String) -> Unit,
    val changeHolderName: (String) -> Unit,
    val changeMonth: (String) -> Unit,
    val changeYear: (String) -> Unit,
    val toggleSave: () -> Unit,
    val continueFromCard: () -> Unit,
    val changeCvv: (String) -> Unit,
    val continueFromCvv: () -> Unit,
    val submit: () -> Unit,
    val retry: () -> Unit,
)

@Composable
fun PaymentCardFlowScreen(state: PaymentCardUiState, actions: PaymentCardActions, onBack: () -> Unit) {
    val continuation = state.stage == br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage.PROCESSING ||
        state.trackingPurchase || state.purchaseReview != null || state.redirect != null ||
        state.redirectPurchaseId != null || state.notice != null || state.registrationPending
    FeatureModuleContent(
        module = AppModule.PAYMENT,
        entryType = PaymentUiEntry::class.java,
        onBack = onBack,
        access = if (continuation) FeatureEntryAccess.CONTINUATION else FeatureEntryAccess.OPTIONAL,
        preserveActive = continuation
    ) { entry ->
        entry.PaymentCardFlowScreen(state, actions, onBack)
    }
}
