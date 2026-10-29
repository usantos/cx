package br.gov.caixa.loterias.feature.betting.domain.model

data class FavoriteBetCartOptions(
    val contestType: Int = 1,
    val recurringDraws: Int = 0,
    val mirror: Boolean? = false,
)
