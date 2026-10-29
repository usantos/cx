package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceRapidConfigurationState(state: RapidConfigurationUiState, action: RapidConfigurationAction): RapidConfigurationUiState {
    val amounts = reduceRapidConfigurationAmounts(state, action)
    val favorites = reduceRapidConfigurationFavorites(amounts, action)
    val loading = reduceRapidConfigurationLoading(favorites, action)
    val selection = reduceRapidConfigurationSelection(loading, action)
    val notices = reduceRapidConfigurationNotices(selection, action)
    return notices
}
