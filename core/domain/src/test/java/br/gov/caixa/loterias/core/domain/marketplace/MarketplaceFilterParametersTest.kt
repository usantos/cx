package br.gov.caixa.loterias.core.domain.marketplace

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters

class MarketplaceFilterParametersTest {
    @Test
    fun createsOptionsFromBackendLimits() {
        val parameters = MarketplaceFilterParameters(3, 5)

        assertEquals(3, parameters.minimumBetQuantity)
        assertEquals(5, parameters.maximumBetQuantity)
        assertEquals(listOf(3, 4, 5), parameters.betOptions)
        assertTrue(parameters.isValid)
    }

    @Test
    fun nullOrInvalidLimitsDoNotCreateFallbackRange() {
        val nullParameters = MarketplaceFilterParameters(null, null)
        val reversed = MarketplaceFilterParameters(5, 3)

        assertEquals(0, nullParameters.minimumBetQuantity)
        assertEquals(0, nullParameters.maximumBetQuantity)
        assertTrue(nullParameters.betOptions.isEmpty())
        assertFalse(nullParameters.isValid)
        assertTrue(reversed.betOptions.isEmpty())
        assertFalse(reversed.isValid)
    }

    @Test
    fun valueSemanticsUseBothLimits() {
        val first = MarketplaceFilterParameters(1, 2)
        val equal = MarketplaceFilterParameters(1, 2)
        val different = MarketplaceFilterParameters(1, 3)

        assertEquals(first, first)
        assertEquals(first, equal)
        assertEquals(first.hashCode(), equal.hashCode())
        assertNotEquals(first, null)
        assertNotEquals(first, "limits")
        assertNotEquals(first, different)
    }
}
