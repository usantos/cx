package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilterRules
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceRepository
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePage
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePool
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceBet
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceDetailRepository
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePoolDetail
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePurchaseRepository
import br.gov.caixa.loterias.core.domain.marketplace.MarketplacePoolOrder
import br.gov.caixa.loterias.core.network.AddPoolShareRequestDto
import br.gov.caixa.loterias.core.network.DomainCharacterDto
import com.google.gson.JsonElement
import java.math.BigDecimal

class RemoteMarketplaceRepository(
    private val remote: MarketplaceRemoteDataSource,
) : MarketplaceRepository, MarketplaceDetailRepository, MarketplacePurchaseRepository {
    override suspend fun search(filter: MarketplaceFilter, page: Int): MarketplacePage {
        val paging = linkedMapOf(
            "tipoConsulta" to "2",
            "idMunicipio" to "0",
            "idUf" to "0",
            "pagina" to page.coerceAtLeast(0).toString(),
            "qtdPorPagina" to "20",
        )
        val payload = requireNotNull(
            remote.pools(paging + MarketplaceFilterRules.toQuery(filter)),
        ) { "Resposta do Marketplace sem payload" }
        return MarketplacePage(
            pools = payload.pools.orEmpty().map { pool ->
                val location = listOfNotNull(
                    pool.city?.name?.trim()?.takeIf(String::isNotEmpty),
                    (pool.city?.state?.abbreviation ?: pool.state?.abbreviation)
                        ?.trim()?.takeIf(String::isNotEmpty),
                ).joinToString(" - ")
                val lastSharePrice = pool.lastSharePriceWithFee
                    ?: pool.sharePrice.toMoneyValue()
                MarketplacePool(
                    code = pool.code.orEmpty(),
                    modality = pool.modality.orEmpty(),
                    contest = pool.contest ?: 0,
                    lotteryName = pool.lotteryName.orEmpty(),
                    drawDate = pool.drawDate.orEmpty(),
                    sharePrice = (pool.lastSharePriceWithFee?.toPlainString() ?: pool.sharePrice).orEmpty(),
                    availableShares = pool.availableShares ?: 0,
                    betQuantity = pool.betQuantity ?: 0,
                    numberQuantity = pool.numberQuantity ?: 0,
                    favoriteLottery = pool.favoriteLottery == true,
                    lotteryId = pool.lotteryId ?: 0,
                    cityAndState = location,
                    totalShares = pool.totalShares ?: 0,
                    contestType = pool.contestType ?: 1,
                    estimatedPrize = pool.estimatedPrize.orEmpty(),
                    regularSharePriceWithFee = pool.regularSharePriceWithFee ?: lastSharePrice,
                    serviceFeePerShare = pool.serviceFeePerShare ?: BigDecimal.ZERO,
                    lastSharePriceWithFeeValue = lastSharePrice,
                )
            },
            currentPage = payload.currentPage ?: page.coerceAtLeast(0),
            lastPage = payload.lastPage ?: page.coerceAtLeast(0),
            totalRecords = payload.totalRecords ?: payload.pools.orEmpty().size,
        )
    }

    override suspend fun detail(poolCode: String): MarketplacePoolDetail {
        require(poolCode.isNotBlank()) { "Código do bolão obrigatório" }
        val payload = requireNotNull(remote.poolDetail(poolCode)) {
            "Resposta do detalhe do bolão sem payload"
        }
        val location = listOfNotNull(
            payload.city?.name?.trim()?.takeIf(String::isNotEmpty),
            payload.city?.state?.abbreviation?.trim()?.takeIf(String::isNotEmpty),
        ).joinToString(" - ")
        val draw = listOfNotNull(
            payload.drawDate?.trim()?.takeIf(String::isNotEmpty),
            payload.drawTime?.trim()?.takeIf(String::isNotEmpty),
        ).joinToString(" às ")
        return MarketplacePoolDetail(
            code = poolCode,
            modality = payload.modality.orEmpty(),
            contest = payload.contest ?: 0,
            lotteryId = payload.lotteryId ?: 0L,
            lotteryName = payload.lotteryName.orEmpty(),
            lotteryNumber = payload.formattedLottery?.formattedNumber.orEmpty(),
            cityAndState = location,
            drawDateAndTime = draw,
            estimatedPrize = payload.estimatedPrize.orEmpty(),
            availableShares = payload.availableShares ?: 0,
            totalShares = payload.totalShares ?: 0,
            betQuantity = payload.betQuantity ?: 0,
            numberQuantity = payload.numberQuantity ?: 0,
            sharePriceWithoutFee = payload.sharePriceWithoutFee.orEmpty(),
            lastShareFee = payload.lastShareFee.orEmpty(),
            lastSharePriceWithFee = payload.lastSharePriceWithFee.orEmpty(),
            bets = payload.bets.orEmpty().map { bet ->
                MarketplaceBet(
                    numbers = bet.numbers.flattenNumbers(),
                    clovers = bet.clovers.orEmpty(),
                    surprise = bet.surprise == true,
                )
            },
            contestType = payload.contestType ?: 1,
            drawDate = payload.drawDate.orEmpty().trim(),
            serviceFeePerShare = payload.serviceFeePerShare ?: BigDecimal.ZERO,
            lastSharePriceWithFeeValue = payload.lastSharePriceWithFee.toMoneyValue(),
        )
    }

    override suspend fun addToCart(order: MarketplacePoolOrder) = remote.addPoolShare(
        AddPoolShareRequestDto(
            modality = order.modality,
            contestType = DomainCharacterDto(order.contestType.toString(), ""),
            amount = order.totalAmount,
            targetContest = order.contest,
            drawDate = order.drawDate,
            poolCode = order.code,
            shareQuantity = order.requestedShares,
            serviceFee = order.serviceFeePerShare,
        ),
    ).toDomainCart()

    private fun JsonElement?.flattenNumbers(): List<Int> = when {
        this == null || isJsonNull -> emptyList()
        isJsonArray -> asJsonArray.flatMap { it.flattenNumbers() }
        isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(asInt)
        else -> emptyList()
    }

    private fun String?.toMoneyValue(): BigDecimal {
        val value = orEmpty().trim()
        return value.toBigDecimalOrNull()
            ?: value.replace(".", "").replace(',', '.').toBigDecimalOrNull()
            ?: BigDecimal.ZERO
    }
}
