package br.gov.caixa.loterias.feature.tickets.presentation.screen

import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth

data class PurchasesListActions(
    val openPurchaseFilters: () -> Unit,
    val selectPurchaseMonth: (PurchaseMonth) -> Unit,
    val retryPurchases: () -> Unit,
    val loadMorePurchases: () -> Unit,
)
