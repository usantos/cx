package br.gov.caixa.loterias.feature.marketplace.presentation.route

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplacePoolConfirmation
import br.gov.caixa.loterias.feature.marketplace.presentation.components.MarketplaceDetailDialogs
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceDetailScreen
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceDetailViewModel

@Composable
fun MarketplaceDetailRoute(viewModel: MarketplaceDetailViewModel, onBack: () -> Unit,
    onPoolReserved: (MarketplacePoolConfirmation) -> Unit,
    cartTotal: BigDecimal = BigDecimal.ZERO, cartBetCount: Int = 0, onCart: (() -> Unit)? = null,
    onSoldOut: () -> Unit = onBack,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose { }
    }
    LaunchedEffect(state.confirmation) {
        state.confirmation?.let { confirmation -> viewModel.consumeConfirmation(); onPoolReserved(confirmation) }
    }
    MarketplaceDetailScreen(state, onBack, { viewModel.onAction(MarketplaceDetailAction.Retry) },
        { viewModel.onAction(MarketplaceDetailAction.DecreaseShares) },
        { viewModel.onAction(MarketplaceDetailAction.IncreaseShares) },
        { viewModel.onAction(MarketplaceDetailAction.AddToCart) }, cartTotal, cartBetCount, onCart, viewModel::requestFavorite,
        specialName = state.specialName)
    MarketplaceDetailDialogs(state, viewModel, onSoldOut)
}
