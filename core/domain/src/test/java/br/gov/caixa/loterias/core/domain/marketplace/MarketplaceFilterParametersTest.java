package br.gov.caixa.loterias.core.domain.marketplace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Test;

public class MarketplaceFilterParametersTest {
    @Test
    public void createsOptionsFromBackendLimits() {
        MarketplaceFilterParameters parameters = new MarketplaceFilterParameters(3, 5);

        assertEquals(3, parameters.getMinimumBetQuantity());
        assertEquals(5, parameters.getMaximumBetQuantity());
        assertEquals(Arrays.asList(3, 4, 5), parameters.getBetOptions());
        assertTrue(parameters.isValid());
    }

    @Test
    public void nullOrInvalidLimitsDoNotCreateFallbackRange() {
        MarketplaceFilterParameters nullParameters = new MarketplaceFilterParameters(null, null);
        MarketplaceFilterParameters reversed = new MarketplaceFilterParameters(5, 3);

        assertEquals(0, nullParameters.getMinimumBetQuantity());
        assertEquals(0, nullParameters.getMaximumBetQuantity());
        assertTrue(nullParameters.getBetOptions().isEmpty());
        assertFalse(nullParameters.isValid());
        assertTrue(reversed.getBetOptions().isEmpty());
        assertFalse(reversed.isValid());
    }

    @Test
    public void valueSemanticsUseBothLimits() {
        MarketplaceFilterParameters first = new MarketplaceFilterParameters(1, 2);
        MarketplaceFilterParameters equal = new MarketplaceFilterParameters(1, 2);
        MarketplaceFilterParameters different = new MarketplaceFilterParameters(1, 3);

        assertEquals(first, first);
        assertEquals(first, equal);
        assertEquals(first.hashCode(), equal.hashCode());
        assertNotEquals(first, null);
        assertNotEquals(first, "limits");
        assertNotEquals(first, different);
    }
}
