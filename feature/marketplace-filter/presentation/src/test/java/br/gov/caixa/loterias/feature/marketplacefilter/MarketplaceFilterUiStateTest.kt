package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.initialMarketplaceFilterState

class MarketplaceFilterUiStateTest {
    @Test
    fun `backend limits create inclusive bet options`() {
        val state = initialMarketplaceFilterState(MarketplaceFilterParameters(2, 4))

        assertEquals(listOf(2, 3, 4), state.betOptions)
        assertNull(state.configurationError)
        assertEquals(true, state.canApply)
    }

    @Test
    fun `missing backend limits do not silently use fixed values`() {
        val state = initialMarketplaceFilterState(MarketplaceFilterParameters(null, null))

        assertEquals(emptyList<Int>(), state.betOptions)
        assertEquals("Configuração do filtro indisponível", state.configurationError)
        assertEquals(false, state.canApply)
    }
}
