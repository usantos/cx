package br.gov.caixa.loterias.feature.betting.domain.model

import br.gov.caixa.loterias.core.domain.contract.betting.model.LuckyMonth
import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsTeam

data class FavoriteBetDraft(
    val modalityId: Int,
    val modalityName: String,
    val name: String,
    val numberColumns: List<List<Int>>,
    val clovers: List<Int> = emptyList(),
    val favoriteTeam: SportsTeam? = null,
    val luckyMonth: LuckyMonth? = null,
)
