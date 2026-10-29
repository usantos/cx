package br.gov.caixa.loterias.feature.betting.presentation.state

internal fun reduceRapidListState(state: RapidListUiState, action: RapidListAction): RapidListUiState {
    val cart = reduceRapidListCart(state, action)
    val notices = reduceRapidListNotices(cart, action)
    return notices
}
