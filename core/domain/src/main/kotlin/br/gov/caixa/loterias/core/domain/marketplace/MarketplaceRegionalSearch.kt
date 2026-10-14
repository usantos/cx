package br.gov.caixa.loterias.core.domain.marketplace

/** CX exhausts each area before moving outward; backend pages start at one in every area. */
enum class MarketplaceSearchArea(val backendValue: Int) {
    MUNICIPALITY(0), STATE(1), NATIONAL(2), RETAILER(3);

    fun next(): MarketplaceSearchArea? = when (this) {
        MUNICIPALITY -> STATE
        STATE -> NATIONAL
        NATIONAL, RETAILER -> null
    }
}

data class MarketplaceLocation(val municipalityId: Long, val stateId: Long)

fun interface MarketplaceLocationProvider {
    suspend fun current(): MarketplaceLocation
}
