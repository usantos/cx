package br.gov.caixa.loterias.feature.retail.presentation.screen

data class RetailSearchActions(
    val changeQuery: (String) -> Unit,
    val search: () -> Unit,
    val toggleFavorite: (br.gov.caixa.loterias.core.domain.contract.retail.model.Retailer) -> Unit,
    val confirmFavorite: () -> Unit,
    val cancelFavorite: () -> Unit,
    val dismissError: () -> Unit,
)
