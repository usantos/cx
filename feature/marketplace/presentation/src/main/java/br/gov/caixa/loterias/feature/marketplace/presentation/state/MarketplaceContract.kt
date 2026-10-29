package br.gov.caixa.loterias.feature.marketplace.presentation.state

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer

enum class MarketplaceTab { POOLS, FAVORITE_RETAILERS }

data class MarketplaceUiState(
    val pools: List<MarketplacePool> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 0,
    val canLoadMore: Boolean = false,
    val totalRecords: Int = 0,
    val expandedPoolCodes: Set<String> = emptySet(),
    val selectedShares: Map<String, Int> = emptyMap(),
    val pendingFavoritePoolCode: String? = null,
    val updatingFavoriteLotteryIds: Set<Long> = emptySet(),
    val addingPoolCodes: Set<String> = emptySet(),
    val actionError: String? = null,
    val confirmation: MarketplacePoolConfirmation? = null,
    val lotteryFiltered: Boolean = false,
    val selectedTab: MarketplaceTab = MarketplaceTab.POOLS,
    val favoriteRetailers: List<Retailer> = emptyList(),
    val favoriteRetailersLoading: Boolean = false,
    val favoriteRetailersOpening: Boolean = false,
    val favoriteRetailersError: String? = null,
    val pendingFavoriteRetailerRemoval: Long? = null,
    val contestHeader: br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters? = null,
    val modalityNames: Map<Pair<String, Int>, String> = emptyMap(),
    val showModalityInList: Boolean = lotteryFiltered,
    val specialContests: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests = br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests(),
    val contestSpecialName: String? = null,
) {
    // A CX usa uma resposta de busca ordenada para as duas seções, nunca um endpoint separado para o carrossel.
    val carouselPools: List<MarketplacePool> get() = pools.take(4)
    val listPools: List<MarketplacePool> get() = pools.drop(4)
}

sealed interface MarketplaceAction {
    data object Retry : MarketplaceAction
    data object LoadMore : MarketplaceAction
    data class ToggleExpanded(val poolCode: String) : MarketplaceAction
    data class IncreaseShares(val poolCode: String) : MarketplaceAction
    data class DecreaseShares(val poolCode: String) : MarketplaceAction
    data class RequestFavorite(val poolCode: String) : MarketplaceAction
    data object ConfirmFavorite : MarketplaceAction
    data object CancelFavorite : MarketplaceAction
    data class AddToCart(val poolCode: String) : MarketplaceAction
    data object DismissActionError : MarketplaceAction
    data class SelectTab(val tab: MarketplaceTab) : MarketplaceAction
    data class RequestFavoriteRetailerRemoval(val retailerId: Long) : MarketplaceAction
    data object ConfirmFavoriteRetailerRemoval : MarketplaceAction
    data object CancelFavoriteRetailerRemoval : MarketplaceAction
}
