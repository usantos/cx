package br.gov.caixa.loterias.feature.cart.domain

import br.gov.caixa.loterias.feature.cart.domain.rule.CartReservationExpiry
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CartReservationExpiryTest {
    @Test fun parsesServerOffsetUtcAndLegacySaoPauloWithoutDeviceTimezone() {
        val expected = Instant.parse("2026-10-27T15:00:00Z")
        listOf("2026-10-27T15:00:00Z", "2026-10-27T12:00:00-03:00", "2026-10-27T12:00:00").forEach {
            assertEquals(expected, CartReservationExpiry.expirationInstant(it))
        }
        assertNull(CartReservationExpiry.expirationInstant("invalid"))
    }

    @Test fun removalUsesExactDeadlineAndNeverRemovesAnUnknownTimestamp() {
        val expires = "2026-10-27T15:00:00Z"
        assertFalse(CartReservationExpiry.removalDue(expires, Instant.parse("2026-10-27T14:59:59.999Z")))
        assertTrue(CartReservationExpiry.removalDue(expires, Instant.parse(expires)))
        assertTrue(CartReservationExpiry.removalDue(expires, Instant.parse("2026-10-27T15:00:00.001Z")))
        assertFalse(CartReservationExpiry.removalDue("invalid", Instant.parse(expires)))
    }
}
