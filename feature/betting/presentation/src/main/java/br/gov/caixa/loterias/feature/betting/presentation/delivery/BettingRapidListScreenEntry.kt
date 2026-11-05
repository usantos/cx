package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListUiState

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
