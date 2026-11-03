package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartUiEntry
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionUiState

@Composable
fun LegacyPoolAddition(
    state: CartAdditionUiState,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onOpenCart: () -> Unit,
    onHelp: ((Int, String) -> Unit)?,
) {
    FeatureModuleContent(
        module = AppModule.CART,
        entryType = CartUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.LegacyPoolAddition(state, onBack, onHome, onOpenCart, onHelp)
    }
}
