// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.repository.AccountMunicipalityRepository
import br.gov.caixa.loterias.feature.betting.data.dto.AddPoolShareRequestDto
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceRemoteDataSource
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePageDto
import br.gov.caixa.loterias.feature.marketplace.data.dto.MarketplacePoolDetailDto
import br.gov.caixa.loterias.feature.marketplace.di.MarketplaceModule
import br.gov.caixa.loterias.feature.marketplace.di.RemoteAdapterModule
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceRepository
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class MarketplaceModuleContractTest {
    @Test fun `special contest provider preserves values maps failure and propagates cancellation`() = runBlocking {
        var failure: Exception? = null
        val configuration = object : AppConfigurationRepository {
            override suspend fun isVersionSupported(versionName: String) = true
            override suspend fun marketplaceFilterParameters() = MarketplaceFilterParameters(1, 10)
            override suspend fun homeFeatures(): HomeFeatureConfiguration {
                failure?.let { throw it }
                return HomeFeatureConfiguration(specialContests = HomeSpecialContests(3000, 1234))
            }
        }
        val contests = MarketplaceModule.marketplaceSpecialContests(configuration)
        assertEquals(AppResult.Success(MarketplaceSpecialContests(3000, 1234)), contests())
        failure = IllegalStateException("Synthetic unavailable configuration")
        assertEquals(AppResult.Failure(AppError.Unexpected), contests())
        val cancellation = CancellationException("cancelled caller")
        failure = cancellation
        try {
            contests()
            fail("Cancellation must propagate")
        } catch (actual: CancellationException) {
            assertSame(cancellation, actual)
        }
    }

    @Test fun `search wiring binds account municipality to captured identity and rejects late account replacement`() = runBlocking {
        val identity = AuthenticatedSessionIdentity("a".repeat(64))
        var current = true
        var loggedIn = true
        var reads = 0
        val searched = mutableListOf<Pair<MarketplaceFilter, Int>>()
        var replaceDuringProfile = false
        val account = AccountMunicipalityRepository {
            reads++
            if (replaceDuringProfile) current = false
            MunicipalityId(7, 123, 0)
        }
        val sessions = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = identity.takeIf { loggedIn }
            override fun isCurrent(candidate: AuthenticatedSessionIdentity) = current && candidate === identity
        }
        val repository = MarketplaceRepository { filter, page ->
            searched += filter to page
            MarketplacePage(emptyList(), page, page, 0)
        }
        val search = MarketplaceModule.searchMarketplace(repository, account, sessions)
        assertTrue(search(MarketplaceFilter.empty(), 2) is AppResult.Success)
        assertEquals(123L, searched.single().first.municipalityId)
        assertEquals(7L, searched.single().first.stateId)
        assertEquals(MarketplaceSearchArea.MUNICIPALITY, searched.single().first.searchArea)
        assertEquals(2, searched.single().second)
        replaceDuringProfile = true
        assertTrue(search(MarketplaceFilter.empty(), 3) is AppResult.Failure)
        assertEquals(1, searched.size)
        loggedIn = false
        assertTrue(search(MarketplaceFilter.empty(), 4) is AppResult.Failure)
        assertEquals(2, reads)
        assertEquals(1, searched.size)
    }

    @Test fun `repository aliases preserve one transport and reservation validation precedes synthetic request`() = runBlocking {
        val concrete = RemoteAdapterModule.remoteAdapter(LoteriasApiProvider())
        assertSame(concrete, MarketplaceModule.marketplaceDataSource(concrete))
        val requests = mutableListOf<AddPoolShareRequestDto>()
        val queries = mutableListOf<Map<String, String>>()
        val source = object : MarketplaceRemoteDataSource {
            override suspend fun pools(query: Map<String, String>): MarketplacePageDto? {
                queries += query
                return null
            }
            override suspend fun poolDetail(poolCode: String): MarketplacePoolDetailDto? = null
            override suspend fun addPoolShare(request: AddPoolShareRequestDto): CartDto {
                requests += request
                return CartDto(id = 41, total = BigDecimal.TEN)
            }
        }
        val repository = MarketplaceModule.marketplaceRepository(source)
        assertSame(repository, MarketplaceModule.marketplaceContract(repository))
        assertSame(repository, MarketplaceModule.marketplaceDetailContract(repository))
        assertSame(repository, MarketplaceModule.marketplacePurchaseContract(repository))
        val page = MarketplaceModule.marketplaceContract(repository).search(MarketplaceFilter.empty(), 0)
        assertTrue(page.pools.isEmpty())
        assertEquals("1", queries.single()["pagina"])
        assertTrue(MarketplaceModule.marketplaceDetail(repository)("") is AppResult.Failure)
        val add = MarketplaceModule.addMarketplacePoolToCart(repository)
        val order = MarketplacePoolOrder("SYNTHETIC", "Mega-Sena", 3000, 1, "22/10/2026", 2, 1, BigDecimal.ONE, BigDecimal.TEN)
        assertTrue(add(order.copy(requestedShares = 3)) is AppResult.Failure)
        assertTrue(requests.isEmpty())
        val result = add(order) as AppResult.Success<ShoppingCart>
        assertEquals(41L, result.value.id)
        assertEquals(1, requests.size)
    }
}
