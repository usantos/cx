package br.gov.caixa.loterias.feature.marketplace.domain.repository

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter

fun interface MarketplaceRepository {
    suspend fun search(filter: MarketplaceFilter, page: Int): MarketplacePage
}
