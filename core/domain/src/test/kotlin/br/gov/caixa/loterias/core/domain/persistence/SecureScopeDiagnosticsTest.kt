package br.gov.caixa.loterias.core.domain.persistence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SecureScopeDiagnosticsTest {
    @Test fun `diagnostics omit environment owner and record contents`() {
        val scope = SecureStoreScope("a".repeat(SCOPE_KEY_LENGTH), "b".repeat(SCOPE_KEY_LENGTH), "private_record")
        val diagnostic = scope.toString()
        assertFalse(diagnostic.contains(scope.environmentKey))
        assertFalse(diagnostic.contains(scope.ownerKey))
        assertFalse(diagnostic.contains(scope.record))
    }

    @Test fun `scope identity and storage coordinates remain intact`() {
        val scope = SecureStoreScope("a".repeat(SCOPE_KEY_LENGTH), "b".repeat(SCOPE_KEY_LENGTH), "private_record")
        assertEquals(scope, scope.copy())
        assertEquals("a".repeat(SCOPE_KEY_LENGTH), scope.environmentKey)
        assertEquals("b".repeat(SCOPE_KEY_LENGTH), scope.ownerKey)
        assertEquals("private_record", scope.record)
    }
}

private const val SCOPE_KEY_LENGTH = 64
