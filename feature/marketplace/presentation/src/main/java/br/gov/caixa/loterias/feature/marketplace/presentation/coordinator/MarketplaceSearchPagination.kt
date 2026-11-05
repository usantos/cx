package br.gov.caixa.loterias.feature.marketplace.presentation.coordinator

import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceSearchArea
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState

internal class MarketplaceSearchPagination(private val filter: MarketplaceFilter) {
    var searchFilter = filter
        private set
    var nextArea: MarketplaceSearchArea? = null

    fun reset() {
        searchFilter = filter
        nextArea = null
    }

    fun nextPage(snapshot: MarketplaceUiState): Int? {
        if (snapshot.isLoading || snapshot.isLoadingMore || !snapshot.canLoadMore) return null
        val followingArea = nextArea
        if (followingArea != null) searchFilter = searchFilter.withSearchArea(followingArea)
        return if (followingArea != null) 0 else snapshot.currentPage + 1
    }
}
