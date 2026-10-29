package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterStateAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.reduceMarketplaceFilterState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class MarketplaceFilterReduxStateReducerTest {
    @Test fun `late retailer response leaves a newer query unchanged`() {
        val state = MarketplaceFilterUiState(lottery = "new query", retailerLoading = true)
        assertSame(state, reduceMarketplaceFilterState(state, MarketplaceFilterStateAction.RetailersReceived("older", emptyList())))
    }

    @Test fun `wanted numbers are distinct and sorted by reducer`() {
        val state = reduceMarketplaceFilterState(
            MarketplaceFilterUiState(),
            MarketplaceFilterStateAction.WantedNumbersChanged(listOf(LARGEST_NUMBER, SMALLEST_NUMBER, LARGEST_NUMBER, MIDDLE_NUMBER))
        )
        assertEquals(listOf(SMALLEST_NUMBER, MIDDLE_NUMBER, LARGEST_NUMBER), state.wantedNumbers)
    }
}

private const val SMALLEST_NUMBER = 3
private const val MIDDLE_NUMBER = 6
private const val LARGEST_NUMBER = 12
