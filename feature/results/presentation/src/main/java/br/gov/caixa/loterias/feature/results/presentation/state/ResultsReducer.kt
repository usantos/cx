package br.gov.caixa.loterias.feature.results.presentation.state

import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

sealed interface ResultsAction {
    data object SelectionCleared : ResultsAction
    data class ModalitySelected(val modality: String) : ResultsAction
    data class ContestChanged(val value: String) : ResultsAction
    data object InputErrorDismissed : ResultsAction
    data class InputRejected(val message: String) : ResultsAction
    data class ContestSelected(val modality: String, val contest: Int) : ResultsAction
    data class AdjacentContestSelected(val contest: Int) : ResultsAction
    data object LoadStarted : ResultsAction
    data class Loaded(val result: DrawResult, val latest: Boolean) : ResultsAction
    data object HistoricalLoadFailed : ResultsAction
    data class LoadFailed(val message: String) : ResultsAction
}

fun reduceResultsState(state: ResultsUiState, action: ResultsAction): ResultsUiState = when (action) {
    ResultsAction.SelectionCleared -> ResultsUiState()

    is ResultsAction.ModalitySelected -> ResultsUiState(modality = action.modality)

    is ResultsAction.ContestChanged -> state.copy(contestInput = action.value.filter(Char::isDigit), inputError = null)

    ResultsAction.InputErrorDismissed -> state.copy(inputError = null)

    is ResultsAction.InputRejected -> state.copy(inputError = action.message)

    is ResultsAction.ContestSelected -> state.copy(modality = action.modality, contestInput = action.contest.toString(), inputError = null)

    is ResultsAction.AdjacentContestSelected -> state.copy(contestInput = action.contest.toString(), inputError = null)

    ResultsAction.LoadStarted -> state.copy(isLoading = true, errorMessage = null)

    is ResultsAction.Loaded -> state.copy(
        result = action.result,
        contestInput = action.result.contest.toString(),
        latestContest = if (action.latest) action.result.contest else state.latestContest,
        isLoading = false,
        inputError = null
    )

    ResultsAction.HistoricalLoadFailed -> state.copy(
        result = null,
        contestInput = "",
        latestContest = null,
        isLoading = false,
        errorMessage = null
    )

    is ResultsAction.LoadFailed -> state.copy(isLoading = false, errorMessage = action.message)
}

sealed interface HomeResultsAction {
    data class RefreshStarted(val modality: String) : HomeResultsAction
    data class Loaded(val modality: String, val result: DrawResult) : HomeResultsAction
}

fun reduceHomeResultsState(state: Map<String, DrawResult>, action: HomeResultsAction): Map<String, DrawResult> = when (action) {
    is HomeResultsAction.RefreshStarted -> state - action.modality
    is HomeResultsAction.Loaded -> state + (action.modality to action.result)
}

sealed interface HomeResultsLoadingAction {
    data object LoadStarted : HomeResultsLoadingAction
    data class JobsChanged(val active: Boolean) : HomeResultsLoadingAction
}

fun reduceHomeResultsLoadingState(ignored: Boolean, action: HomeResultsLoadingAction): Boolean = when (action) {
    HomeResultsLoadingAction.LoadStarted -> true
    is HomeResultsLoadingAction.JobsChanged -> action.active
}
