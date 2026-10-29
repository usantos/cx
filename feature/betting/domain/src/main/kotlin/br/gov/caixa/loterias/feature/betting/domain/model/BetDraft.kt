package br.gov.caixa.loterias.feature.betting.domain.model

data class BetDraft(
    val modalityId: Int,
    val contestType: String = "NORMAL",
    val numbers: List<Int>,
    val clovers: List<Int> = emptyList(),
    val recurringDraws: Int = 0,
    val surprise: Boolean = false,
    val quantity: Int = 1,
    val numberQuantity: Int = numbers.size,
    val favoriteTeam: SportsTeam? = null,
    val lotecaSelections: List<LotecaMatchSelection> = emptyList(),
    val lotogolSelections: List<LotogolMatchSelection> = emptyList(),
    val superSevenColumns: List<List<Int>> = emptyList(),
    val luckyMonth: LuckyMonth? = null,
    val generateMirror: Boolean = false,
    val cloverQuantity: Int = clovers.size,
    val contestNumber: Int? = null,
    val allowLocalDuplicate: Boolean = false,
    val amount: java.math.BigDecimal? = null,
    val surpriseTeamSelected: Boolean = false,
)
