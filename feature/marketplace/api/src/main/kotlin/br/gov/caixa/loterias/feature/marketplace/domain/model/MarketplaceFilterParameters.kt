package br.gov.caixa.loterias.feature.marketplace.domain.model

import java.util.Objects
import br.gov.caixa.loterias.feature.marketplace.domain.rule.MarketplaceFilterRules

/** Limites retornados por /parametros/configuraveis-app; sem faixa alternativa inventada. */
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
