package br.gov.caixa.loterias.core.domain.marketplace;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class MarketplaceFilterTest {
    @Test
    public void homeModalityFactoryKeepsBackendIdentifiers() {
        MarketplaceFilter filter = MarketplaceFilter.forModality(8, 2);

        assertEquals(Integer.valueOf(8), filter.getModalityId());
        assertEquals(Integer.valueOf(2), filter.getContestType());
        assertEquals(MarketplaceFilter.forModality(8, 2), filter.withBetQuantity(0));
    }

    @Test
    public void empty_hasNeutralValues() {
        MarketplaceFilter filter = MarketplaceFilter.empty();
        assertEquals("", filter.getMinimumValue());
        assertEquals("", filter.getMaximumValue());
        assertNull(filter.getMinimumShares());
        assertNull(filter.getMaximumShares());
        assertNull(filter.getBetQuantity());
        assertNull(filter.getNumberQuantity());
        assertEquals("", filter.getLottery());
        assertEquals("", filter.getModality());
        assertTrue(filter.getWantedNumbers().isEmpty());
        assertTrue(filter.getUnwantedNumbers().isEmpty());
    }

    @Test
    public void constructor_normalizesInputAndDefensivelyCopiesLists() {
        ArrayList<Integer> wanted = new ArrayList<>(Arrays.asList(1, 2));
        MarketplaceFilter filter = new MarketplaceFilter(
                " 1,00 ", null, 0, -1, 3, 6, " Centro ", " Mega ", wanted, null
        );
        wanted.add(3);

        assertEquals("1,00", filter.getMinimumValue());
        assertEquals("", filter.getMaximumValue());
        assertNull(filter.getMinimumShares());
        assertNull(filter.getMaximumShares());
        assertEquals(Integer.valueOf(3), filter.getBetQuantity());
        assertEquals(Integer.valueOf(6), filter.getNumberQuantity());
        assertEquals("Centro", filter.getLottery());
        assertEquals("Mega", filter.getModality());
        assertEquals(Arrays.asList(1, 2), filter.getWantedNumbers());
        assertTrue(filter.getUnwantedNumbers().isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void numberLists_areImmutable() {
        MarketplaceFilter.empty().getWantedNumbers().add(1);
    }

    @Test
    public void copyOperations_changeOnlyRequestedValues() {
        MarketplaceFilter original = new MarketplaceFilter(
                "1", "2", 3, 4, 5, 6, "L", "M",
                Collections.singletonList(7), Collections.singletonList(8)
        );
        MarketplaceFilter changed = original
                .withBetQuantity(9)
                .withNumberQuantity(10)
                .withWantedNumbers(Arrays.asList(11, 12))
                .withUnwantedNumbers(Collections.singletonList(13));

        assertEquals("1", changed.getMinimumValue());
        assertEquals("2", changed.getMaximumValue());
        assertEquals(Integer.valueOf(3), changed.getMinimumShares());
        assertEquals(Integer.valueOf(4), changed.getMaximumShares());
        assertEquals(Integer.valueOf(9), changed.getBetQuantity());
        assertEquals(Integer.valueOf(10), changed.getNumberQuantity());
        assertEquals(Arrays.asList(11, 12), changed.getWantedNumbers());
        assertEquals(Collections.singletonList(13), changed.getUnwantedNumbers());
    }

    @Test
    public void withModality_preservesEveryFilterAndAddsBackendIdentifiers() {
        MarketplaceFilter original = new MarketplaceFilter(
                "1", "2", 3, 4, 5, 6, "101", "Mega-Sena",
                Collections.singletonList(7), Collections.singletonList(8)
        );
        MarketplaceFilter changed = original.withModality(2, 1);

        assertEquals(Integer.valueOf(2), changed.getModalityId());
        assertEquals(Integer.valueOf(1), changed.getContestType());
        assertEquals("101", changed.getLottery());
        assertEquals(Integer.valueOf(5), changed.getBetQuantity());
        assertEquals(Collections.singletonList(7), changed.getWantedNumbers());
    }

    @Test
    public void equalsAndHashCode_obeyValueSemantics() {
        MarketplaceFilter first = MarketplaceFilter.empty();
        MarketplaceFilter second = MarketplaceFilter.empty();
        assertEquals(first, first);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, null);
        assertNotEquals(first, "filter");
        assertNotEquals(first, first.withBetQuantity(2));
        assertNotEquals(first, first.withNumberQuantity(6));
        assertNotEquals(first, first.withWantedNumbers(Collections.singletonList(1)));
        assertNotEquals(first, first.withUnwantedNumbers(Collections.singletonList(2)));
    }
}
