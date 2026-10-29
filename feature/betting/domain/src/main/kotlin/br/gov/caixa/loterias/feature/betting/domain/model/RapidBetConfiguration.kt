package br.gov.caixa.loterias.feature.betting.domain.model

import java.math.BigDecimal

data class RapidBetConfiguration(
    val minimumAmount: BigDecimal,
    val maximumAmount: BigDecimal,
    val minimumPrize: BigDecimal,
    val requiredNumbers: Set<Int>,
    val excludedNumbers: Set<Int>,
    val modalities: List<LotteryModality>,
    val favoriteBetIds: Set<Long> = emptySet(),
)
