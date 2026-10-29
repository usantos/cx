package br.gov.caixa.loterias.feature.marketplace.domain.model

import java.math.BigDecimal

data class MarketplacePool(
    val code: String,
    val modality: String,
    val contest: Int,
    val lotteryName: String,
    val drawDate: String,
    val sharePrice: String,
    val availableShares: Int,
    val betQuantity: Int,
    val numberQuantity: Int,
    val favoriteLottery: Boolean,
    val lotteryId: Long = 0,
    val cityAndState: String = "",
    val totalShares: Int = 0,
    val contestType: Int = 1,
    val estimatedPrize: String = "",
    val regularSharePriceWithFee: BigDecimal = BigDecimal.ZERO,
    val serviceFeePerShare: BigDecimal = BigDecimal.ZERO,
    val lastSharePriceWithFeeValue: BigDecimal = BigDecimal.ZERO,
)
