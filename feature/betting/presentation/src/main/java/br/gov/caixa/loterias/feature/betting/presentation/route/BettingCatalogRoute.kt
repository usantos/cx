package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.betting.presentation.screen.BetComboDetailScreen
import br.gov.caixa.loterias.feature.betting.presentation.screen.BetCombosScreen
import br.gov.caixa.loterias.feature.betting.presentation.screen.LotteryModalitiesScreen
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingCatalogViewModel
import java.math.BigDecimal

@Composable
fun LotteryModalitiesRoute(viewModel: BettingCatalogViewModel, onBack: () -> Unit, onSelect: (Int) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LotteryModalitiesScreen(state, onBack, onSelect, onRetry = viewModel::load)
}

@Composable
fun BetCombosRoute(
    viewModel: BettingCatalogViewModel,
    onBack: () -> Unit,
    onSelect: (Int) -> Unit,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BetCombosScreen(state, onBack, onSelect, cartTotal, cartBetCount, onCart, onRetry = viewModel::load, onHelp = onHelp)
}

@Composable
fun BetComboDetailRoute(
    viewModel: BettingCatalogViewModel,
    onBack: () -> Unit,
    onAdded: () -> Unit,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel, state.addedToCart) {
        if (state.addedToCart && viewModel.consumeCartAddition()) onAdded()
    }
    BetComboDetailScreen(
        state, onBack, cartTotal, cartBetCount, onCart,
        onRetry = viewModel::load,
        onDecreaseQuantity = viewModel::decreaseQuantity,
        onIncreaseQuantity = viewModel::increaseQuantity,
        onAddCombo = viewModel::addSelectedCombo,
        onToggleSurprise = viewModel::toggleSurprise,
        onHelp = onHelp
    )
}
