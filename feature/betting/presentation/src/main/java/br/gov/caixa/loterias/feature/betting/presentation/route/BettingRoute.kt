package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowEffect
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingFlowViewModel
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingScreen

@Composable
fun BettingRoute(viewModel: BettingFlowViewModel, onBack: () -> Unit, onComplete: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                BettingFlowEffect.NavigateBack -> onBack()
                BettingFlowEffect.Completed -> onComplete()
            }
        }
    }
    BettingScreen(
        state = state,
        onPageSelected = viewModel::selectPage,
        onBack = viewModel::back,
        onPrimary = viewModel::primaryAction,
        onSecondary = viewModel::secondaryAction,
        onToggleDoNotShowAgain = viewModel::toggleDoNotShowAgain,
    )
}
