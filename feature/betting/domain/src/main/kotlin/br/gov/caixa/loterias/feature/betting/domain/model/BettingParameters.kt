package br.gov.caixa.loterias.feature.betting.domain.model

import java.math.BigDecimal

data class BettingParameters(
    val dailyLimit: BigDecimal,
    val minimumCartAmount: BigDecimal,
    val maximumCartBets: Int,
    val games: List<LotteryGameParameters>,
    val availablePoolModalities: List<PoolModalityAvailability> = emptyList(),
)
