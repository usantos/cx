package br.gov.caixa.loterias.core.domain.marketplace

import br.gov.caixa.loterias.core.common.result.AppResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceLocation
import br.gov.caixa.loterias.feature.marketplace.domain.port.MarketplaceLocationProvider
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSearchArea
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceRepository
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplace

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

    @Test fun `retailer filter retains native geographic progression and account location`() = runTest {
        val calls = mutableListOf<MarketplaceFilter>()
        val search = SearchMarketplace(MarketplaceRepository { filter, page ->
            calls += filter
            MarketplacePage(emptyList(), page, page, 0)
        }, MarketplaceLocationProvider { MarketplaceLocation(123, 53) })
        search(MarketplaceFilter.empty().withLottery("987"), 0)
        search(MarketplaceFilter.empty().withLottery("987").withSearchArea(MarketplaceSearchArea.STATE), 0)
        search(MarketplaceFilter.empty().withLottery("987").withSearchArea(MarketplaceSearchArea.NATIONAL), 0)
        assertEquals(listOf(MarketplaceSearchArea.MUNICIPALITY, MarketplaceSearchArea.STATE, MarketplaceSearchArea.NATIONAL),
            calls.map { it.searchArea })
        assertTrue(calls.all { it.lottery == "987" && it.municipalityId == 123L && it.stateId == 53L })
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
