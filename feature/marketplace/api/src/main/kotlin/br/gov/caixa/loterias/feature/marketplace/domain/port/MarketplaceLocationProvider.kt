package br.gov.caixa.loterias.feature.marketplace.domain.port

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceLocation

fun interface MarketplaceLocationProvider {
    suspend fun current(): MarketplaceLocation
}
