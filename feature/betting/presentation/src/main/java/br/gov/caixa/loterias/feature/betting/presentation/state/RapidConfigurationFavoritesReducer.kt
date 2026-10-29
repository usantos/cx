package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet

internal fun reduceRapidConfigurationFavorites(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction,
): RapidConfigurationUiState = when (action) {
    RapidConfigurationAction.FavoritesLoading -> reduceRapidConfigurationFavoritesLoading(state)
    is RapidConfigurationAction.FavoritesLoaded -> reduceRapidConfigurationFavoritesLoaded(state, action)
    is RapidConfigurationAction.FavoritesFailed -> reduceRapidConfigurationFavoritesFailed(state, action)
    is RapidConfigurationAction.FavoriteToggled -> reduceRapidConfigurationFavoriteToggled(state, action)
    else -> state
}

private fun reduceRapidConfigurationFavoritesLoading(state: RapidConfigurationUiState): RapidConfigurationUiState = run {
    val it = state
    it.copy(favoritesLoading = true, favoritesError = null)
}

private fun reduceRapidConfigurationFavoritesLoaded(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.FavoritesLoaded,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(favoritesLoading = false, availableFavorites = action.favorites.filter { bet -> bet.id > 0 }.distinctBy(FavoriteBet::id))
}

private fun reduceRapidConfigurationFavoritesFailed(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.FavoritesFailed,
): RapidConfigurationUiState = run {
    val it = state
    it.copy(
        favoritesLoading = false,
        favoritesError = action.message
    )
}

private fun reduceRapidConfigurationFavoriteToggled(
    state: RapidConfigurationUiState,
    action: RapidConfigurationAction.FavoriteToggled,
): RapidConfigurationUiState = run {
    val current = state
    if (current.availableFavorites.none { it.id == action.id }) return@run current
    current.copy(
        favoriteBetIds = if (action.id in
            current.favoriteBetIds
        ) {
            current.favoriteBetIds - action.id
        } else {
            current.favoriteBetIds + action.id
        }
    )
}
