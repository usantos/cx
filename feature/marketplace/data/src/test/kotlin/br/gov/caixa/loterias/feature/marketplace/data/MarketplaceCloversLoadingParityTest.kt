package br.gov.caixa.loterias.feature.marketplace.data

import br.gov.caixa.loterias.feature.betting.data.dto.AddPoolShareRequestDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceRemoteDataSource
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePageDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePoolDetailDto
import br.gov.caixa.loterias.feature.marketplace.data.repository.MarketplaceRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketplaceCloversLoadingParityTest {
    @Test fun `null clovers remain distinguishable from loaded empty clovers after detail mapping`() = runTest {
        val payload = Gson().fromJson(
            """{"modalidade":"MAIS_MILIONARIA","apostas":[{"trevos":null},{"trevos":[]},{"trevos":[1,6]}]}""",
            MarketplacePoolDetailDto::class.java,
        )
        val repository = MarketplaceRepositoryImpl(object : MarketplaceRemoteDataSource {
            override suspend fun pools(query: Map<String, String>): MarketplacePageDto? = error("Not used")
            override suspend fun poolDetail(poolCode: String): MarketplacePoolDetailDto = payload
            override suspend fun addPoolShare(request: AddPoolShareRequestDto): CartDto? = error("Mutation forbidden")
        })

        val bets = repository.detail("B1").bets

        assertFalse(bets[0].cloversLoaded)
        assertTrue(bets[0].clovers.isEmpty())
        assertTrue(bets[1].cloversLoaded)
        assertTrue(bets[1].clovers.isEmpty())
        assertTrue(bets[2].cloversLoaded)
        assertEquals(listOf(1, 6), bets[2].clovers)
    }
}
