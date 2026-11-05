package br.gov.caixa.loterias.feature.marketplace.data

import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceLocation
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
import org.junit.Test

class MarketplaceRetailerAreaParityTest {
    @Test
    fun `retailer query keeps native municipality state national areas and starts each at wire page one`() = runTest {
        val requests = mutableListOf<Map<String, String>>()
        val repository = MarketplaceRepositoryImpl(object : MarketplaceRemoteDataSource {
            override suspend fun pools(query: Map<String, String>): MarketplacePageDto? {
                requests += query
                return null
            }
            override suspend fun poolDetail(poolCode: String): MarketplacePoolDetailDto? = error("Not used")
            override suspend fun addPoolShare(request: AddPoolShareRequestDto): CartDto? = error("Mutation forbidden")
        })
        val filter = MarketplaceFilter.empty().withLottery("987")
            .withSearchLocation(MarketplaceLocation(123, 53))

        val municipal = repository.search(filter.withSearchArea(MarketplaceSearchArea.MUNICIPALITY), 0)
        val state = repository.search(filter.withSearchArea(MarketplaceSearchArea.STATE), 0)
        val national = repository.search(filter.withSearchArea(MarketplaceSearchArea.NATIONAL), 0)

        assertEquals(listOf("0", "1", "2"), requests.map { it["tipoConsulta"] })
        assertEquals(listOf("1", "1", "1"), requests.map { it["pagina"] })
        assertEquals(listOf("987", "987", "987"), requests.map { it["numeroLoterico"] })
        assertEquals(listOf("123", "123", "123"), requests.map { it["idMunicipio"] })
        assertEquals(listOf("53", "53", "53"), requests.map { it["idUf"] })
        assertEquals(listOf("30", "30", "30"), requests.map { it["qtdPorPagina"] })
        assertEquals(MarketplaceSearchArea.STATE, municipal.nextArea)
        assertEquals(MarketplaceSearchArea.NATIONAL, state.nextArea)
        assertFalse(national.canLoadMore)
    }
}
