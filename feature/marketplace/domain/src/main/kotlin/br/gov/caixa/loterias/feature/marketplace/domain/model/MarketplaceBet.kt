package br.gov.caixa.loterias.feature.marketplace.domain.model

data class MarketplaceBet(
    val numbers: List<Int>,
    val clovers: List<Int>,
    val surprise: Boolean,
    val numberColumns: List<List<Int>> = emptyList(),
    val favoriteTeam: br.gov.caixa.loterias.core.domain.contract.betting.model.SportsTeam? = null,
    val luckyMonth: br.gov.caixa.loterias.core.domain.contract.betting.model.LuckyMonth? = null,
    val lotecaSelections: List<br.gov.caixa.loterias.core.domain.contract.betting.model.LotecaMatchSelection> = emptyList(),
    val cloversLoaded: Boolean = true,
)
