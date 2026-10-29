package br.gov.caixa.loterias.feature.marketplace.data

import br.gov.caixa.loterias.feature.betting.data.dto.AddPoolShareRequestDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceRemoteDataSource
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePageDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePoolDetailDto
import br.gov.caixa.loterias.feature.marketplace.data.repository.MarketplaceRepositoryImpl
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrderOrigin
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MarketplacePurchaseOriginParityTest {
    @Test
    fun `detail request keeps native last share times quantity while displayed total uses regular shares`() = runTest {
        val requests = mutableListOf<AddPoolShareRequestDto>()
        val repository = repository(requests)
        val detailOrder = order().copy(origin = MarketplacePoolOrderOrigin.DETAIL)

        repository.addToCart(detailOrder)

        assertEquals(BigDecimal("28.00"), detailOrder.totalAmount)
        assertEquals(BigDecimal("30.00"), requests.single().amount)
        assertEquals("B1", requests.single().poolCode)
        assertEquals(3, requests.single().shareQuantity)
        assertEquals(BigDecimal("1.25"), requests.single().serviceFee)
    }

    @Test
    fun `list request keeps last share plus regular shares`() = runTest {
        val requests = mutableListOf<AddPoolShareRequestDto>()
        repository(requests).addToCart(order())

        assertEquals(BigDecimal("28.00"), requests.single().amount)
    }

    private fun order() = MarketplacePoolOrder(
        code = "B1", modality = "MEGA_SENA", contest = 2900, contestType = 1,
        drawDate = "10/09/2026", availableShares = 5, requestedShares = 3,
        serviceFeePerShare = BigDecimal("1.25"),
        firstSharePriceWithFee = BigDecimal("10.00"), regularSharePriceWithFee = BigDecimal("9.00"),
    )

    private fun repository(requests: MutableList<AddPoolShareRequestDto>) =
        MarketplaceRepositoryImpl(object : MarketplaceRemoteDataSource {
            override suspend fun pools(query: Map<String, String>): MarketplacePageDto? = error("Not used")
            override suspend fun poolDetail(poolCode: String): MarketplacePoolDetailDto? = error("Not used")
            override suspend fun addPoolShare(request: AddPoolShareRequestDto): CartDto {
                requests += request
                return CartDto()
            }
        })
}
