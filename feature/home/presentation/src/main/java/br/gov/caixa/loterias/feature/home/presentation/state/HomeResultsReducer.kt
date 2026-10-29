package br.gov.caixa.loterias.feature.home.presentation.state

import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

internal sealed interface HomeResultsAction : HomeStateAction {
    data class Invalidated(val generation: Long) : HomeResultsAction
    data class ExpansionChanged(val page: Int?, val modality: String?) : HomeResultsAction
    data class Requested(val generation: Long, val modality: String, val page: Int?) : HomeResultsAction
    data class Received(val generation: Long, val modality: String, val page: Int?, val result: DrawResult) : HomeResultsAction
    data class Failed(val generation: Long, val modality: String, val message: String?) : HomeResultsAction
    data object ErrorDismissed : HomeResultsAction
}

internal fun reduceHomeResultsState(state: HomeUiState, action: HomeResultsAction): HomeUiState {
    val current = state.results
    val next = when (action) {
        is HomeResultsAction.Invalidated -> HomeResultsState(generation = action.generation)
        is HomeResultsAction.ExpansionChanged -> current.copy(expandedPage = action.page, expandedModality = action.modality)
        is HomeResultsAction.Requested -> requested(current, action)
        is HomeResultsAction.Received -> received(current, action)
        is HomeResultsAction.Failed -> failed(current, action)
        HomeResultsAction.ErrorDismissed -> current.copy(error = null)
    }
    return if (next == current) state else state.copy(results = next)
}

private fun requested(current: HomeResultsState, action: HomeResultsAction.Requested): HomeResultsState =
    if (action.generation != current.generation) {
        current
    } else {
        current.copy(
            loading = current.loading + action.modality,
            error = null,
            pendingPage = action.page ?: current.pendingPage,
            pendingModality = if (action.page != null) action.modality else current.pendingModality
        )
    }

private fun received(current: HomeResultsState, action: HomeResultsAction.Received): HomeResultsState =
    if (action.generation != current.generation) {
        current
    } else {
        current.copy(
            cache = current.cache + (action.modality to action.result),
            loading = current.loading - action.modality,
            expandedPage = action.page ?: current.expandedPage,
            expandedModality = if (action.page != null) action.modality else current.expandedModality,
            pendingPage = if (current.pendingModality == action.modality) null else current.pendingPage,
            pendingModality = if (current.pendingModality == action.modality) null else current.pendingModality
        )
    }

private fun failed(current: HomeResultsState, action: HomeResultsAction.Failed): HomeResultsState =
    if (action.generation != current.generation) {
        current
    } else {
        current.copy(
            loading = current.loading - action.modality,
            pendingPage = if (current.pendingModality == action.modality) null else current.pendingPage,
            pendingModality = if (current.pendingModality == action.modality) null else current.pendingModality,
            error = action.message
        )
    }
