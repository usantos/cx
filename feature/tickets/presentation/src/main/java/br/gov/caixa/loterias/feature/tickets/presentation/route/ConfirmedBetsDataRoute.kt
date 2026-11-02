package br.gov.caixa.loterias.feature.tickets.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetsContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetsListActions
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import java.math.BigDecimal

@Composable
fun ConfirmedBetsDataRoute(
    viewModel: TicketsViewModel,
    onBack: () -> Unit,
    onOpen: (ConfirmedBet) -> Unit,
    onAddToCart: ((ConfirmedBet) -> Unit)? = null,
    onBetNow: (() -> Unit)? = null,
    onHelp: (() -> Unit)? = null,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    privateDataEnabled: Boolean = true,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel, privateDataEnabled) { if (privateDataEnabled && !state.loaded) viewModel.loadBets() }
    ConfirmedBetsContent(
        state = state,
        onBack = onBack,
        onOpen = onOpen,
        onAddToCart = onAddToCart,
        onBetNow = onBetNow,
        onHelp = onHelp,
        cartTotal = cartTotal,
        cartBetCount = cartBetCount,
        onCart = onCart,
        privateDataEnabled = privateDataEnabled,
        actions = ConfirmedBetsListActions(
            openBetFilters = viewModel::openBetFilters,
            retryBets = viewModel::retryBets,
            selectPeriod = {
                viewModel.selectBetPeriod(it)
                viewModel.applyBetFilters()
            }
        )
    )
    if (state.showBetFilters) {
        BetFiltersDialog(
            state,
            BetFiltersActions(
                closeBetFilters = viewModel::closeBetFilters,
                selectBetPeriod = viewModel::selectBetPeriod,
                selectBetStatus = viewModel::selectBetStatus,
                selectBetOrdering = viewModel::selectBetOrdering,
                selectBetType = viewModel::selectBetType,
                selectContestType = viewModel::selectContestType,
                selectModality = viewModel::selectModality,
                toggleSurprise = viewModel::toggleSurprise,
                toggleRecurring = viewModel::toggleRecurring,
                toggleCombo = viewModel::toggleCombo,
                applyBetFilters = viewModel::applyBetFilters,
                clearBetFilters = viewModel::clearBetFilters
            )
        )
    }
}
