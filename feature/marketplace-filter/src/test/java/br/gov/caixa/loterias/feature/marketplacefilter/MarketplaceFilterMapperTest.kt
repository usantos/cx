package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.core.domain.retail.Retailer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MarketplaceFilterMapperTest {
    @Test
    fun `ui state maps every filter and backend identifier to domain`() {
        val modality = MarketplaceFilterModalityUi(2, "Mega-Sena", "Mega-Sena", 1, listOf(6), 60)
        val retailer = Retailer(101, "0001-01", "Centro", "", "", "", "", "", false)
        val filter = MarketplaceFilterUiState(
            selectedModality = modality,
            lottery = retailerDisplayLabel(retailer),
            selectedRetailer = retailer,
            minimumValue = "1.234,50",
            maximumValue = "2,00",
            minimumShares = "3",
            maximumShares = "4",
            selectedBetQuantity = 5,
            selectedNumberQuantity = 6,
            wantedNumbers = listOf(7, 8),
            unwantedNumbers = listOf(9, 10),
        ).toDomain()

        assertEquals("Mega-Sena", filter.modality)
        assertEquals(2, filter.modalityId)
        assertEquals(1, filter.contestType)
        assertEquals("101", filter.lottery)
        assertEquals("1234.50", filter.minimumValue)
        assertEquals("2.00", filter.maximumValue)
        assertEquals(3, filter.minimumShares)
        assertEquals(4, filter.maximumShares)
        assertEquals(5, filter.betQuantity)
        assertEquals(6, filter.numberQuantity)
        assertEquals(listOf(7, 8), filter.wantedNumbers)
        assertEquals(listOf(9, 10), filter.unwantedNumbers)
    }

    @Test
    fun `unselected retailer and invalid numeric fields map safely`() {
        val filter = MarketplaceFilterUiState(
            lottery = "texto sem seleção",
            minimumShares = "",
            maximumShares = "invalid",
        ).toDomain()

        assertNull(filter.minimumShares)
        assertNull(filter.maximumShares)
        assertNull(filter.modalityId)
        assertNull(filter.contestType)
        assertEquals("", filter.modality)
        assertEquals("", filter.lottery)
    }
}
