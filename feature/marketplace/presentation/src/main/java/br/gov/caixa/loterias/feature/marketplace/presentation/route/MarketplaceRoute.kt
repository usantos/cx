package br.gov.caixa.loterias.feature.marketplace.presentation.route

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplacePoolConfirmation
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreen
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceViewModel

@Composable
fun MarketplaceRoute(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onFilter: () -> Unit,
    onHelp: () -> Unit,
    onRetailerSelected: (Retailer) -> Unit,
    onPoolSelected: (String) -> Unit,
    onPoolReserved: (MarketplacePoolConfirmation) -> Unit,
    retailerView: Boolean = false,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    refreshRevision: Int = 0,
    onRetailerSearch: () -> Unit = {},
) {
    var refreshFavoritesOnReturn by rememberSaveable { mutableStateOf(false) }
    LifecycleResumeEffect(viewModel) {
        if (refreshFavoritesOnReturn) {
            refreshFavoritesOnReturn = false
            viewModel.refreshFavoriteRetailers()
        }
        onPauseOrDispose {}
    }
    LaunchedEffect(viewModel, refreshRevision) {
        viewModel.observeChildRevision(refreshRevision)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.confirmation) {
        state.confirmation?.let { confirmation ->
            viewModel.consumeConfirmation()
            onPoolReserved(confirmation)
        }
    }
    MarketplaceScreen(
        state,
        onBack,
        onFilter,
        onHelp,
        onRetailerSelected,
        { viewModel.onAction(MarketplaceAction.Retry) },
        { viewModel.onAction(MarketplaceAction.LoadMore) },
        onPoolSelected,
        onToggleExpanded = { viewModel.onAction(MarketplaceAction.ToggleExpanded(it)) },
        onDecrease = { viewModel.onAction(MarketplaceAction.DecreaseShares(it)) },
        onIncrease = { viewModel.onAction(MarketplaceAction.IncreaseShares(it)) },
        onFavorite = { viewModel.onAction(MarketplaceAction.RequestFavorite(it)) },
        onAddToCart = { viewModel.onAction(MarketplaceAction.AddToCart(it)) },
        onSelectTab = { viewModel.onAction(MarketplaceAction.SelectTab(it)) },
        onRemoveRetailer = { viewModel.onAction(MarketplaceAction.RequestFavoriteRetailerRemoval(it)) },
        retailerView = retailerView,
        cartTotal = cartTotal,
        cartBetCount = cartBetCount,
        onCart = onCart,
        onRetailerSearch = {
            refreshFavoritesOnReturn = true
            onRetailerSearch()
        }
    )
    MarketplaceDialogs(state) { viewModel.onAction(it) }
}
