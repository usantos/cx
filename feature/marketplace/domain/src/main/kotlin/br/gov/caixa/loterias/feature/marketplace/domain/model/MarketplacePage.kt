package br.gov.caixa.loterias.feature.marketplace.domain.model

import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceSearchArea

data class MarketplacePage(
    val pools: List<MarketplacePool>,
    val currentPage: Int,
    val lastPage: Int,
    val totalRecords: Int,
    val nextArea: MarketplaceSearchArea? = null,
    val continueAutomatically: Boolean = false,
) {
    val canLoadMore: Boolean get() = currentPage < lastPage || nextArea != null
}
