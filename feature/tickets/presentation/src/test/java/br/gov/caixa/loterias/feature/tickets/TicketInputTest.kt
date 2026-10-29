package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.feature.tickets.presentation.screen.legacyNsbMask
import br.gov.caixa.loterias.feature.tickets.presentation.screen.legacyRetailerMask
import org.junit.Assert.assertEquals
import org.junit.Test

class TicketInputTest {
    @Test fun `formats nsb with the same legacy groups`() {
        assertEquals("", "".legacyNsbMask())
        assertEquals("1234", "12-34".legacyNsbMask())
        assertEquals("1234-56789", "123456789".legacyNsbMask())
        assertEquals(
            "1234-56789012345678901-23",
            "12345678901234567890123".legacyNsbMask()
        )
    }

    @Test fun `formats retailer with the same legacy groups`() {
        assertEquals("10.000004-1", "10.000004-1".legacyRetailerMask())
        assertEquals("12.345", "12345".legacyRetailerMask())
        assertEquals("12.345678-9", "1234567890".legacyRetailerMask())
    }
}
