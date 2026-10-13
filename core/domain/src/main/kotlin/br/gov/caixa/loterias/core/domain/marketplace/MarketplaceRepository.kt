package br.gov.caixa.loterias.core.domain.marketplace

import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal

fun interface MarketplaceRepository {
    suspend fun search(filter: MarketplaceFilter, page: Int): MarketplacePage
}

data class MarketplacePage(
    val pools: List<MarketplacePool>,
    val currentPage: Int,
    val lastPage: Int,
    val totalRecords: Int,
) {
    val canLoadMore: Boolean get() = currentPage < lastPage
}

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

fun interface MarketplaceDetailRepository {
    suspend fun detail(poolCode: String): MarketplacePoolDetail
}

fun interface MarketplacePurchaseRepository {
    suspend fun addToCart(order: MarketplacePoolOrder): ShoppingCart
}

data class MarketplacePoolOrder(
    val code: String,
    val modality: String,
    val contest: Int,
    val contestType: Int,
    val drawDate: String,
    val availableShares: Int,
    val requestedShares: Int,
    val serviceFeePerShare: BigDecimal,
    val firstSharePriceWithFee: BigDecimal,
    val regularSharePriceWithFee: BigDecimal = firstSharePriceWithFee,
) {
    val totalAmount: BigDecimal
        get() = firstSharePriceWithFee + regularSharePriceWithFee.multiply((requestedShares - 1).coerceAtLeast(0).toBigDecimal())
}

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
)

data class MarketplaceBet(
    val numbers: List<Int>,
    val clovers: List<Int>,
    val surprise: Boolean,
)
