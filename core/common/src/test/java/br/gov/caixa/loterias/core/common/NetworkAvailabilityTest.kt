package br.gov.caixa.loterias.core.common

import org.junit.Assert.assertEquals
import org.junit.Test
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.ReadNetworkAvailability

class NetworkAvailabilityTest {
    @Test fun `available missing and unobservable connection stay distinct`() {
        assertEquals(ConnectionAvailability.CONNECTED, ReadNetworkAvailability { true }.current())
        assertEquals(ConnectionAvailability.DISCONNECTED, ReadNetworkAvailability { false }.current())
        assertEquals(ConnectionAvailability.UNKNOWN, ReadNetworkAvailability { null }.current())
    }

    @Test fun `every request reads again after connection changes`() {
        var connected = false
        var reads = 0
        val availability = ReadNetworkAvailability { reads++; connected }
        assertEquals(ConnectionAvailability.DISCONNECTED, availability.current())
        connected = true
        assertEquals(ConnectionAvailability.CONNECTED, availability.current())
        connected = false
        assertEquals(ConnectionAvailability.DISCONNECTED, availability.current())
        assertEquals(3, reads)
    }

    @Test fun `missing permission is unknown rather than a fabricated offline diagnosis`() {
        assertEquals(ConnectionAvailability.UNKNOWN,
            ReadNetworkAvailability { throw SecurityException("Permission denied") }.current())
    }

    @Test(expected = IllegalStateException::class)
    fun `programming failures are not swallowed as connectivity state`() {
        ReadNetworkAvailability { error("Broken probe") }.current()
    }
}
