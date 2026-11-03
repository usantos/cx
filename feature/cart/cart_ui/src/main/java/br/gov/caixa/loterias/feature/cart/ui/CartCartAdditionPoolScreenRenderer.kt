package br.gov.caixa.loterias.feature.cart.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartCartAdditionPoolScreenEntry
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionUiState

internal class CartCartAdditionPoolScreenRenderer : CartCartAdditionPoolScreenEntry {
    @Composable
    override fun LegacyPoolAddition(
        state: CartAdditionUiState,
        onBack: () -> Unit,
        onHome: () -> Unit,
        onOpenCart: () -> Unit,
        onHelp: ((Int, String) -> Unit)?,
    ) {
        br.gov.caixa.loterias.feature.cart.presentation.screen.LegacyPoolAdditionDelivered(state, onBack, onHome, onOpenCart, onHelp)
    }
}
