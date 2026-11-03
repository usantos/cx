package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
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
    val latestState by rememberUpdatedState(currentState)
    val latestOnAdded by rememberUpdatedState(onAdded)
    LaunchedEffect(viewModel, state.addedToCart) {
        if (!state.addedToCart) return@LaunchedEffect
        // O commit pode consumir a confirmação antes da navegação; releia no próximo frame aplicado.
        withFrameNanos { }
        if (latestState().addedToCart && viewModel.state.value.addedToCart) latestOnAdded()
    }
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
