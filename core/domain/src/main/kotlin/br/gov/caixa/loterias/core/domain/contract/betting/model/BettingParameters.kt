package br.gov.caixa.loterias.core.domain.contract.betting.model

import br.gov.caixa.loterias.core.domain.contract.betting.model.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.contract.betting.model.PoolModalityAvailability
import java.math.BigDecimal

data class BettingParameters(
    val dailyLimit: BigDecimal,
    val minimumCartAmount: BigDecimal,
    val maximumCartBets: Int,
    val games: List<LotteryGameParameters>,
    val availablePoolModalities: List<PoolModalityAvailability> = emptyList(),
)
