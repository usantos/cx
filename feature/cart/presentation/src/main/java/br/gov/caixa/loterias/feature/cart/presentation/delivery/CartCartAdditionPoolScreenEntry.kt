package br.gov.caixa.loterias.feature.cart.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionUiState

interface CartCartAdditionPoolScreenEntry {
    @Composable
    fun LegacyPoolAddition(
        state: CartAdditionUiState,
        onBack: () -> Unit,
        onHome: () -> Unit,
        onOpenCart: () -> Unit,
        onHelp: ((Int, String) -> Unit)?,
    )
}
