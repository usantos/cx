package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultVisualData

interface TicketsTicketResultScreenEntry {
    @Composable
    fun TicketResultScreen(
        bets: List<ConfirmedBet>,
        onBack: () -> Unit,
        visualData: TicketResultVisualData? = null,
        onBetNow: (() -> Unit)? = null,
        onHelp: () -> Unit = {},
    )
}
