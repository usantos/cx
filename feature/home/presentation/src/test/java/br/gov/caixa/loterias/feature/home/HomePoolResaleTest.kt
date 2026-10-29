package br.gov.caixa.loterias.feature.home

import br.gov.caixa.loterias.feature.home.presentation.mapper.homePoolResaleCountdown
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class HomePoolResaleTest {
    private val now = Instant.parse("2026-10-21T21:00:00Z")
    @Test fun `extra window starts only after individual closing and ends at resale expiry`() {
        assertNull(homePoolResaleCountdown("21/10/2026 18:01:00", "21/10/2026 18:30:00", now))
        assertEquals("30:00", homePoolResaleCountdown("21/10/2026 18:00:00", "21/10/2026 18:30:00", now)?.label)
        assertNull(homePoolResaleCountdown("21/10/2026 17:00:00", "21/10/2026 18:00:00", now))
        assertNull(homePoolResaleCountdown("inválida", "21/10/2026 18:30:00", now))
        assertNull(homePoolResaleCountdown("21/10/2026 17:00:00", "", now))
    }
    @Test fun `countdown uses legacy blue yellow and red thresholds and hours format`() {
        fun at(end: String) = homePoolResaleCountdown("21/10/2026 17:00:00", "21/10/2026 $end", now)!!
        assertEquals(0xFF004680.toInt(), at("18:10:00").colorArgb)
        assertEquals(0xFFEAAA28.toInt(), at("18:09:59").colorArgb)
        assertEquals(0xFFF60E0E.toInt(), at("18:04:59").colorArgb)
        assertEquals("01:10:00", at("19:10:00").label)
    }
}
