package br.gov.caixa.loterias.feature.results.presentation.state

import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure

data class ConfirmedBetResultsState(
    val results: List<DrawResult> = emptyList(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val serviceFailure: ServiceFailure? = null,
)
