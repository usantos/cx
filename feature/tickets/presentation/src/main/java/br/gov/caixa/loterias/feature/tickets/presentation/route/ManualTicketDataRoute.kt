package br.gov.caixa.loterias.feature.tickets.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketContent
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

@Composable
fun ManualTicketDataRoute(
    viewModel: TicketsViewModel,
    onBack: () -> Unit,
    onResult: (List<ConfirmedBet>) -> Unit,
    onHelp: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.loaded, state.bets, state.ticketResult) {
        if (viewModel.consumeTicketResultNavigation()) onResult(state.bets)
    }
    val manualCodeValid = state.ticketCode.length >= TicketsViewModel.MINIMUM_MANUAL_TICKET_LENGTH
    ManualTicketContent(
        state = state,
        onBack = onBack,
        onHelp = onHelp,
        manualCodeValid = manualCodeValid,
        actions = ManualTicketActions(
            changeTicketCode = viewModel::changeTicketCode,
            changeTicketRetailerCode = viewModel::changeTicketRetailerCode,
            checkTicket = { viewModel.checkTicket() }
        )
    )
}
