package br.gov.caixa.loterias.core.domain.contract.betting.model

import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsTeam

data class SportsMatch(
    val number: Int,
    val home: SportsTeam,
    val away: SportsTeam,
    val legendDescription: String = "",
    val legendLetter: String = "",
)
