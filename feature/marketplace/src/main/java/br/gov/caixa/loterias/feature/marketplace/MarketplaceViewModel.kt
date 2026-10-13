package br.gov.caixa.loterias.feature.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.marketplace.SearchMarketplaceUseCase
import br.gov.caixa.loterias.core.domain.marketplace.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePoolOrder
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import br.gov.caixa.loterias.core.domain.retail.SetRetailerFavoriteUseCase
import br.gov.caixa.loterias.core.domain.retail.GetFavoriteRetailersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketplaceViewModel(
    private val searchMarketplace: SearchMarketplaceUseCase,
    private val filter: MarketplaceFilter,
    private val addMarketplacePoolToCart: AddMarketplacePoolToCartUseCase,
    private val getFavoriteRetailers: GetFavoriteRetailersUseCase,
    private val setRetailerFavorite: SetRetailerFavoriteUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        MarketplaceUiState(isLoading = true, lotteryFiltered = filter.lottery.isNotBlank()),
    )
    val state: StateFlow<MarketplaceUiState> = mutableState.asStateFlow()

    init {
        search()
        loadFavoriteRetailers()
    }

    fun retry() = search()

    fun onAction(action: MarketplaceAction) = when (action) {
        MarketplaceAction.Retry -> retry()
        MarketplaceAction.LoadMore -> loadMore()
        is MarketplaceAction.ToggleExpanded -> toggleExpanded(action.poolCode)
        is MarketplaceAction.IncreaseShares -> changeShares(action.poolCode, 1)
        is MarketplaceAction.DecreaseShares -> changeShares(action.poolCode, -1)
        is MarketplaceAction.RequestFavorite -> requestFavorite(action.poolCode)
        MarketplaceAction.ConfirmFavorite -> confirmFavorite()
        MarketplaceAction.CancelFavorite -> cancelFavorite()
        is MarketplaceAction.AddToCart -> addToCart(action.poolCode)
        MarketplaceAction.DismissActionError -> dismissActionError()
        is MarketplaceAction.SelectTab -> selectTab(action.tab)
        is MarketplaceAction.RequestFavoriteRetailerRemoval -> requestFavoriteRetailerRemoval(action.retailerId)
        MarketplaceAction.ConfirmFavoriteRetailerRemoval -> confirmFavoriteRetailerRemoval()
        MarketplaceAction.CancelFavoriteRetailerRemoval -> cancelFavoriteRetailerRemoval()
    }

    fun selectTab(tab: MarketplaceTab) {
        mutableState.value = mutableState.value.copy(selectedTab = tab)
    }

    fun requestFavoriteRetailerRemoval(retailerId: Long) {
        if (mutableState.value.favoriteRetailers.none { it.id == retailerId }) return
        mutableState.value = mutableState.value.copy(
            pendingFavoriteRetailerRemoval = retailerId,
            actionError = null,
        )
    }

    fun cancelFavoriteRetailerRemoval() {
        mutableState.value = mutableState.value.copy(pendingFavoriteRetailerRemoval = null)
    }

    fun confirmFavoriteRetailerRemoval() {
        val retailerId = mutableState.value.pendingFavoriteRetailerRemoval ?: return
        mutableState.value = mutableState.value.copy(pendingFavoriteRetailerRemoval = null)
        viewModelScope.launch(dispatchers.io) {
            when (setRetailerFavorite(retailerId, false)) {
                is AppResult.Success -> mutableState.value = mutableState.value.copy(
                    favoriteRetailers = mutableState.value.favoriteRetailers.filterNot { it.id == retailerId },
                    pools = mutableState.value.pools.map { pool ->
                        if (pool.lotteryId == retailerId) pool.copy(favoriteLottery = false) else pool
                    },
                )
                is AppResult.Failure -> mutableState.value = mutableState.value.copy(
                    actionError = "Não foi possível concluir esta operação. Por favor, tente novamente.",
                )
            }
        }
    }

    fun loadMore() {
        val snapshot = mutableState.value
        if (snapshot.isLoading || snapshot.isLoadingMore || !snapshot.canLoadMore) return
        load(snapshot.currentPage + 1, append = true)
    }

    fun toggleExpanded(poolCode: String) {
        if (mutableState.value.pools.none { it.code == poolCode }) return
        val expanded = mutableState.value.expandedPoolCodes
        mutableState.value = mutableState.value.copy(
            expandedPoolCodes = if (poolCode in expanded) expanded - poolCode else expanded + poolCode,
        )
    }

    fun increaseShares(poolCode: String) = changeShares(poolCode, 1)

    fun decreaseShares(poolCode: String) = changeShares(poolCode, -1)

    private fun changeShares(poolCode: String, delta: Int) {
        val state = mutableState.value
        if (poolCode in state.addingPoolCodes) return
        val pool = state.pools.firstOrNull { it.code == poolCode } ?: return
        val current = state.selectedShares[poolCode] ?: 1
        val updated = (current + delta).coerceIn(1, pool.availableShares.coerceAtLeast(1))
        if (updated != current) {
            mutableState.value = state.copy(selectedShares = state.selectedShares + (poolCode to updated))
        }
    }

    fun requestFavorite(poolCode: String) {
        val state = mutableState.value
        val pool = state.pools.firstOrNull { it.code == poolCode } ?: return
        if (pool.lotteryId <= 0 || pool.lotteryId in state.updatingFavoriteLotteryIds) return
        mutableState.value = state.copy(pendingFavoritePoolCode = poolCode, actionError = null)
    }

    fun cancelFavorite() {
        mutableState.value = mutableState.value.copy(pendingFavoritePoolCode = null)
    }

    fun confirmFavorite() {
        val state = mutableState.value
        val code = state.pendingFavoritePoolCode ?: return
        val pool = state.pools.firstOrNull { it.code == code } ?: return cancelFavorite()
        val newFavorite = !pool.favoriteLottery
        mutableState.value = state.copy(
            pendingFavoritePoolCode = null,
            updatingFavoriteLotteryIds = state.updatingFavoriteLotteryIds + pool.lotteryId,
            actionError = null,
        )
        viewModelScope.launch(dispatchers.io) {
            when (setRetailerFavorite(pool.lotteryId, newFavorite)) {
                is AppResult.Success -> mutableState.value = mutableState.value.copy(
                    pools = mutableState.value.pools.map { item ->
                        if (item.lotteryId == pool.lotteryId) item.copy(favoriteLottery = newFavorite) else item
                    },
                    updatingFavoriteLotteryIds = mutableState.value.updatingFavoriteLotteryIds - pool.lotteryId,
                )
                is AppResult.Failure -> mutableState.value = mutableState.value.copy(
                    updatingFavoriteLotteryIds = mutableState.value.updatingFavoriteLotteryIds - pool.lotteryId,
                    actionError = "Não foi possível concluir esta operação. Por favor, tente novamente.",
                )
            }
        }
    }

    fun addToCart(poolCode: String) {
        val state = mutableState.value
        if (poolCode in state.addingPoolCodes) return
        val pool = state.pools.firstOrNull { it.code == poolCode } ?: return
        val shares = state.selectedShares[poolCode] ?: 1
        if (shares !in 1..pool.availableShares) return
        mutableState.value = state.copy(
            addingPoolCodes = state.addingPoolCodes + poolCode,
            actionError = null,
        )
        viewModelScope.launch(dispatchers.io) {
            val order = MarketplacePoolOrder(
                code = pool.code,
                modality = pool.modality,
                contest = pool.contest,
                contestType = pool.contestType,
                drawDate = pool.drawDate,
                availableShares = pool.availableShares,
                requestedShares = shares,
                serviceFeePerShare = pool.serviceFeePerShare,
                firstSharePriceWithFee = pool.lastSharePriceWithFeeValue,
                regularSharePriceWithFee = pool.regularSharePriceWithFee,
            )
            when (val outcome = addMarketplacePoolToCart(order)) {
                is AppResult.Success -> {
                    val reservation = outcome.value.items.firstOrNull { item ->
                        item.kind == CartItemKind.POOL_SHARE && item.poolShare?.code == pool.code
                    }?.poolShare
                    mutableState.value = if (reservation?.expiresAt.isNullOrBlank()) {
                        mutableState.value.copy(
                            addingPoolCodes = mutableState.value.addingPoolCodes - poolCode,
                            actionError = "Não foi possível confirmar a reserva da cota. Tente novamente.",
                        )
                    } else {
                        mutableState.value.copy(
                            pools = mutableState.value.pools.map { item ->
                                if (item.code == poolCode) item.copy(
                                    availableShares = (item.availableShares - shares).coerceAtLeast(0),
                                ) else item
                            },
                            selectedShares = mutableState.value.selectedShares - poolCode,
                            addingPoolCodes = mutableState.value.addingPoolCodes - poolCode,
                            confirmation = MarketplacePoolConfirmation(
                                poolCode = pool.code,
                                modality = pool.modality,
                                expiresAt = requireNotNull(reservation).expiresAt,
                                contest = pool.contest,
                                drawDate = pool.drawDate,
                                amount = order.totalAmount,
                            ),
                        )
                    }
                }
                is AppResult.Failure -> mutableState.value = mutableState.value.copy(
                    addingPoolCodes = mutableState.value.addingPoolCodes - poolCode,
                    actionError = "Não foi possível adicionar a cota ao carrinho. Tente novamente.",
                )
            }
        }
    }

    fun consumeConfirmation() {
        mutableState.value = mutableState.value.copy(confirmation = null)
    }

    fun dismissActionError() {
        mutableState.value = mutableState.value.copy(actionError = null)
    }

    private fun search() = load(page = 0, append = false)

    private fun loadFavoriteRetailers() {
        mutableState.value = mutableState.value.copy(
            favoriteRetailersLoading = true,
            favoriteRetailersError = null,
        )
        viewModelScope.launch(dispatchers.io) {
            when (val result = getFavoriteRetailers()) {
                is AppResult.Success -> mutableState.value = mutableState.value.copy(
                    favoriteRetailers = result.value,
                    favoriteRetailersLoading = false,
                    favoriteRetailersError = null,
                )
                is AppResult.Failure -> mutableState.value = mutableState.value.copy(
                    favoriteRetailersLoading = false,
                    favoriteRetailersError = "Não foi possível carregar as lotéricas favoritas.",
                )
            }
        }
    }

    private fun load(page: Int, append: Boolean) {
        mutableState.value = if (append) {
            mutableState.value.copy(isLoadingMore = true, errorMessage = null)
        } else {
            mutableState.value.copy(isLoading = true, errorMessage = null)
        }
        viewModelScope.launch(dispatchers.io) {
            when (val outcome = searchMarketplace(filter, page)) {
                is AppResult.Success -> {
                    val result = outcome.value
                    val current = mutableState.value
                    mutableState.value = current.copy(
                        pools = if (append) current.pools + result.pools else result.pools,
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = null,
                        currentPage = result.currentPage,
                        canLoadMore = result.canLoadMore,
                        totalRecords = result.totalRecords,
                    )
                }
                is AppResult.Failure -> {
                    mutableState.value = mutableState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = "Não foi possível carregar os bolões. Tente novamente.",
                    )
                }
            }
        }
    }
}
