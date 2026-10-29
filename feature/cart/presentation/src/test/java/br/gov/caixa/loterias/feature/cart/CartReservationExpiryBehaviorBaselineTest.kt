package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.feature.cart.presentation.mapper.PoolExpiryState
import br.gov.caixa.loterias.feature.cart.presentation.mapper.poolExpiry
import br.gov.caixa.loterias.feature.cart.presentation.mapper.reservationExpirationInstant
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CartReservationExpiryBehaviorBaselineTest {
    @Test fun offsetUtcAndLegacyLocalTimesDescribeTheSameServerDeadline() {
        val expected = Instant.parse("2026-10-27T15:00:00Z")
        listOf("2026-10-27T15:00:00Z", "2026-10-27T12:00:00-03:00", "2026-10-27T12:00:00").forEach {
            assertEquals(expected, reservationExpirationInstant(it))
        }
        assertNull(reservationExpirationInstant("invalid"))
        assertNull(reservationExpirationInstant(""))
    }

    @Test fun expirationBoundaryAndWarningKeepExistingBehavior() {
        val expires = "2026-10-27T15:00:00Z"
        listOf(
            "2026-10-27T14:58:00Z" to PoolExpiryState.ACTIVE,
            "2026-10-27T14:59:01Z" to PoolExpiryState.WARNING,
            "2026-10-27T15:00:00Z" to PoolExpiryState.EXPIRED,
            "2026-10-27T15:00:01Z" to PoolExpiryState.EXPIRED
        ).forEach { (now, expected) ->
            assertEquals(expected, poolExpiry(expires, Instant.parse(now)).state)
        }
    }
}
