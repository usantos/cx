package br.gov.caixa.loterias.feature.betting.domain.model

data class LotecaMatchSelection(
    val match: SportsMatch,
    val homeWin: Boolean,
    val draw: Boolean,
    val awayWin: Boolean,
)
