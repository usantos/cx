package br.gov.caixa.loterias.feature.tickets.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.tickets.presentation.screen.TicketGameChoiceScreen
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

@Composable
fun TicketGameChoiceRoute(viewModel: TicketsViewModel, onBack: () -> Unit, onHelp: () -> Unit, onSave: (Set<Int>) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.loadTicketModalities() }
    TicketGameChoiceScreen(
        state,
        onBack,
        onHelp,
        onSave,
        viewModel::toggleTicketModality,
        onRetry = {
            viewModel.dismissTicketError()
            viewModel.loadTicketModalities()
        }
    )
}
