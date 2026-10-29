package br.gov.caixa.loterias.feature.retail.domain.model

data class PrizeRedemptionOption(
    val channel: PrizeRedemptionChannel,
    val title: String,
    val description: String,
)
