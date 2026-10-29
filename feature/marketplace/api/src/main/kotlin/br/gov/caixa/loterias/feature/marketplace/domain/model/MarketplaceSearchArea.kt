package br.gov.caixa.loterias.feature.marketplace.domain.model

/** A CX esgota cada área antes de avançar para fora; as páginas do servidor começam em um em cada área. */
enum class MarketplaceSearchArea(val backendValue: Int) {
    MUNICIPALITY(0), STATE(1), NATIONAL(2), RETAILER(3);

    fun next(): MarketplaceSearchArea? = when (this) {
        MUNICIPALITY -> STATE
        STATE -> NATIONAL
        NATIONAL, RETAILER -> null
    }
}
