package br.gov.caixa.loterias.feature.cart.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState

interface CartFavoriteCartDetailContentEntry {
    @Composable
    fun FavoriteCartDetailContent(
        state: FavoriteCartsUiState,
        onBack: () -> Unit,
        onHelp: (() -> Unit)?,
        onReload: () -> Unit,
        onRequestBetRemoval: (Long) -> Unit,
    )
}
