package br.gov.caixa.loterias.feature.marketplace.domain.model

data class MarketplaceBet(
    val numbers: List<Int>,
    val clovers: List<Int>,
    val surprise: Boolean,
    val numberColumns: List<List<Int>> = emptyList(),
    val favoriteTeam: br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam? = null,
    val luckyMonth: br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth? = null,
    val lotecaSelections: List<br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection> = emptyList(),
    val cloversLoaded: Boolean = true,
)
