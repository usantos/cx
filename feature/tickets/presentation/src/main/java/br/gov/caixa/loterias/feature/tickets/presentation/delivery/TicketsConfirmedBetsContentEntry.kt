package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetsListActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import java.math.BigDecimal

interface TicketsConfirmedBetsContentEntry {
    @Composable
    fun ConfirmedBetsContent(
        state: TicketsUiState,
        onBack: () -> Unit,
        onOpen: (ConfirmedBet) -> Unit,
        onAddToCart: ((ConfirmedBet) -> Unit)?,
        onBetNow: (() -> Unit)?,
        onHelp: (() -> Unit)?,
        cartTotal: BigDecimal,
        cartBetCount: Int,
        onCart: (() -> Unit)?,
        privateDataEnabled: Boolean,
        actions: ConfirmedBetsListActions,
    )
}
