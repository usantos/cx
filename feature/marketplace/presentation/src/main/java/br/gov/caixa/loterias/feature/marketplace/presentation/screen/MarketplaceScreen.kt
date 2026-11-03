package br.gov.caixa.loterias.feature.marketplace.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceUiEntry
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import java.math.BigDecimal

@Composable
fun MarketplaceScreen(
    state: MarketplaceUiState,
    onBack: () -> Unit,
    onFilter: () -> Unit,
    onHelp: () -> Unit,
    onRetailerSelected: (Retailer) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onPoolSelected: (String) -> Unit,
    onToggleExpanded: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onSelectTab: (MarketplaceTab) -> Unit,
    onRemoveRetailer: (Long) -> Unit,
    retailerView: Boolean,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    onRetailerSearch: () -> Unit = {},
) {
    FeatureModuleContent(
        module = AppModule.MARKETPLACE,
        entryType = MarketplaceUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.MarketplaceScreen(
            state = state,
            navigation = br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenNavigation(
                onBack,
                onFilter,
                onHelp,
                onRetailerSelected,
                onPoolSelected,
                onCart,
                onRetailerSearch
            ),
            actions = br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenActions(
                onRetry, onLoadMore, onToggleExpanded, onDecrease, onIncrease, onFavorite, onAddToCart, onSelectTab, onRemoveRetailer
            ),
            retailerView = retailerView,
            cartTotal = cartTotal,
            cartBetCount = cartBetCount
        )
    }
}
