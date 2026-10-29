package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam

internal data class GeneratedBettingSelection(
    val numbers: Set<Int>,
    val quantity: Int,
    val columns: List<Set<Int>>,
    val outcomes: Map<Int, Set<LotecaOutcome>>,
    val team: SportsTeam?,
    val teamQuery: String,
    val month: LuckyMonth?,
    val clovers: Set<Int>,
    val mirror: Boolean,
    val warning: String?,
    val error: String?,
)
