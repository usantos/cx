package br.gov.caixa.loterias.feature.marketplace.domain.model

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
