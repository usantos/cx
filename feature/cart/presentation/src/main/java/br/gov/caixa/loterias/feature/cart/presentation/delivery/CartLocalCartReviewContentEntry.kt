package br.gov.caixa.loterias.feature.cart.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.presentation.state.LocalCartReviewState

interface CartLocalCartReviewContentEntry {
    @Composable
    fun LocalCartReviewContent(
        state: br.gov.caixa.loterias.feature.cart.presentation.state.LocalCartReviewState,
        onBack: () -> Unit,
        onRemoteCart: () -> Unit,
        onLoad: () -> Unit,
        onConfirmDiscard: () -> Unit,
    )
}
