package br.gov.caixa.loterias.core.common.config

import org.junit.Assert.assertEquals
import org.junit.Test

class ConfigurationActivationEventsTest {
    @Test fun subscribersAreIsolatedByIdentityAndCanCloseIndependently() {
        val first = Source(1)
        val second = Source(1)
        var one = 0
        var two = 0
        var other = 0
        val a = ConfigurationActivationEvents.observe(first) { one++ }
        val b = ConfigurationActivationEvents.observe(first) { two++ }
        val c = ConfigurationActivationEvents.observe(second) { other++ }
        try {
            ConfigurationActivationEvents.publish(first)
            a.close()
            a.close()
            ConfigurationActivationEvents.publish(first)
            b.close()
            b.close()
            ConfigurationActivationEvents.publish(first)
            ConfigurationActivationEvents.publish(second)
            assertEquals(1, one)
            assertEquals(2, two)
            assertEquals(1, other)
        } finally {
            a.close()
            b.close()
            c.close()
        }
    }

    private data class Source(val id: Int)
}
