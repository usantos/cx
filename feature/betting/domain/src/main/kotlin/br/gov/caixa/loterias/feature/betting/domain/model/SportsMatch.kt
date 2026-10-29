package br.gov.caixa.loterias.feature.betting.domain.model

data class SportsMatch(
    val number: Int,
    val home: SportsTeam,
    val away: SportsTeam,
    val legendDescription: String = "",
    val legendLetter: String = "",
)
