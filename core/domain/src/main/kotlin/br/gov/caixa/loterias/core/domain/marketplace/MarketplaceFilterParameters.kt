package br.gov.caixa.loterias.core.domain.marketplace

import java.util.Objects

/** Limits returned by /parametros/configuraveis-app; no invented fallback range. */
class MarketplaceFilterParameters(minimumBetQuantity: Int?, maximumBetQuantity: Int?) {
    val minimumBetQuantity = minimumBetQuantity ?: 0
    val maximumBetQuantity = maximumBetQuantity ?: 0
    val betOptions: List<Int>
        get() = MarketplaceFilterRules.inclusiveRange(minimumBetQuantity, maximumBetQuantity)
    val isValid: Boolean
        get() = betOptions.isNotEmpty()

    override fun equals(other: Any?): Boolean = this === other || other is MarketplaceFilterParameters &&
        minimumBetQuantity == other.minimumBetQuantity && maximumBetQuantity == other.maximumBetQuantity

    override fun hashCode(): Int = Objects.hash(minimumBetQuantity, maximumBetQuantity)
}
