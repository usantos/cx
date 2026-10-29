// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.security

import br.gov.caixa.loterias.core.security.topaz.ComponentOverrideBackup
import br.gov.caixa.loterias.core.security.topaz.ComponentOverrideController
import br.gov.caixa.loterias.core.security.topaz.TopazMockComponentPolicy
import br.gov.caixa.loterias.core.security.topaz.topazMockComponents
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TopazRecoveryPolicyTest {
    private class State :
        ComponentOverrideController,
        ComponentOverrideBackup {
        val components = linkedMapOf("a" to 0, "b" to 1, "disabled" to 2)
        var saved: Map<String, Int>? = null
        var saveSucceeds = true
        var clearSucceeds = true
        var failAt: String? = null
        val mutations = mutableListOf<String>()
        override fun state(name: String) = components.getValue(name)
        override fun setState(name: String, state: Int) {
            check(saved != null) { "mutation before durable backup" }
            check(name != failAt) { "interrupted process" }
            components[name] = state
            mutations += name
        }
        override fun read() = saved
        override fun save(states: Map<String, Int>): Boolean {
            if (saveSucceeds) saved = states.toMap()
            return saveSucceeds
        }
        override fun clear(): Boolean {
            if (clearSucceeds) saved = null
            return clearSucceeds
        }
        fun policy() = TopazMockComponentPolicy(components.keys.toList(), this, this)
    }

    @Test fun `isolation persists original states first and interrupted restore can be retried`() {
        val state = State()
        state.failAt = "b"
        assertTrue(runCatching { state.policy().apply(true) }.isFailure)
        assertEquals(mapOf("a" to 0, "b" to 1), state.saved)
        assertEquals(2, state.components["a"])
        assertEquals(1, state.components["b"])
        state.failAt = null
        state.policy().apply(true)
        assertEquals(mapOf("a" to 0, "b" to 1), state.saved)
        assertTrue(state.components.values.all { it == 2 })
        state.failAt = "b"
        assertTrue(runCatching { state.policy().apply(false) }.isFailure)
        assertNotNull(state.saved)
        state.failAt = null
        state.policy().apply(false)
        assertEquals(mapOf("a" to 0, "b" to 1, "disabled" to 2), state.components)
        assertNull(state.saved)
        state.policy().apply(false)
    }

    @Test fun `backup failures and unknown persisted names refuse unsafe component mutation`() {
        val state = State()
        state.saveSucceeds = false
        assertTrue(runCatching { state.policy().apply(true) }.isFailure)
        assertTrue(state.mutations.isEmpty())
        for (corruption in listOf(mapOf("intruder" to 0), mapOf("a" to 5), mapOf("a" to -1))) {
            state.saved = corruption
            assertTrue(runCatching { state.policy().apply(true) }.isFailure)
            assertTrue(runCatching { state.policy().apply(false) }.isFailure)
            assertTrue(state.mutations.isEmpty())
        }
        state.saved = null
        state.saveSucceeds = true
        state.policy().apply(true)
        state.clearSucceeds = false
        assertTrue(runCatching { state.policy().apply(false) }.isFailure)
        assertNotNull(state.saved)
        state.clearSucceeds = true
        state.policy().apply(false)
        assertNull(state.saved)
    }

    @Test fun `already disabled components never create a backup`() {
        val state = State()
        state.components.replaceAll { _, _ -> 2 }
        state.policy().apply(true)
        assertNull(state.saved)
        assertTrue(state.mutations.isEmpty())
        assertTrue(topazMockComponents.size > 10)
    }
}
