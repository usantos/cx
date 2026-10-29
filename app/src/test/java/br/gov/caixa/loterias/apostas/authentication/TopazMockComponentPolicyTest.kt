package br.gov.caixa.loterias.apostas.authentication

import br.gov.caixa.loterias.core.security.topaz.ComponentOverrideBackup
import br.gov.caixa.loterias.core.security.topaz.ComponentOverrideController
import br.gov.caixa.loterias.core.security.topaz.TopazMockComponentPolicy
import org.junit.Assert.*
import org.junit.Test

class TopazMockComponentPolicyTest {
    private val names = listOf("default", "enabled", "disabled", "user-disabled", "until-used")
    private val originals = mapOf("default" to 0, "enabled" to 1, "disabled" to 2, "user-disabled" to 3, "until-used" to 4)

    @Test fun `mock snapshots before changing PM and real mode restores only recorded original overrides`() {
        val fixture = Fixture(originals)
        fixture.apply(true)
        assertEquals(listOf("persist", "set:default:2", "set:enabled:2", "set:user-disabled:2", "set:until-used:2"), fixture.events)
        assertEquals(originals.filterValues { it != 2 }, fixture.persisted)
        assertTrue(fixture.states.values.all { it == 2 })
        fixture.events.clear()
        fixture.apply(true)
        assertEquals(listOf("persist"), fixture.events)
        assertEquals(originals.filterValues { it != 2 }, fixture.persisted)
        fixture.events.clear()
        fixture.apply(false)
        assertEquals(originals, fixture.states)
        assertNull(fixture.persisted)
        assertEquals(listOf("set:default:0", "set:enabled:1", "set:user-disabled:3", "set:until-used:4", "clear"), fixture.events)
    }

    @Test fun `production without our marker leaves every PM state untouched`() {
        val fixture = Fixture(originals)
        fixture.apply(false)
        assertTrue(fixture.events.isEmpty())
        assertEquals(originals, fixture.states)
    }

    @Test fun `failure persisting recovery data prevents every PM mutation`() {
        val fixture = Fixture(originals).apply { saveFails = true }
        assertTrue(runCatching { fixture.apply(true) }.isFailure)
        assertEquals(originals, fixture.states)
        assertNull(fixture.persisted)
        assertEquals(listOf("persist"), fixture.events)
    }

    @Test fun `partial PM failure retains recoverable originals and restoration only changes affected states`() {
        val fixture = Fixture(originals).apply { failedComponent = "enabled" }
        assertTrue(runCatching { fixture.apply(true) }.isFailure)
        assertEquals(2, fixture.states["default"])
        assertEquals(1, fixture.states["enabled"])
        assertNotNull(fixture.persisted)
        fixture.failedComponent = null
        fixture.events.clear()
        fixture.apply(false)
        assertEquals(originals, fixture.states)
        assertEquals(listOf("set:default:0", "clear"), fixture.events)
    }

    @Test fun `marker clear failure leaves restored states safe and can retry without repeating PM mutations`() {
        val fixture = Fixture(originals)
        fixture.apply(true)
        fixture.clearFails = true
        assertTrue(runCatching { fixture.apply(false) }.isFailure)
        assertEquals(originals, fixture.states)
        assertNotNull(fixture.persisted)
        fixture.clearFails = false
        fixture.events.clear()
        fixture.apply(false)
        assertEquals(listOf("clear"), fixture.events)
        assertNull(fixture.persisted)
    }

    @Test fun `an unrelated component in backup is rejected before any PM restoration`() {
        val fixture = Fixture(originals).apply { persisted = mapOf("default" to 0, "unrelated-sdk" to 1) }
        assertTrue(runCatching { fixture.apply(false) }.isFailure)
        assertTrue(fixture.events.isEmpty())
        assertEquals(originals, fixture.states)
    }

    private inner class Fixture(initial: Map<String, Int>) {
        val states = initial.toMutableMap()
        val events = mutableListOf<String>()
        var persisted: Map<String, Int>? = null
        var saveFails = false
        var clearFails = false
        var failedComponent: String? = null
        private val controller = object : ComponentOverrideController {
            override fun state(name: String) = states.getValue(name)
            override fun setState(name: String, state: Int) {
                check(name != failedComponent) { "PM fixture failure" }
                events += "set:$name:$state"
                states[name] = state
            }
        }
        private val backup = object : ComponentOverrideBackup {
            override fun read() = persisted
            override fun save(states: Map<String, Int>): Boolean {
                events += "persist"
                if (saveFails) return false
                persisted = states.toMap()
                return true
            }
            override fun clear(): Boolean {
                events += "clear"
                if (clearFails) return false
                persisted = null
                return true
            }
        }
        fun apply(mock: Boolean) = TopazMockComponentPolicy(names, controller, backup).apply(mock)
    }
}
