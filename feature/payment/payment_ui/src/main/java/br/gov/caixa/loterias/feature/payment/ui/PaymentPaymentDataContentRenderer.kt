package br.gov.caixa.loterias.feature.payment.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentPaymentDataContentEntry
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentDataActions
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState

internal class PaymentPaymentDataContentRenderer : PaymentPaymentDataContentEntry {
    @Composable
    override fun PaymentDataContent(
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
        br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentDataContentDelivered(
            mode,
            state,
            visualAssets,
            handleBack,
            onHelp,
            onPix,
            onOpenCard,
            onPurchases,
            privateDataEnabled,
            actions
        )
    }
}
