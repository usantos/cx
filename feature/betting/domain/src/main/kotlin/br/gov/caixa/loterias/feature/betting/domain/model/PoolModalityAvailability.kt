package br.gov.caixa.loterias.feature.betting.domain.model

data class PoolModalityAvailability(
    val modalityId: Int,
    val modalityCode: String,
    val contestType: String,
    val contestNumber: Int? = null,
    val resaleClosingDate: String = "",
    val resaleClosingTime: String = "",
    val modality: LotteryModality? = null,
    val estimatedPrize: java.math.BigDecimal? = null,
    val drawDateTime: String = "",
)
