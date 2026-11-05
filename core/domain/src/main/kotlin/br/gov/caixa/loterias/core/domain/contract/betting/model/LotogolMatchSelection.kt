package br.gov.caixa.loterias.core.domain.contract.betting.model

import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsMatch

data class LotogolMatchSelection(
    val match: SportsMatch,
    val homeScore: Int,
    val awayScore: Int,
)
