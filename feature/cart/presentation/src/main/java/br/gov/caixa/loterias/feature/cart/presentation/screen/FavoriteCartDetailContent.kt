package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartUiEntry
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState

@Composable
fun FavoriteCartDetailContent(
    state: FavoriteCartsUiState,
    onBack: () -> Unit,
    onHelp: (() -> Unit)?,
    onReload: () -> Unit,
    onRequestBetRemoval: (Long) -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.CART,
        entryType = CartUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.FavoriteCartDetailContent(state, onBack, onHelp, onReload, onRequestBetRemoval)
    }
}
