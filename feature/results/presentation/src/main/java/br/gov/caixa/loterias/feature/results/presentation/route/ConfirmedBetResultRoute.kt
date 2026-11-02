package br.gov.caixa.loterias.feature.results.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.results.presentation.screen.ConfirmedBetResultScreen
import br.gov.caixa.loterias.feature.results.presentation.viewmodel.ConfirmedBetResultsViewModel
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

@Composable
fun ConfirmedBetResultRoute(
    bet: ConfirmedBet,
    viewModel: ConfirmedBetResultsViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onServiceRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit = {},
    onRedeemPrize: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.serviceFailure) {
        (state.serviceFailure as? br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RedirectRequired)?.let {
            onServiceRedirect(it.destination)
        }
    }
    ConfirmedBetResultScreen(bet, state, onBack, onComplete, viewModel::retry, onRedeemPrize)
}
