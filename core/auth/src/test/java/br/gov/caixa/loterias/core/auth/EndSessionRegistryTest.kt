package br.gov.caixa.loterias.core.auth

import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.auth.authorization.EndSessionRegistry

class EndSessionRegistryTest {
    @Test fun `only exact handle launches original request once and exact state returns once`() {
        val registry = EndSessionRegistry<Any>()
        val request = Any()
        val launch = registry.prepare("secret-state", request)
        assertEquals("EndSessionLaunch(redacted)", launch.toString())
        assertNull(registry.takeRequest(EndSessionLaunch()))
        assertSame(request, registry.takeRequest(launch))
        assertNull(registry.takeRequest(launch))
        assertEquals(EndSessionOutcome.RETURNED, registry.complete(launch, "secret-state", false))
        assertEquals(EndSessionOutcome.UNCONFIRMED, registry.complete(launch, "secret-state", false))
        assertNull(registry.takeRequest(launch))
    }

    @Test fun `missing wrong and unlaunched states never confirm logout`() {
        for (state in listOf(null, "STATE", "state ", "unknown")) {
            val registry = EndSessionRegistry<String>()
            val launch = registry.prepare("state", "request")
            registry.takeRequest(launch)
            assertEquals(EndSessionOutcome.UNCONFIRMED, registry.complete(launch, state, false))
            assertEquals(EndSessionOutcome.UNCONFIRMED, registry.complete(launch, "state", false))
        }
        val registry = EndSessionRegistry<String>()
        val unlaunched = registry.prepare("state", "request")
        assertEquals(EndSessionOutcome.UNCONFIRMED, registry.complete(unlaunched, "state", false))
    }

    @Test fun `old callback and cleanup do not consume newer request`() {
        val registry = EndSessionRegistry<String>()
        val old = registry.prepare("old", "old-request")
        registry.takeRequest(old)
        val fresh = registry.prepare("fresh", "fresh-request")
        registry.discard(old)
        assertEquals(EndSessionOutcome.UNCONFIRMED, registry.complete(old, "old", false))
        assertEquals("fresh-request", registry.takeRequest(fresh))
        assertEquals(EndSessionOutcome.RETURNED, registry.complete(fresh, "fresh", false))
    }

    @Test fun `cancel never becomes a successful callback even if state is supplied`() {
        val registry = EndSessionRegistry<String>()
        val launch = registry.prepare("state", "request")
        registry.takeRequest(launch)
        assertEquals(EndSessionOutcome.CANCELLED, registry.complete(launch, "state", true))
        assertNull(registry.takeRequest(launch))
    }

    @Test fun `discard clear and invalid state fail closed`() {
        val registry = EndSessionRegistry<String>()
        assertTrue(runCatching { registry.prepare(" ", "request") }.isFailure)
        val discarded = registry.prepare("state", "request")
        registry.discard(discarded)
        assertNull(registry.takeRequest(discarded))
        val cleared = registry.prepare("state", "request")
        registry.clear()
        registry.discard(cleared)
        assertEquals(EndSessionOutcome.UNCONFIRMED, registry.complete(cleared, "state", false))
    }
}
