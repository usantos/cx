package br.gov.caixa.loterias.feature.marketplace.domain.repository

import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage

fun interface MarketplaceRepository {
    suspend fun search(filter: MarketplaceFilter, page: Int): MarketplacePage
}
