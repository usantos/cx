package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartUiEntry

@Composable
fun LocalCartReviewContent(
    state: br.gov.caixa.loterias.feature.cart.presentation.state.LocalCartReviewState,
    onBack: () -> Unit,
    onRemoteCart: () -> Unit,
    onLoad: () -> Unit,
    onConfirmDiscard: () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.CART,
        entryType = CartUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.LocalCartReviewContent(state, onBack, onRemoteCart, onLoad, onConfirmDiscard)
    }
}
