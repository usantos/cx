package br.gov.caixa.loterias.core.domain.persistence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureStoreContractTest {
    @Test fun environmentDigestIsStableAndSeparatesBothFields() {
        val key = secureEnvironmentKey("prd", "https://example.invalid")
        assertTrue(key.matches(Regex("[a-f0-9]{64}")))
        assertEquals(key, secureEnvironmentKey("prd", "https://example.invalid"))
        assertNotEquals(secureEnvironmentKey("ab", "c"), secureEnvironmentKey("a", "bc"))
        assertNotEquals(key, secureEnvironmentKey("hml", "https://example.invalid"))
    }

    @Test fun snapshotDefensivelyCopiesCallerValues() {
        val caller = mutableMapOf("selection" to "old")
        val snapshot = SecureSnapshot(1, caller)
        caller["selection"] = "new"
        assertEquals(mapOf("selection" to "old"), snapshot.values)
        assertEquals(SecureSnapshot(1, mapOf("selection" to "old")), snapshot)
    }

    @Test(expected = IllegalArgumentException::class)
    fun scopeRejectsRawIdentity() {
        SecureStoreScope("installation", "12345678901", "draft")
    }

    @Test(expected = IllegalArgumentException::class)
    fun scopeRejectsDirectoryTraversal() {
        SecureStoreScope("installation", "guest", "../draft")
    }
}
