package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchasePixDeadline
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class PurchasePixDeadlineTest {
    private val device = Instant.parse("2020-01-01T18:15:00Z")

    @Test fun `server time overrides ahead device time and monotonic passage expires without extension`() {
        val deadline = PurchasePixDeadline.capture(9, "2020-01-01T15:10:00.000", "2020-01-01T15:00:00.000", 10_000, device)
        assertEquals(600, deadline.remainingSeconds(10_000))
        assertEquals(1, deadline.remainingSeconds(609_000))
        assertEquals(0, deadline.remainingSeconds(610_000))
        assertEquals(0, deadline.remainingSeconds(999_000))
    }

    @Test fun `offset timestamps and local Sao Paulo timestamps share the same server instant`() {
        for (expiration in listOf("2020-01-01T18:10:00Z", "2020-01-01T20:10:00+02:00", "2020-01-01T15:10:00")) {
            val deadline = PurchasePixDeadline.capture(9, expiration, "2020-01-01T18:00:00Z", 0, device)
            assertEquals(600, deadline.remainingSeconds(0))
        }
    }

    @Test fun `missing or malformed server time falls back to device and invalid expiration cannot query`() {
        for (server in listOf("", "invalid")) {
            val deadline = PurchasePixDeadline.capture(9, "2020-01-01T15:25:00", server, 0, device)
            assertEquals(600, deadline.remainingSeconds(0))
        }
        assertEquals(0, PurchasePixDeadline.capture(9, "invalid", "2020-01-01T15:00:00", 0, device).remainingSeconds(0))
    }
}
