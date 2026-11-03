package br.gov.caixa.loterias.feature.cart.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartFavoriteCartDetailContentEntry
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState

internal class CartFavoriteCartDetailContentRenderer : CartFavoriteCartDetailContentEntry {
    @Composable
    override fun FavoriteCartDetailContent(
        state: FavoriteCartsUiState,
        onBack: () -> Unit,
        onHelp: (() -> Unit)?,
        onReload: () -> Unit,
        onRequestBetRemoval: (Long) -> Unit,
    ) {
        br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartDetailContentDelivered(
            state,
            onBack,
            onHelp,
            onReload,
            onRequestBetRemoval
        )
    }
}
