package br.gov.caixa.loterias.core.common

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.common.time.LotteryDateTimes
import br.gov.caixa.loterias.core.common.time.MonotonicServerClock

class ServerTimeSourceTest {
    private val initial = Instant.parse("2026-09-06T21:00:00Z")
    private var elapsed = 1000L
    private val clock = MonotonicServerClock({ elapsed }, Clock.fixed(initial, ZoneOffset.UTC))

    @Test fun `default startup uses current UTC time until first server synchronization`() {
        val before = Instant.now()
        val startup = MonotonicServerClock(elapsedMillis = { elapsed })
        val after = Instant.now()
        assertFalse(startup.now().isBefore(before))
        assertFalse(startup.now().isAfter(after))
        assertTrue(startup.synchronize("2026-09-06T21:00:00Z"))
        assertEquals(initial, startup.now())
    }

    @Test fun `startup and synchronized time advance by elapsed time`() {
        assertEquals(initial, clock.now())
        elapsed += 6000
        assertEquals(initial.plusSeconds(6), clock.now())
        assertTrue(clock.synchronize("2026-09-06T17:00:00.000"))
        assertEquals(initial.minusSeconds(3600), clock.now())
        elapsed += 1234
        assertEquals(initial.minusSeconds(3600).plusMillis(1234), clock.now())
        elapsed = 0
        assertEquals(initial.minusSeconds(3600), clock.now())
    }

    @Test fun `invalid duplicate and older responses leave accepted anchor intact`() {
        assertFalse(clock.synchronize("invalid"))
        assertEquals(initial, clock.now())
        assertTrue(clock.synchronize("2026-09-06T21:00:00Z"))
        elapsed += 5000
        assertFalse(clock.synchronize("2026-09-06T21:00:00Z"))
        assertFalse(clock.synchronize("2026-09-06T20:59:59Z"))
        assertFalse(clock.synchronize(""))
        assertEquals(initial.plusSeconds(5), clock.now())
        assertTrue(clock.synchronize("2026-09-06T21:00:06Z"))
        assertEquals(initial.plusSeconds(6), clock.now())
    }

    @Test fun `wire formats resolve without relying on device timezone`() {
        listOf("2026-09-06T21:00:00Z", "2026-09-06T18:00:00-03:00",
            "2026-09-06T18:00:00.000", "06/09/2026 18:00:00").forEach {
            assertEquals(initial, LotteryDateTimes.parse(it))
        }
        assertNull(LotteryDateTimes.parse("31/02/2026 18:00:00"))
        assertNull(LotteryDateTimes.parse("2026-02-31T18:00:00"))
        assertNull(LotteryDateTimes.parse("06/09/2026 24:00:00"))
    }
}
