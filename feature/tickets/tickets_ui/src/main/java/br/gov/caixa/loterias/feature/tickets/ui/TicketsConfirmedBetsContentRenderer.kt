package br.gov.caixa.loterias.feature.tickets.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsConfirmedBetsContentEntry
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetsListActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import java.math.BigDecimal

internal class TicketsConfirmedBetsContentRenderer : TicketsConfirmedBetsContentEntry {
    @Composable
    override fun ConfirmedBetsContent(
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
    ) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetsContentDelivered(
            state,
            onBack,
            onOpen,
            onAddToCart,
            onBetNow,
            onHelp,
            cartTotal,
            cartBetCount,
            onCart,
            privateDataEnabled,
            actions
        )
    }
}
