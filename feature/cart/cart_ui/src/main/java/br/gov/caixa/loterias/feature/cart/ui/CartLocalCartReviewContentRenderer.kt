package br.gov.caixa.loterias.feature.cart.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartLocalCartReviewContentEntry
import br.gov.caixa.loterias.feature.cart.presentation.state.LocalCartReviewState

internal class CartLocalCartReviewContentRenderer : CartLocalCartReviewContentEntry {
    @Composable
    override fun LocalCartReviewContent(
        state: br.gov.caixa.loterias.feature.cart.presentation.state.LocalCartReviewState,
        onBack: () -> Unit,
        onRemoteCart: () -> Unit,
        onLoad: () -> Unit,
        onConfirmDiscard: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.cart.presentation.screen.LocalCartReviewContentDelivered(
            state,
            onBack,
            onRemoteCart,
            onLoad,
            onConfirmDiscard
        )
    }
}
