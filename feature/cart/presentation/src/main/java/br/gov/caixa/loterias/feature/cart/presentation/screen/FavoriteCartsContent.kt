package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartUiEntry
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState

@Composable
fun FavoriteCartsContent(
    state: FavoriteCartsUiState,
    onBack: () -> Unit,
    onOpen: (Long) -> Unit,
    privateDataEnabled: Boolean,
    actions: FavoriteCartsActions,
) {
    FeatureModuleContent(
        module = AppModule.CART,
        entryType = CartUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.FavoriteCartsContent(state, onBack, onOpen, privateDataEnabled, actions)
    }
}
