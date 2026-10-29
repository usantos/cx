package br.gov.caixa.loterias.feature.betting.domain.model

data class LotogolMatchSelection(
    val match: SportsMatch,
    val homeScore: Int,
    val awayScore: Int,
)
