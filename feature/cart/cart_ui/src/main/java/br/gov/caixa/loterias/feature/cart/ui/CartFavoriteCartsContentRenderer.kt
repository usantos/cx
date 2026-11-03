package br.gov.caixa.loterias.feature.cart.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartFavoriteCartsContentEntry
import br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartsActions
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState

internal class CartFavoriteCartsContentRenderer : CartFavoriteCartsContentEntry {
    @Composable
    override fun FavoriteCartsContent(
        state: FavoriteCartsUiState,
        onBack: () -> Unit,
        onOpen: (Long) -> Unit,
        privateDataEnabled: Boolean,
        actions: FavoriteCartsActions,
    ) {
        br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartsContentDelivered(
            state,
            onBack,
            onOpen,
            privateDataEnabled,
            actions
        )
    }
}
