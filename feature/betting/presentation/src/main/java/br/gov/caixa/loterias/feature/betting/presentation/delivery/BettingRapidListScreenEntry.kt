package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListUiState
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

interface BettingRapidListScreenEntry {
    @Composable
    fun RapidListScreen(
        state: RapidListUiState,
        onBack: () -> Unit,
        onConfiguration: () -> Unit,
        onGenerate: () -> Unit,
        onAdvance: () -> Unit,
        onRemove: (CartItem) -> Unit,
        onDismissError: () -> Unit,
    )
}
