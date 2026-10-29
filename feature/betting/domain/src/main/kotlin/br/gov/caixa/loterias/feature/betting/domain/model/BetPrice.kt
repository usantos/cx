package br.gov.caixa.loterias.feature.betting.domain.model

import java.math.BigDecimal

data class BetPrice(
    val numberQuantity: Int,
    val cloverQuantity: Int,
    val amount: BigDecimal,
    val doubleQuantity: Int = 0,
    val tripleQuantity: Int = 0,
)
