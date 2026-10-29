package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import androidx.lifecycle.Lifecycle
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision
import br.gov.caixa.loterias.core.navigation.destination.serviceRedirectRoute

/** A atualização do ciclo de vida e as decisões de domínio consumidas preservam suas chaves de efeito originais. */
@Composable
internal fun HomeRouteEffects(
    homeState: HomeUiState,
    lifecycleState: Lifecycle.State,
    viewModel: HomeViewModel,
    onOpen: (String) -> Unit,
    onBetEntry: (BetEntryDecision) -> Unit,
    onOtherBetting: (HomeBettingDecision) -> Unit,
    onPoolEntry: (PoolEntryDecision) -> Unit,
    refreshHomeOnResume: Boolean = true,
) {
    LaunchedEffect(lifecycleState, refreshHomeOnResume) {
        if (lifecycleState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
            viewModel.refreshUnreadNotificationCount()
            if (refreshHomeOnResume) viewModel.load()
            viewModel.refreshCart()
        }
    }
    LaunchedEffect(homeState.notificationRedirect, lifecycleState) {
        if (lifecycleState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
            homeState.notificationRedirect?.let { event ->
                if (viewModel.consumeNotificationRedirect(event)) onOpen(serviceRedirectRoute(event.destination))
            }
        }
    }
    LaunchedEffect(homeState.betEntryDecision) {
        homeState.betEntryDecision?.let {
            viewModel.consumeBetEntryDecision()
            onBetEntry(it)
        }
    }
    LaunchedEffect(homeState.otherBettingDecision) {
        homeState.otherBettingDecision?.let {
            viewModel.consumeOtherBettingDecision()
            onOtherBetting(it)
        }
    }
    LaunchedEffect(homeState.poolEntryDecision) {
        homeState.poolEntryDecision?.let {
            viewModel.consumePoolEntryDecision()
            onPoolEntry(it)
        }
    }
}
