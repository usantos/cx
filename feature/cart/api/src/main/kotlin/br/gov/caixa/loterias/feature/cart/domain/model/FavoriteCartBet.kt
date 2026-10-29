package br.gov.caixa.loterias.feature.cart.domain.model

data class FavoriteCartBet(
    val id: Long,
    val modality: String,
    val numbers: List<Int>,
    val clovers: List<Int> = emptyList(),
    val recurringDraws: Int = 0,
    val surprise: Boolean = false,
    val special: Boolean = false,
    val modalityId: Int? = null,
    val numberColumns: List<List<Int>> = emptyList(),
    val numberCount: Int = numbers.size,
    val cloverCount: Int = clovers.size,
    val favoriteTeam: String? = null,
    val luckyMonth: String? = null,
)
