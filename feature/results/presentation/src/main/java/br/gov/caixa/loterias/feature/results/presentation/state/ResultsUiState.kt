package br.gov.caixa.loterias.feature.results.presentation.state

import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

data class ResultsUiState(
    val modality: String? = null,
    val contestInput: String = "",
    val result: DrawResult? = null,
    val isLoading: Boolean = false,
    val inputError: String? = null,
    val errorMessage: String? = null,
    val latestContest: Int? = null,
)
