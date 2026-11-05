package br.gov.caixa.loterias.feature.tickets.presentation.route

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultVisualData
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketResultScreen

@Composable
fun TicketResultDataRoute(
    bets: List<ConfirmedBet>,
    onBack: () -> Unit,
    visualData: TicketResultVisualData? = null,
    onBetNow: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
) {
    TicketResultScreen(bets, onBack, visualData, onBetNow, onHelp)
}
