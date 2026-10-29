package br.gov.caixa.loterias.core.domain.marketplace

import org.junit.Test
import org.junit.Assert.*
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter

class MarketplaceFilterTest {
    @Test
    fun homeModalityFactoryKeepsBackendIdentifiers() {
        val filter = MarketplaceFilter.forModality(8, 2)

        assertEquals(8, filter.modalityId)
        assertEquals(2, filter.contestType)
        assertEquals(MarketplaceFilter.forModality(8, 2), filter.withBetQuantity(0))
    }

    @Test
    fun empty_hasNeutralValues() {
        val filter = MarketplaceFilter.empty()
        assertEquals("", filter.minimumValue)
        assertEquals("", filter.maximumValue)
        assertNull(filter.minimumShares)
        assertNull(filter.maximumShares)
        assertNull(filter.betQuantity)
        assertNull(filter.numberQuantity)
        assertEquals("", filter.lottery)
        assertEquals("", filter.modality)
        assertTrue(filter.wantedNumbers.isEmpty())
        assertTrue(filter.unwantedNumbers.isEmpty())
    }

    @Test
    fun constructor_normalizesInputAndDefensivelyCopiesLists() {
        val wanted = arrayListOf(1, 2)
        val filter = MarketplaceFilter(
                " 1,00 ", null, 0, -1, 3, 6, " Centro ", " Mega ", wanted, null
        )
        wanted.add(3)

        assertEquals("1,00", filter.minimumValue)
        assertEquals("", filter.maximumValue)
        assertNull(filter.minimumShares)
        assertNull(filter.maximumShares)
        assertEquals(3, filter.betQuantity)
        assertEquals(6, filter.numberQuantity)
        assertEquals("Centro", filter.lottery)
        assertEquals("Mega", filter.modality)
        assertEquals(listOf(1, 2), filter.wantedNumbers)
        assertTrue(filter.unwantedNumbers.isEmpty())
    }

    @Test(expected = UnsupportedOperationException::class)
    fun numberLists_areImmutable() {
        (MarketplaceFilter.empty().wantedNumbers as MutableList<Int>).add(1)
    }

    @Test
    fun copyOperations_changeOnlyRequestedValues() {
        val original = MarketplaceFilter(
                "1", "2", 3, 4, 5, 6, "L", "M",
                listOf(7), listOf(8)
        )
        val changed = original
                .withBetQuantity(9)
                .withNumberQuantity(10)
                .withWantedNumbers(listOf(11, 12))
                .withUnwantedNumbers(listOf(13))

        assertEquals("1", changed.minimumValue)
        assertEquals("2", changed.maximumValue)
        assertEquals(3, changed.minimumShares)
        assertEquals(4, changed.maximumShares)
        assertEquals(9, changed.betQuantity)
        assertEquals(10, changed.numberQuantity)
        assertEquals(listOf(11, 12), changed.wantedNumbers)
        assertEquals(listOf(13), changed.unwantedNumbers)
    }

    @Test
    fun withModality_preservesEveryFilterAndAddsBackendIdentifiers() {
        val original = MarketplaceFilter(
                "1", "2", 3, 4, 5, 6, "101", "Mega-Sena",
                listOf(7), listOf(8)
        )
        val changed = original.withModality(2, 1)

        assertEquals(2, changed.modalityId)
        assertEquals(1, changed.contestType)
        assertEquals("101", changed.lottery)
        assertEquals(5, changed.betQuantity)
        assertEquals(listOf(7), changed.wantedNumbers)
    }

    @Test
    fun equalsAndHashCode_obeyValueSemantics() {
        val first = MarketplaceFilter.empty()
        val second = MarketplaceFilter.empty()
        assertEquals(first, first)
        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertNotEquals(first, null)
        assertNotEquals(first, "filter")
        assertNotEquals(first, first.withBetQuantity(2))
        assertNotEquals(first, first.withNumberQuantity(6))
        assertNotEquals(first, first.withWantedNumbers(listOf(1)))
        assertNotEquals(first, first.withUnwantedNumbers(listOf(2)))
    }
}
