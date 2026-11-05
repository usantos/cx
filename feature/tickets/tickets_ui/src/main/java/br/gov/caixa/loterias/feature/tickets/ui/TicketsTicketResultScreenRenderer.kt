package br.gov.caixa.loterias.feature.tickets.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultVisualData
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsTicketResultScreenEntry

internal class TicketsTicketResultScreenRenderer : TicketsTicketResultScreenEntry {
    @Composable
    override fun TicketResultScreen(
        bets: List<ConfirmedBet>,
        onBack: () -> Unit,
        visualData: TicketResultVisualData?,
        onBetNow: (() -> Unit)?,
        onHelp: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketResultScreenDelivered(bets, onBack, visualData, onBetNow, onHelp)
    }
}
