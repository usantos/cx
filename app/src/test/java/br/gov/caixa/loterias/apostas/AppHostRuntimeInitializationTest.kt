package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.apostas.runtime.host.AppHostRuntimeInitialization
import org.junit.Assert.assertEquals
import org.junit.Test

class AppHostRuntimeInitializationTest {
    @Test
    fun `network clients initialize once for the host runtime lifetime`() {
        val events = mutableListOf<String>()
        val initialization = AppHostRuntimeInitialization { events += "network-clients" }

        initialization.initialize()
        initialization.initialize()

        assertEquals(listOf("network-clients"), events)
    }
}
