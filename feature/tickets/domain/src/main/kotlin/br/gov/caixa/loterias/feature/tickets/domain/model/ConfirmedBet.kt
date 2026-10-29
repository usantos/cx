package br.gov.caixa.loterias.feature.tickets.domain.model

import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import java.math.BigDecimal

data class ConfirmedBet(
    val id: Long,
    val modality: String,
    val contest: Int,
    val date: String,
    val status: String,
    val numbers: List<Int>,
    val prize: BigDecimal = BigDecimal.ZERO,
    val originalRecurringBet: Boolean = false,
    val redeemedPrize: Boolean = false,
    val exchangeBetId: Long? = null,
    val modalityId: Int = 0,
    val statusId: Long = 0,
    val contestTypeId: Long = 0,
    val poolShare: Boolean = false,
    val surprise: Boolean = false,
    val time: String = "",
    val numberColumns: List<List<Int>> = if (numbers.isEmpty()) emptyList() else listOf(numbers),
    val clovers: List<Int> = emptyList(),
    val favoriteTeam: SportsTeam? = null,
    val luckyMonth: LuckyMonth? = null,
    val lotecaSelections: List<LotecaMatchSelection> = emptyList(),
    val lotogolSelections: List<LotogolMatchSelection> = emptyList(),
    val recurringDraws: Int = 0,
    val quantity: Int = 1,
    val mirror: Boolean = false,
    val exchange: Boolean = false,
    val combo: Boolean = false,
    val receipt: ConfirmedBetReceipt? = null,
    val backend: ConfirmedBetsBackend = ConfirmedBetsBackend.SILCE,
)
