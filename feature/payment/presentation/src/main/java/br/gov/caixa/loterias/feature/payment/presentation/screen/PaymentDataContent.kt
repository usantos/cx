package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentUiEntry
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState

@Composable
fun PaymentDataContent(
    mode: PaymentMode,
    state: PaymentUiState,
    visualAssets: PaymentVisualAssets,
    handleBack: () -> Unit,
    onHelp: () -> Unit,
    onPix: () -> Unit,
    onOpenCard: (SavedPaymentCard) -> Unit,
    onPurchases: () -> Unit,
    privateDataEnabled: Boolean,
    actions: PaymentDataActions,
) {
    val continuation = mode == PaymentMode.PIX &&
        (state.pix != null || state.pixRecoveryRequired || state.redirectPaused || state.registrationPending || state.notice != null)
    FeatureModuleContent(
        module = AppModule.PAYMENT,
        entryType = PaymentUiEntry::class.java,
        onBack = handleBack,
        access = if (continuation) FeatureEntryAccess.CONTINUATION else FeatureEntryAccess.OPTIONAL,
        preserveActive = mode == PaymentMode.PIX && (state.loading || continuation)
    ) { entry ->
        entry.PaymentDataContent(mode, state, visualAssets, handleBack, onHelp, onPix, onOpenCard, onPurchases, privateDataEnabled, actions)
    }
}
