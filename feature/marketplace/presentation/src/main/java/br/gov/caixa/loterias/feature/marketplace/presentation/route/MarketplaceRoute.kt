package br.gov.caixa.loterias.feature.marketplace.presentation.route

import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
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
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
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
    state.pendingFavoritePoolCode?.let { code ->
        val pool = state.pools.firstOrNull { it.code == code }
        if (pool != null) {
            CaixaDSDialog(
                title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                message = if (pool.favoriteLottery) {
                    uiStrings.getString(LocalizedR.string.ui_deseja_excluir_1_s_da_sua_lista_de_f_a0d867, pool.lotteryName)
                } else {
                    uiStrings.getString(LocalizedR.string.ui_deseja_incluir_1_s_na_sua_lista_de_f_5f60aa, pool.lotteryName)
                },
                positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
                onPositive = { viewModel.onAction(MarketplaceAction.ConfirmFavorite) },
                negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
                onNegative = { viewModel.onAction(MarketplaceAction.CancelFavorite) }
            )
        }
    }
    state.actionError?.let { message ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { viewModel.onAction(MarketplaceAction.DismissActionError) }
        )
    }
    state.pendingFavoriteRetailerRemoval?.let { retailerId ->
        val retailer = state.favoriteRetailers.firstOrNull { it.id == retailerId }
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(
                LocalizedR.string.ui_deseja_excluir_1_s_da_sua_lista_de_f_a0d867,
                retailer?.fantasyName.orEmpty()
            ),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
            onPositive = { viewModel.onAction(MarketplaceAction.ConfirmFavoriteRetailerRemoval) },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = { viewModel.onAction(MarketplaceAction.CancelFavoriteRetailerRemoval) }
        )
    }
    if (state.isLoading || state.isLoadingMore || state.favoriteRetailersOpening || state.addingPoolCodes.isNotEmpty() ||
        state.updatingFavoriteLotteryIds.isNotEmpty()
    ) {
        CaixaDSAuthenticationLoadingDialog()
    }
}
