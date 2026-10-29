package br.gov.caixa.loterias.feature.results.presentation.state

import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

sealed interface ConfirmedBetResultsAction {
    data object LoadStarted : ConfirmedBetResultsAction
    data class Loaded(val results: List<DrawResult>) : ConfirmedBetResultsAction
    data class Rejected(val failure: ServiceFailure) : ConfirmedBetResultsAction
    data class Failed(val message: String) : ConfirmedBetResultsAction
}

fun reduceConfirmedBetResultsState(ignored: ConfirmedBetResultsState, action: ConfirmedBetResultsAction): ConfirmedBetResultsState =
    when (action) {
        ConfirmedBetResultsAction.LoadStarted -> ConfirmedBetResultsState(loading = true)
        is ConfirmedBetResultsAction.Loaded -> ConfirmedBetResultsState(results = action.results)
        is ConfirmedBetResultsAction.Rejected -> ConfirmedBetResultsState(serviceFailure = action.failure)
        is ConfirmedBetResultsAction.Failed -> ConfirmedBetResultsState(errorMessage = action.message)
    }
