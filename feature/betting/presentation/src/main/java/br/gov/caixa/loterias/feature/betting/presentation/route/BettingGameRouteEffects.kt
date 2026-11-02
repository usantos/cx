package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

@Composable
internal fun BettingGameRouteEffects(
    state: BettingGameUiState,
    viewModel: BettingGameViewModel,
    shakeEnabled: Boolean,
    shakeBlocked: Boolean,
    onBack: () -> Unit,
    onAdded: () -> Unit,
    currentState: () -> BettingGameUiState,
) {
    BackHandler(enabled = !state.loading, onBack = onBack)
    BettingShakeEffect(shakeEnabled, state, viewModel, shakeBlocked)
    LaunchedEffect(state.addedToCart) { if (currentState().addedToCart) onAdded() }
}

internal fun bettingShakeBlocked(
    state: BettingGameUiState,
    showExitConfirmation: Boolean,
    showNumberQuantity: Boolean,
    showCloverQuantity: Boolean,
    showRecurring: Boolean,
): Boolean = showExitConfirmation || showNumberQuantity || showCloverQuantity || showRecurring ||
    state.favoriteExpanded || state.favoriteOverwriteConfirmation || state.errorMessage != null ||
    state.warningMessage != null || state.duplicateConfirmation || state.dailyLimitConfirmation || state.addedToCart
