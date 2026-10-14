package br.gov.caixa.loterias.core.domain.marketplace

import br.gov.caixa.loterias.core.common.AppResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketplaceRegionalSearchTest {
    @Test fun `authenticated search uses current account municipality before broader areas`() = runTest {
        val calls = mutableListOf<MarketplaceFilter>()
        val search = SearchMarketplace(MarketplaceRepository { filter, page ->
            calls += filter
            MarketplacePage(emptyList(), page, page, 0)
        }, MarketplaceLocationProvider { MarketplaceLocation(123, 53) })
        assertTrue(search(MarketplaceFilter.empty(), 0) is AppResult.Success)
        search(MarketplaceFilter.empty().withSearchArea(MarketplaceSearchArea.STATE), 0)
        search(MarketplaceFilter.empty().withSearchArea(MarketplaceSearchArea.NATIONAL), 0)
        assertEquals(listOf(MarketplaceSearchArea.MUNICIPALITY, MarketplaceSearchArea.STATE, MarketplaceSearchArea.NATIONAL), calls.map { it.searchArea })
        assertTrue(calls.all { it.municipalityId == 123L && it.stateId == 53L })
    }

    @Test fun `retailer filter uses retailer area instead of geographic progression`() = runTest {
        var actual: MarketplaceFilter? = null
        val search = SearchMarketplace(MarketplaceRepository { filter, page ->
            actual = filter
            MarketplacePage(emptyList(), page, page, 0)
        }, MarketplaceLocationProvider { MarketplaceLocation(123, 53) })
        search(MarketplaceFilter.empty().withLottery("987"), 0)
        assertEquals(MarketplaceSearchArea.RETAILER, actual?.searchArea)
        assertEquals("987", actual?.lottery)
    }

    @Test fun `profile failure stops search instead of querying a different region`() = runTest {
        var calls = 0
        val search = SearchMarketplace(MarketplaceRepository { _, page ->
            calls++
            MarketplacePage(emptyList(), page, page, 0)
        }, MarketplaceLocationProvider { error("account unavailable") })
        assertTrue(search(MarketplaceFilter.empty(), 0) is AppResult.Failure)
        assertEquals(0, calls)
    }
}
