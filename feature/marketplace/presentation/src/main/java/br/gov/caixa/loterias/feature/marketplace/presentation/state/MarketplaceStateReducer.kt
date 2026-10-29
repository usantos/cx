package br.gov.caixa.loterias.feature.marketplace.presentation.state

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplacePoolConfirmation
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceTab

internal sealed interface MarketplaceLoadingAction : MarketplaceStateAction

internal sealed interface MarketplaceCartAction : MarketplaceStateAction

internal sealed interface MarketplaceRetailerAction : MarketplaceStateAction

internal sealed interface MarketplacePoolAction : MarketplaceStateAction

internal fun reduceMarketplaceState(state: MarketplaceUiState, action: MarketplaceStateAction): MarketplaceUiState = when (action) {
    is MarketplaceLoadingAction -> reduceMarketplaceLoadingState(state, action)
    is MarketplaceCartAction -> reduceMarketplaceCartState(state, action)
    is MarketplaceRetailerAction -> reduceMarketplaceRetailerState(state, action)
    is MarketplacePoolAction -> reduceMarketplacePoolState(state, action)
}

private fun reduceMarketplaceLoadingState(state: MarketplaceUiState, action: MarketplaceLoadingAction): MarketplaceUiState = when (action) {
    is MarketplaceStateAction.HeaderReceived -> if (action.stale) {
        state
    } else {
        state.copy(
            contestHeader = action.game,
            modalityNames = action.names,
            specialContests = action.configured,
            contestSpecialName = action.specialName
        )
    }

    is MarketplaceStateAction.SearchStarted -> if (action.append) {
        state.copy(isLoadingMore = true, errorMessage = null)
    } else {
        state.copy(isLoading = true, isLoadingMore = false, errorMessage = null)
    }

    is MarketplaceStateAction.SearchReceived -> if (action.stale) {
        state
    } else {
        state.copy(
            pools = if (action.append) state.pools + action.result.pools else action.result.pools,
            isLoading = false,
            isLoadingMore = false,
            errorMessage = null,
            currentPage = action.result.currentPage,
            canLoadMore = action.result.canLoadMore,
            totalRecords = action.result.totalRecords,
            selectedShares = if (action.append) state.selectedShares else emptyMap(),
            expandedPoolCodes = if (action.append) state.expandedPoolCodes else emptySet()
        )
    }

    is MarketplaceStateAction.SearchFailed -> if (action.stale) {
        state
    } else {
        state.copy(
            isLoading = false,
            isLoadingMore = false,
            errorMessage = if (action.filteredSearch) {
                "Ocorreu um erro.\nTente novamente"
            } else {
                action.message
            }
        )
    }
}

private fun reduceMarketplaceCartState(state: MarketplaceUiState, action: MarketplaceCartAction): MarketplaceUiState = when (action) {
    is MarketplaceStateAction.CartAdditionClaimed -> run reduce@{
        if (action.poolCode in state.addingPoolCodes) return@reduce state
        val pool = state.pools.firstOrNull { it.code == action.poolCode } ?: return@reduce state
        val shares = state.selectedShares[action.poolCode] ?: 1
        if (shares !in 1..pool.availableShares) return@reduce state
        state.copy(addingPoolCodes = state.addingPoolCodes + action.poolCode, actionError = null)
    }

    is MarketplaceStateAction.CartReservationReceived -> if (action.reservation?.expiresAt.isNullOrBlank()) {
        state.copy(
            addingPoolCodes = state.addingPoolCodes - action.poolCode,
            actionError = action.message
        )
    } else {
        state.copy(
            pools = state.pools.map { item ->
                if (item.code == action.poolCode) {
                    item.copy(
                        availableShares = (item.availableShares - action.shares).coerceAtLeast(0)
                    )
                } else {
                    item
                }
            },
            selectedShares = state.selectedShares - action.poolCode,
            addingPoolCodes = state.addingPoolCodes - action.poolCode,
            confirmation = MarketplacePoolConfirmation(
                poolCode = action.pool.code,
                modality = action.pool.modality,
                expiresAt = requireNotNull(action.reservation).expiresAt,
                contest = action.pool.contest,
                drawDate = action.pool.drawDate,
                amount = action.order.totalAmount
            )
        )
    }

    is MarketplaceStateAction.CartAdditionFailed -> state.copy(
        addingPoolCodes = state.addingPoolCodes - action.poolCode,
        actionError = action.message
    )

    MarketplaceStateAction.ConfirmationConsumed -> state.copy(confirmation = null)
}

private fun reduceMarketplaceRetailerState(state: MarketplaceUiState, action: MarketplaceRetailerAction): MarketplaceUiState =
    when (action) {
        is MarketplaceStateAction.PoolFavoriteRemoved -> state.copy(
            favoriteRetailers = state.favoriteRetailers.filterNot { it.id == action.retailerId },
            favoriteRetailersLoading = false,
            favoriteRetailersOpening = false,
            favoriteRetailersError = null
        )

        is MarketplaceStateAction.RetailerRemovalRequested -> reduceMarketplaceRetailerStateRetailerRemovalRequested(state, action)

        MarketplaceStateAction.RetailerRemovalCancelled -> state.copy(pendingFavoriteRetailerRemoval = null)

        MarketplaceStateAction.RetailerRemovalClaimed -> reduceMarketplaceRetailerStateRetailerRemovalClaimed(state)

        is MarketplaceStateAction.RetailerRemoved -> state.copy(
            favoriteRetailersLoading = false,
            favoriteRetailersOpening = false,
            updatingFavoriteLotteryIds = state.updatingFavoriteLotteryIds - action.retailerId,
            favoriteRetailers = state.favoriteRetailers.filterNot { it.id == action.retailerId },
            pools = state.pools.map { pool ->
                if (pool.lotteryId == action.retailerId) pool.copy(favoriteLottery = false) else pool
            }
        )

        is MarketplaceStateAction.RetailerRemovalFailed -> state.copy(
            updatingFavoriteLotteryIds = state.updatingFavoriteLotteryIds - action.retailerId,
            actionError = action.message
        )

        is MarketplaceStateAction.FavoriteRetailersLoading -> state.copy(
            favoriteRetailersLoading = true,
            favoriteRetailersOpening = action.openAfterLoad,
            favoriteRetailersError = null
        )

        is MarketplaceStateAction.FavoriteRetailersReceived -> reduceMarketplaceRetailerStateFavoriteRetailersReceived(state, action)
    }

private fun reduceMarketplacePoolState(state: MarketplaceUiState, action: MarketplacePoolAction): MarketplaceUiState = when (action) {
    is MarketplaceStateAction.SharesChanged -> reduceMarketplacePoolStateSharesChanged(state, action)

    is MarketplaceStateAction.PoolFavoriteRequested -> reduceMarketplacePoolStatePoolFavoriteRequested(state, action)

    MarketplaceStateAction.PoolFavoriteCancelled -> state.copy(pendingFavoritePoolCode = null)

    MarketplaceStateAction.PoolFavoriteClaimed -> reduceMarketplacePoolStatePoolFavoriteClaimed(state)

    is MarketplaceStateAction.PoolFavoriteSaved -> state.copy(
        pools = state.pools.map { item ->
            if (item.lotteryId == action.retailerId) item.copy(favoriteLottery = action.favorite) else item
        },
        updatingFavoriteLotteryIds = state.updatingFavoriteLotteryIds - action.retailerId
    )

    is MarketplaceStateAction.PoolFavoriteFailed -> state.copy(
        updatingFavoriteLotteryIds = state.updatingFavoriteLotteryIds - action.retailerId,
        actionError = action.message
    )

    is MarketplaceStateAction.TabSelected -> state.copy(selectedTab = action.tab)

    is MarketplaceStateAction.PoolExpanded -> reduceMarketplacePoolStatePoolExpanded(state, action)

    MarketplaceStateAction.ActionErrorDismissed -> state.copy(actionError = null)
}

private fun reduceMarketplaceRetailerStateRetailerRemovalRequested(
    state: MarketplaceUiState,
    action: MarketplaceStateAction.RetailerRemovalRequested,
): MarketplaceUiState = if (action.retailerId in state.updatingFavoriteLotteryIds ||
    state.favoriteRetailers.none { it.id == action.retailerId }
) {
    state
} else {
    state.copy(pendingFavoriteRetailerRemoval = action.retailerId, actionError = null)
}

private fun reduceMarketplaceRetailerStateRetailerRemovalClaimed(state: MarketplaceUiState): MarketplaceUiState = run reduce@{
    val retailerId =
        state.pendingFavoriteRetailerRemoval ?: return@reduce state
    if (retailerId in state.updatingFavoriteLotteryIds) return@reduce state
    state.copy(
        pendingFavoriteRetailerRemoval = null,
        updatingFavoriteLotteryIds = state.updatingFavoriteLotteryIds + retailerId
    )
}

private fun reduceMarketplaceRetailerStateFavoriteRetailersReceived(
    state: MarketplaceUiState,
    action: MarketplaceStateAction.FavoriteRetailersReceived,
): MarketplaceUiState = run reduce@{
    if (action.stale) return@reduce state
    when (action.result) {
        is AppResult.Success -> state.copy(
            favoriteRetailers = action.result.value,
            pools = if (action.synchronizePools) {
                val favoriteIds = action.result.value.mapTo(hashSetOf()) { it.id }
                state.pools.map { it.copy(favoriteLottery = it.lotteryId in favoriteIds) }
            } else {
                state.pools
            },
            favoriteRetailersLoading = false,
            favoriteRetailersOpening = false,
            favoriteRetailersError = null,
            selectedTab = if (action.openAfterLoad) MarketplaceTab.FAVORITE_RETAILERS else state.selectedTab
        )

        is AppResult.Failure -> state.copy(
            favoriteRetailers = if (action.openAfterLoad) emptyList() else state.favoriteRetailers,
            favoriteRetailersLoading = false,
            favoriteRetailersOpening = false,
            favoriteRetailersError = action.message,
            selectedTab = if (action.openAfterLoad) MarketplaceTab.FAVORITE_RETAILERS else state.selectedTab
        )
    }
}

private fun reduceMarketplacePoolStateSharesChanged(
    state: MarketplaceUiState,
    action: MarketplaceStateAction.SharesChanged,
): MarketplaceUiState = run reduce@{
    if (action.poolCode in state.addingPoolCodes) return@reduce state
    val pool = state.pools.firstOrNull { it.code == action.poolCode } ?: return@reduce state
    val current = state.selectedShares[action.poolCode] ?: 1
    val updated = (current + action.delta).coerceIn(1, pool.availableShares.coerceAtLeast(1))
    if (updated != current) state.copy(selectedShares = state.selectedShares + (action.poolCode to updated)) else state
}

private fun reduceMarketplacePoolStatePoolFavoriteRequested(
    state: MarketplaceUiState,
    action: MarketplaceStateAction.PoolFavoriteRequested,
): MarketplaceUiState = run reduce@{
    val pool =
        state.pools.firstOrNull { it.code == action.poolCode } ?: return@reduce state
    if (pool.lotteryId <= 0 || pool.lotteryId in state.updatingFavoriteLotteryIds) {
        state
    } else {
        state.copy(pendingFavoritePoolCode = action.poolCode, actionError = null)
    }
}

private fun reduceMarketplacePoolStatePoolFavoriteClaimed(state: MarketplaceUiState): MarketplaceUiState = run reduce@{
    val code = state.pendingFavoritePoolCode ?: return@reduce state
    val pool = state.pools.firstOrNull { it.code == code }
        ?: return@reduce state.copy(pendingFavoritePoolCode = null)
    state.copy(
        pendingFavoritePoolCode = null,
        updatingFavoriteLotteryIds = state.updatingFavoriteLotteryIds + pool.lotteryId,
        actionError = null
    )
}

private fun reduceMarketplacePoolStatePoolExpanded(
    state: MarketplaceUiState,
    action: MarketplaceStateAction.PoolExpanded,
): MarketplaceUiState = run reduce@{
    if (state.pools.none { it.code == action.poolCode }) return@reduce state
    val expanded = state.expandedPoolCodes
    state.copy(expandedPoolCodes = if (action.poolCode in expanded) expanded - action.poolCode else expanded + action.poolCode)
}
