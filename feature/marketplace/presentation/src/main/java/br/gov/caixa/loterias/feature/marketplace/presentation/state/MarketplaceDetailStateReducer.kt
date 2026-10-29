package br.gov.caixa.loterias.feature.marketplace.presentation.state

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail

internal sealed interface MarketplaceDetailFavoriteAction : MarketplaceDetailStateAction

internal sealed interface MarketplaceDetailStateAction {
    data class FavoriteRequested(val favorite: Boolean) : MarketplaceDetailFavoriteAction
    data object FavoriteStarted : MarketplaceDetailFavoriteAction
    data class FavoriteSaved(val favorite: Boolean) : MarketplaceDetailFavoriteAction
    data class FavoriteFailed(val message: String) : MarketplaceDetailFavoriteAction
    data object FavoriteCancelled : MarketplaceDetailFavoriteAction
    data object FavoriteErrorDismissed : MarketplaceDetailFavoriteAction
    data object SharesIncreased : MarketplaceDetailStateAction
    data object SharesDecreased : MarketplaceDetailStateAction
    data object CartAdditionStarted : MarketplaceDetailStateAction
    data class CartAdditionFailed(val message: String) : MarketplaceDetailStateAction
    data class CartReservationReceived(val confirmation: MarketplacePoolConfirmation) : MarketplaceDetailStateAction
    data object ConfirmationConsumed : MarketplaceDetailStateAction
    data object SoldOutNoticeConsumed : MarketplaceDetailStateAction
    data object LoadingStarted : MarketplaceDetailStateAction
    data class DetailReceived(val detail: MarketplacePoolDetail, val specialName: String?, val soldOutNotice: String?) :
        MarketplaceDetailStateAction
    data class LoadingFailed(val message: String) : MarketplaceDetailStateAction
}

internal fun reduceMarketplaceDetailState(state: MarketplaceDetailUiState, action: MarketplaceDetailStateAction): MarketplaceDetailUiState =
    when (action) {
        is MarketplaceDetailFavoriteAction -> reduceMarketplaceDetailFavoriteState(state, action)

        MarketplaceDetailStateAction.SharesIncreased -> reduceMarketplaceDetailStateSharesIncreased(state)

        MarketplaceDetailStateAction.SharesDecreased -> reduceMarketplaceDetailStateSharesDecreased(state)

        MarketplaceDetailStateAction.CartAdditionStarted -> state.copy(isAddingToCart = true, addToCartError = null)

        is MarketplaceDetailStateAction.CartAdditionFailed -> state.copy(isAddingToCart = false, addToCartError = action.message)

        is MarketplaceDetailStateAction.CartReservationReceived -> state.copy(isAddingToCart = false, confirmation = action.confirmation)

        MarketplaceDetailStateAction.ConfirmationConsumed -> state.copy(confirmation = null)

        MarketplaceDetailStateAction.SoldOutNoticeConsumed -> state.copy(soldOutNotice = null)

        MarketplaceDetailStateAction.LoadingStarted -> state.copy(isLoading = true, errorMessage = null)

        is MarketplaceDetailStateAction.DetailReceived -> MarketplaceDetailUiState(
            detail = action.detail,
            selectedShares = if (action.detail.availableShares > 0) 1 else 0,
            isLoading = false,
            favoriteLottery = state.favoriteLottery,
            specialName = action.specialName,
            soldOutNotice = action.soldOutNotice
        )

        is MarketplaceDetailStateAction.LoadingFailed -> state.copy(
            isLoading = false,
            errorMessage = action.message.takeIf {
                state.detail ==
                    null
            }
        )
    }

private fun reduceMarketplaceDetailFavoriteState(
    state: MarketplaceDetailUiState,
    action: MarketplaceDetailFavoriteAction,
): MarketplaceDetailUiState = when (action) {
    is MarketplaceDetailStateAction.FavoriteRequested -> state.copy(pendingFavorite = action.favorite, favoriteError = null)
    MarketplaceDetailStateAction.FavoriteStarted -> state.copy(pendingFavorite = null, isUpdatingFavorite = true, favoriteError = null)
    is MarketplaceDetailStateAction.FavoriteSaved -> state.copy(favoriteLottery = action.favorite, isUpdatingFavorite = false)
    is MarketplaceDetailStateAction.FavoriteFailed -> state.copy(isUpdatingFavorite = false, favoriteError = action.message)
    MarketplaceDetailStateAction.FavoriteCancelled -> state.copy(pendingFavorite = null)
    MarketplaceDetailStateAction.FavoriteErrorDismissed -> state.copy(favoriteError = null)
}

private fun reduceMarketplaceDetailStateSharesIncreased(state: MarketplaceDetailUiState): MarketplaceDetailUiState =
    if (state.canIncrease) {
        state.copy(
            selectedShares = state.selectedShares + 1
        )
    } else {
        state
    }

private fun reduceMarketplaceDetailStateSharesDecreased(state: MarketplaceDetailUiState): MarketplaceDetailUiState =
    if (state.canDecrease) {
        state.copy(
            selectedShares = state.selectedShares - 1
        )
    } else {
        state
    }
