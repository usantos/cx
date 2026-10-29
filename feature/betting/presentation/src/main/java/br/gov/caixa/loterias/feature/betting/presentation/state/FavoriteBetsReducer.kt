package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceFavoriteBetsState(state: FavoriteBetsUiState, action: FavoriteBetsAction): FavoriteBetsUiState {
    val loading = reduceFavoriteBetsLoading(state, action)
    val selection = reduceFavoriteBetsSelection(loading, action)
    val deletion = reduceFavoriteBetsDeletion(selection, action)
    val cart = reduceFavoriteBetsCart(deletion, action)
    return cart
}
