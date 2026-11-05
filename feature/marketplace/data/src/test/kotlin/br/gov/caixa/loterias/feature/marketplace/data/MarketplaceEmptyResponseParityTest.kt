package br.gov.caixa.loterias.feature.marketplace.data

import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceSearchArea
import br.gov.caixa.loterias.core.network.dto.betting.AddPoolShareRequestDto
import br.gov.caixa.loterias.core.network.dto.cart.CartDto
import br.gov.caixa.loterias.core.network.dto.marketplace.MarketplacePageDto
import br.gov.caixa.loterias.core.network.dto.marketplace.MarketplacePoolDetailDto
import br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceRemoteDataSource
import br.gov.caixa.loterias.feature.marketplace.data.repository.MarketplaceRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketplaceEmptyResponseParityTest {
    @Test
    fun `successful null municipal payload advances to state like CX empty response`() = runTest {
        val page = repository().search(
            MarketplaceFilter.empty().withSearchArea(MarketplaceSearchArea.MUNICIPALITY), 0,
        )

        assertTrue(page.pools.isEmpty())
        assertEquals(0, page.totalRecords)
        assertEquals(MarketplaceSearchArea.STATE, page.nextArea)
        assertTrue(page.continueAutomatically)
        assertTrue(page.canLoadMore)
    }

    @Test
    fun `successful null national payload exhausts the search like CX empty response`() = runTest {
        val page = repository().search(
            MarketplaceFilter.empty().withSearchArea(MarketplaceSearchArea.NATIONAL), 0,
        )

        assertTrue(page.pools.isEmpty())
        assertEquals(0, page.totalRecords)
        assertFalse(page.continueAutomatically)
        assertFalse(page.canLoadMore)
    }

    private fun repository() = MarketplaceRepositoryImpl(object : MarketplaceRemoteDataSource {
        override suspend fun pools(query: Map<String, String>): MarketplacePageDto? = null
        override suspend fun poolDetail(poolCode: String): MarketplacePoolDetailDto? = error("Not used")
        override suspend fun addPoolShare(request: AddPoolShareRequestDto): CartDto? = error("Not used")
    })
}
