package br.gov.caixa.loterias.feature.retail.presentation.state

import br.gov.caixa.loterias.feature.retail.domain.model.Retailer

internal sealed interface RetailSearchStateAction {
    data class QueryChanged(val query: String) : RetailSearchStateAction
    data class SearchRejected(val message: String) : RetailSearchStateAction
    data class FavoriteRequested(val retailer: Retailer) : RetailSearchStateAction
    data object FavoriteCancelled : RetailSearchStateAction
    data object FavoriteStarted : RetailSearchStateAction
    data class FavoriteSaved(val retailer: Retailer) : RetailSearchStateAction
    data class FavoriteFailed(val message: String) : RetailSearchStateAction
    data object ErrorDismissed : RetailSearchStateAction
    data object SearchStarted : RetailSearchStateAction
    data class SearchReceived(val query: String, val retailers: List<Retailer>) : RetailSearchStateAction
    data class SearchFailed(val query: String, val message: String) : RetailSearchStateAction
}

internal fun reduceRetailSearchState(state: RetailSearchUiState, action: RetailSearchStateAction): RetailSearchUiState = when (action) {
    is RetailSearchStateAction.QueryChanged -> reduceRetailSearchStateQueryChanged(state, action)

    is RetailSearchStateAction.SearchRejected -> state.copy(errorMessage = action.message)

    is RetailSearchStateAction.FavoriteRequested -> state.copy(favoriteCandidate = action.retailer)

    RetailSearchStateAction.FavoriteCancelled -> state.copy(favoriteCandidate = null)

    RetailSearchStateAction.FavoriteStarted -> state.copy(loading = true, favoriteCandidate = null, errorMessage = null)

    is RetailSearchStateAction.FavoriteSaved -> state.copy(
        retailers = state.retailers.map { if (it.id == action.retailer.id) it.copy(favorite = !action.retailer.favorite) else it },
        loading = false
    )

    is RetailSearchStateAction.FavoriteFailed -> state.copy(loading = false, errorMessage = action.message)

    RetailSearchStateAction.ErrorDismissed -> state.copy(errorMessage = null)

    RetailSearchStateAction.SearchStarted -> state.copy(loading = true, errorMessage = null)

    is RetailSearchStateAction.SearchReceived -> reduceRetailSearchStateSearchReceived(state, action)

    is RetailSearchStateAction.SearchFailed -> reduceRetailSearchStateSearchFailed(state, action)
}

private fun reduceRetailSearchStateQueryChanged(
    state: RetailSearchUiState,
    action: RetailSearchStateAction.QueryChanged,
): RetailSearchUiState = if (action.query.trim().length < RETAIL_QUERY_MIN_LENGTH) {
    RetailSearchUiState(query = action.query)
} else {
    state.copy(query = action.query, errorMessage = null)
}

private const val RETAIL_QUERY_MIN_LENGTH = 3

private fun reduceRetailSearchStateSearchReceived(
    state: RetailSearchUiState,
    action: RetailSearchStateAction.SearchReceived,
): RetailSearchUiState = if (state.query != action.query) {
    state
} else {
    state.copy(retailers = action.retailers, loading = false, searched = true)
}

private fun reduceRetailSearchStateSearchFailed(
    state: RetailSearchUiState,
    action: RetailSearchStateAction.SearchFailed,
): RetailSearchUiState = if (state.query != action.query) {
    state
} else {
    state.copy(retailers = emptyList(), loading = false, searched = true, errorMessage = action.message)
}
