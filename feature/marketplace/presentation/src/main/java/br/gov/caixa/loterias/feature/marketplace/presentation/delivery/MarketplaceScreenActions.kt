package br.gov.caixa.loterias.feature.marketplace.presentation.delivery

import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab

data class MarketplaceScreenActions(
    val onRetry: () -> Unit,
    val onLoadMore: () -> Unit,
    val onToggleExpanded: (String) -> Unit,
    val onDecrease: (String) -> Unit,
    val onIncrease: (String) -> Unit,
    val onFavorite: (String) -> Unit,
    val onAddToCart: (String) -> Unit,
    val onSelectTab: (MarketplaceTab) -> Unit,
    val onRemoveRetailer: (Long) -> Unit,
)
