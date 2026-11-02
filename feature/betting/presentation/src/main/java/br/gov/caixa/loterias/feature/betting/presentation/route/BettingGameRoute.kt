package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingGameScreen
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

@Composable
fun BettingGameRoute(
    viewModel: BettingGameViewModel,
    onBack: () -> Unit,
    onAdded: () -> Unit,
    shakeEnabled: Boolean = false,
    cartTotal: java.math.BigDecimal = java.math.BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
    fallbackModalityId: Int? = null,
    onFavoriteLoginRequired: (() -> Unit)? = null,
    isAuthenticated: Boolean = false,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    androidx.compose.runtime.SideEffect { viewModel.updateCartContext(isAuthenticated, cartTotal) }
    var showNumberQuantity by remember { mutableStateOf(false) }
    var lotecaDescriptionBindingKey by remember { mutableStateOf(0) }
    var showRecurring by remember { mutableStateOf(false) }
    var showCloverQuantity by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    var showEmptyCartMessage by remember { mutableStateOf(false) }
    val requestCart: () -> Unit = {
        requestBettingCart(viewModel, cartBetCount, onCart) { showEmptyCartMessage = true }
    }
    val returnOrConfirm: () -> Unit = {
        requestBettingBack(viewModel, onBack) { showExitConfirmation = true }
    }
    BettingGameRouteEffects(
        state,
        viewModel,
        shakeEnabled,
        bettingShakeBlocked(state, showExitConfirmation, showNumberQuantity, showCloverQuantity, showRecurring),
        returnOrConfirm,
        onAdded,
        currentState = { state }
    )

    val actions = bettingGameActions(viewModel, onFavoriteLoginRequired)

    BettingGameScreen(
        state = state,
        actions = actions,
        cartTotal = cartTotal,
        cartBetCount = cartBetCount,
        onCart = requestCart.takeIf { onCart != null },
        onClear = {
            clearBettingStep(viewModel, { state }) { lotecaDescriptionBindingKey++ }
        },
        onBack = returnOrConfirm,
        onHelp = { viewModel.checkpointNavigation(onHelp) },
        fallbackModalityId = fallbackModalityId,
        onRetry = viewModel::load
    ) {
        BettingGameModalityContent(
            state,
            actions,
            viewModel,
            lotecaDescriptionBindingKey,
            onNumberQuantity = { showNumberQuantity = true },
            onCloverQuantity = { showCloverQuantity = true },
            onHelp = { showHelp = true },
            onRecurring = { showRecurring = true }
        )
    }

    BettingGameNavigationOverlays(
        state,
        showExitConfirmation,
        showHelp,
        onConfirmExit = {
            showExitConfirmation = false
            viewModel.checkpointNavigation(onBack)
        },
        onDismissExit = { showExitConfirmation = false },
        onDismissHelp = { showHelp = false }
    )
    BettingCartDialogs(state, viewModel)
    BettingGameNumberQuantityOverlay(
        state,
        showNumberQuantity,
        onSelect = { quantity ->
            selectBettingNumberQuantity(viewModel, { state }, quantity) { lotecaDescriptionBindingKey++ }
        },
        onDismiss = { showNumberQuantity = false }
    )
    BettingGameComplementOverlays(
        state,
        viewModel,
        showCloverQuantity,
        showRecurring,
        onDismissClover = { showCloverQuantity = false },
        onDismissRecurring = { showRecurring = false }
    )
    BettingFavoriteFeedbackDialogs(state, viewModel)
    BettingEmptyCartOverlay(showEmptyCartMessage) { showEmptyCartMessage = false }
}
