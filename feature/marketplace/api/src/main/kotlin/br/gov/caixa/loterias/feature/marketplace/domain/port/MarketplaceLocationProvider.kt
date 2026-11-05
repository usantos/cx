package br.gov.caixa.loterias.feature.marketplace.domain.port

import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceLocation

fun interface MarketplaceLocationProvider {
    suspend fun current(): MarketplaceLocation
}
