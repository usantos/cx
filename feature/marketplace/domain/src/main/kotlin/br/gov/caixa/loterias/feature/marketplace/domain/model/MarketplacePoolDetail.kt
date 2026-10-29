package br.gov.caixa.loterias.feature.marketplace.domain.model

import java.math.BigDecimal

data class MarketplacePoolDetail(
    val code: String,
    val modality: String,
    val contest: Int,
    val lotteryId: Long,
    val lotteryName: String,
    val lotteryNumber: String,
    val cityAndState: String,
    val drawDateAndTime: String,
    val estimatedPrize: String,
    val availableShares: Int,
    val totalShares: Int,
    val betQuantity: Int,
    val numberQuantity: Int,
    val sharePriceWithoutFee: String,
    val lastShareFee: String,
    val lastSharePriceWithFee: String,
    val bets: List<MarketplaceBet>,
    val contestType: Int = 1,
    val drawDate: String = "",
    val serviceFeePerShare: BigDecimal = BigDecimal.ZERO,
    val lastSharePriceWithFeeValue: BigDecimal = BigDecimal.ZERO,
    val regularSharePriceWithFee: BigDecimal = lastSharePriceWithFeeValue,
)
