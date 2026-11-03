package br.gov.caixa.loterias.feature.cart.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.presentation.screen.FavoriteCartsActions
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState

interface CartFavoriteCartsContentEntry {
    @Composable
    fun FavoriteCartsContent(
        state: FavoriteCartsUiState,
        onBack: () -> Unit,
        onOpen: (Long) -> Unit,
        privateDataEnabled: Boolean,
        actions: FavoriteCartsActions,
    )
}
