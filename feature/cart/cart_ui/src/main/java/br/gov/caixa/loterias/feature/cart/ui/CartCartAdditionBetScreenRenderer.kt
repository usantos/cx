package br.gov.caixa.loterias.feature.cart.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartCartAdditionBetScreenEntry
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionUiState

internal class CartCartAdditionBetScreenRenderer : CartCartAdditionBetScreenEntry {
    @Composable
    override fun LegacyBetAddition(
        state: CartAdditionUiState,
        onBack: () -> Unit,
        onHome: () -> Unit,
        onOpenCart: () -> Unit,
        onHelp: ((Int, String) -> Unit)?,
    ) {
        br.gov.caixa.loterias.feature.cart.presentation.screen.LegacyBetAdditionDelivered(state, onBack, onHome, onOpenCart, onHelp)
    }
}
