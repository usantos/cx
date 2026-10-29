package br.gov.caixa.loterias.feature.betting.domain.model

data class FavoriteBet(
    val id: Long,
    val modalityId: Int,
    val modalityName: String,
    val name: String,
    val numberColumns: List<List<Int>>,
    val heartTeam: String = "",
    val luckyMonth: String = "",
    val clovers: List<Int> = emptyList(),
) {
    val numbers: List<Int> get() = numberColumns.flatten()
    val isSuperSeven: Boolean get() = numberColumns.size > 1
}
