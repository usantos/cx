package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingRapidListScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListUiState

internal class BettingRapidListScreenRenderer : BettingRapidListScreenEntry {
    @Composable
    override fun RapidListScreen(
        state: RapidListUiState,
        onBack: () -> Unit,
        onConfiguration: () -> Unit,
        onGenerate: () -> Unit,
        onAdvance: () -> Unit,
        onRemove: (CartItem) -> Unit,
        onDismissError: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.RapidListScreenDelivered(
            state,
            onBack,
            onConfiguration,
            onGenerate,
            onAdvance,
            onRemove,
            onDismissError
        )
    }
}
