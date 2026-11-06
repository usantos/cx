package br.gov.caixa.loterias.core.domain.contract.betting.model

import br.gov.caixa.loterias.core.domain.contract.betting.model.BetPrice
import br.gov.caixa.loterias.core.domain.contract.betting.model.LotteryModality
import br.gov.caixa.loterias.core.domain.contract.betting.model.LuckyMonth
import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsMatch
import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsTeam
import java.math.BigDecimal

data class LotteryGameParameters(
    val modality: LotteryModality,
    val contestNumber: Int,
    val drawDate: String,
    val maximumNumber: Int,
    val minimumSelection: Int,
    val maximumSelection: Int,
    val maximumSurprises: Int,
    val recurringOptions: List<Int>,
    val prices: List<BetPrice>,
    val minimumClovers: Int = 0,
    val maximumClovers: Int = 0,
    val maximumCloverNumber: Int = 0,
    val open: Boolean = true,
    val contestType: String = "NORMAL",
    val estimatedPrize: BigDecimal = BigDecimal.ZERO,
    val closingDateTime: String = "",
    val teams: List<SportsTeam> = emptyList(),
    val matches: List<SportsMatch> = emptyList(),
    val luckyMonths: List<LuckyMonth> = emptyList(),
    val acceptsMirror: Boolean = false,
    val hasNextContest: Boolean = false,
    val minimumBetAmount: BigDecimal? = null,
)
