package br.gov.caixa.loterias.feature.retail.presentation.state

import br.gov.caixa.loterias.feature.retail.domain.model.Retailer

data class RetailSearchUiState(
    val query: String = "",
    val retailers: List<Retailer> = emptyList(),
    val loading: Boolean = false,
    val searched: Boolean = false,
    val favoriteCandidate: Retailer? = null,
    val errorMessage: String? = null,
)
