package br.gov.caixa.loterias.feature.marketplace.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceSearchArea
import br.gov.caixa.loterias.feature.marketplace.domain.port.MarketplaceLocationProvider
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceRepository

class SearchMarketplace(
    private val repository: MarketplaceRepository,
    private val location: MarketplaceLocationProvider? = null,
) : SearchMarketplaceUseCase {
    override suspend fun invoke(filter: MarketplaceFilter, page: Int) = appResultOf {
        val locatedFilter = location?.current()?.let(filter::withSearchLocation) ?: filter
        val area = when {
            filter.searchArea != null -> filter.searchArea
            location != null -> MarketplaceSearchArea.MUNICIPALITY
            else -> MarketplaceSearchArea.NATIONAL
        }
        repository.search(locatedFilter.withSearchArea(area), page)
    }
}
