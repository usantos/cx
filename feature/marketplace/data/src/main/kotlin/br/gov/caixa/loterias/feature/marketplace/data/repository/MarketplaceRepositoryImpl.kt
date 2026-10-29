package br.gov.caixa.loterias.feature.marketplace.data.repository

import br.gov.caixa.loterias.feature.marketplace.data.localizedMessage as messageText

import br.gov.caixa.loterias.core.network.mapper.flattenNumbers
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea
import br.gov.caixa.loterias.feature.marketplace.domain.rule.MarketplaceFilterRules
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceRepository
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceDetailRepository
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplacePurchaseRepository
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.betting.data.dto.AddPoolShareRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.DomainCharacterDto
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceRemoteDataSource
import br.gov.caixa.loterias.feature.cart.data.repository.toDomainCart
import br.gov.caixa.loterias.feature.betting.data.mapper.toDomainLotecaSelection
import br.gov.caixa.loterias.feature.betting.data.mapper.toDomainLuckyMonth
import br.gov.caixa.loterias.feature.betting.data.mapper.toDomainSportsTeam
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePageDto

class MarketplaceRepositoryImpl(private val remote: MarketplaceRemoteDataSource) :
    MarketplaceRepository,
    MarketplaceDetailRepository,
    MarketplacePurchaseRepository {
    override suspend fun search(filter: MarketplaceFilter, page: Int): MarketplacePage {
        val area = filter.searchArea ?: MarketplaceSearchArea.NATIONAL
        val paging = linkedMapOf(
            "tipoConsulta" to area.backendValue.toString(),
            "idMunicipio" to filter.municipalityId.toString(),
            "idUf" to filter.stateId.toString(),
            "pagina" to (page.coerceAtLeast(0) + 1).toString(),
            "qtdPorPagina" to "30"
        )
        // A CX trata uma resposta bem-sucedida sem payload como página vazia,
        // permitindo que a busca por município e estado continue para fora.
        val payload = remote.pools(paging + MarketplaceFilterRules.toQuery(filter))
            ?: MarketplacePageDto(totalRecords = 0, currentPage = 1, lastPage = 0, pools = emptyList())
        val currentPage = payload.currentPage?.minus(1)?.coerceAtLeast(0) ?: page.coerceAtLeast(0)
        val terminalEmpty = payload.pools.isNullOrEmpty() && area.next() == null
        val lastPage = if (terminalEmpty) {
            currentPage
        } else {
            payload.lastPage?.minus(1)?.coerceAtLeast(0) ?: page.coerceAtLeast(0)
        }
        val nextArea = if (currentPage >= lastPage) area.next() else null
        return MarketplacePage(
            pools = payload.pools.orEmpty().map { pool ->
                val location = listOfNotNull(
                    pool.city?.name?.trim()?.takeIf(String::isNotEmpty),
                    (pool.city?.state?.abbreviation ?: pool.state?.abbreviation)
                        ?.trim()?.takeIf(String::isNotEmpty)
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
                    lastSharePriceWithFeeValue = lastSharePrice
                )
            },
            currentPage = currentPage,
            lastPage = lastPage,
            totalRecords = payload.totalRecords ?: payload.pools.orEmpty().size,
            nextArea = nextArea,
            continueAutomatically = (currentPage < lastPage || nextArea != null) &&
                payload.pools.orEmpty().size < 30
        )
    }

    override suspend fun detail(poolCode: String): MarketplacePoolDetail {
        require(poolCode.isNotBlank()) { messageText("codigo_do_bolao_obrigatorio_4b3ef2") }
        val payload = requireNotNull(remote.poolDetail(poolCode)) {
            messageText("resposta_do_detalhe_do_bolao_sem_9036e4")
        }
        val location = listOfNotNull(
            payload.city?.name?.trim()?.takeIf(String::isNotEmpty),
            payload.city?.state?.abbreviation?.trim()?.takeIf(String::isNotEmpty)
        ).joinToString(" - ")
        val draw = listOfNotNull(
            payload.drawDate?.trim()?.takeIf(String::isNotEmpty),
            payload.drawTime?.trim()?.takeIf(String::isNotEmpty)
        ).joinToString(messageText("as_ab3cd7"))
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
            sharePriceWithoutFee = (payload.lastSharePriceWithoutFee ?: payload.sharePriceWithoutFee).orEmpty(),
            lastShareFee = payload.lastShareFee.orEmpty(),
            lastSharePriceWithFee = payload.lastSharePriceWithFee.orEmpty(),
            bets = payload.bets.orEmpty().map { bet ->
                val numbers = bet.numbers
                MarketplaceBet(
                    numbers = numbers.flattenNumbers(),
                    clovers = bet.clovers.orEmpty(),
                    cloversLoaded = bet.clovers != null,
                    surprise = bet.surprise == true,
                    numberColumns = if (numbers?.isJsonArray == true && numbers.asJsonArray.any { it.isJsonArray }) {
                        numbers.asJsonArray.map { it.flattenNumbers() }
                    } else {
                        emptyList()
                    },
                    favoriteTeam = bet.favoriteTeam.toDomainSportsTeam(),
                    luckyMonth = bet.luckyMonth.toDomainLuckyMonth(),
                    lotecaSelections = bet.lotecaMatches.orEmpty().mapNotNull { it.toDomainLotecaSelection() }
                )
            },
            contestType = payload.contestType ?: 1,
            drawDate = payload.drawDate.orEmpty().trim(),
            serviceFeePerShare = payload.serviceFeePerShare ?: BigDecimal.ZERO,
            lastSharePriceWithFeeValue = payload.lastSharePriceWithFee.toMoneyValue(),
            regularSharePriceWithFee = payload.regularSharePriceWithFee ?: payload.lastSharePriceWithFee.toMoneyValue()
        )
    }

    override suspend fun addToCart(order: MarketplacePoolOrder) = remote.addPoolShare(
        AddPoolShareRequestDto(
            modality = order.modality,
            contestType = DomainCharacterDto(order.contestType.toString(), ""),
            amount = order.requestAmount,
            targetContest = order.contest,
            drawDate = order.drawDate,
            poolCode = order.code,
            shareQuantity = order.requestedShares,
            serviceFee = order.serviceFeePerShare
        )
    ).toDomainCart()

    private fun String?.toMoneyValue(): BigDecimal {
        val value = orEmpty().trim()
        return value.toBigDecimalOrNull()
            ?: value.replace(".", "").replace(',', '.').toBigDecimalOrNull()
            ?: BigDecimal.ZERO
    }
}
