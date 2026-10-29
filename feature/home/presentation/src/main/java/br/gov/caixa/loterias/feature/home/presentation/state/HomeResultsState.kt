package br.gov.caixa.loterias.feature.home.presentation.state

import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

data class HomeResultsState(
    val generation: Long = 0,
    val cache: Map<String, DrawResult> = emptyMap(),
    val pendingPage: Int? = null,
    val pendingModality: String? = null,
    val expandedPage: Int? = null,
    val expandedModality: String? = null,
    val loading: Set<String> = emptySet(),
    val error: String? = null,
)
