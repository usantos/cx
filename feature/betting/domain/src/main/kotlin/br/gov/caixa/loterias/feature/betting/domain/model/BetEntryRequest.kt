package br.gov.caixa.loterias.feature.betting.domain.model

data class BetEntryRequest(
    val modalityId: Int,
    val contestType: String? = null,
    val contestNumber: Int? = null,
)
