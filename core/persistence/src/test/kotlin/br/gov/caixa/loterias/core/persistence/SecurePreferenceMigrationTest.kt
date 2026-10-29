package br.gov.caixa.loterias.core.persistence

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurePreferenceMigrationTest {
    @Test fun cleanupWaitsForVerifiedEncryptedCommitAndRestartKeepsNewerValue() = runTest {
        val store = MemoryStore()
        var legacy = mapOf("enabled" to "false")
        val migration = SecurePreferenceMigration(store, { legacy }, { legacy = emptyMap() })
        assertEquals("false", migration.read().values["enabled"])
        assertTrue(legacy.isEmpty())
        migration.update { it + ("enabled" to "true") }
        legacy = mapOf("enabled" to "false")
        val restarted = SecurePreferenceMigration(store, { legacy }, { legacy = emptyMap() })
        assertEquals("true", restarted.read().values["enabled"])
        assertTrue(legacy.isEmpty())
    }

    @Test fun failedCommitPreservesLegacyAndDoesNotAcknowledgeMigration() = runTest {
        val store = MemoryStore().apply { fail = true }
        var cleaned = false
        val migration = SecurePreferenceMigration(store, { mapOf("enabled" to "true") }, { cleaned = true })
        try {
            migration.read()
            throw AssertionError("Expected storage failure")
        } catch (_: SecureStorageException.WriteFailure) {
            assertFalse(cleaned)
            assertTrue(store.snapshot.values.isEmpty())
        }
        store.fail = false
        assertEquals("true", migration.read().values["enabled"])
        assertTrue(cleaned)
    }

    @Test fun interruptedCleanupDoesNotOverwriteANewerEncryptedValue() = runTest {
        val store = MemoryStore()
        var interrupt = true
        var legacy = mapOf("enabled" to "false")
        val migration = SecurePreferenceMigration(store, { legacy }, {
            if (interrupt) throw SecureStorageException.WriteFailure()
            legacy = emptyMap()
        })
        try {
            migration.read()
            throw AssertionError("Expected cleanup interruption")
        } catch (_: SecureStorageException.WriteFailure) {
            assertEquals("false", store.snapshot.values["enabled"])
            assertFalse(legacy.isEmpty())
        }
        store.update { it + ("enabled" to "true") }
        interrupt = false
        assertEquals("true", migration.read().values["enabled"])
        assertTrue(legacy.isEmpty())
    }

    private class MemoryStore : SecureRecordStore {
        var snapshot = SecureSnapshot(0, emptyMap())
        var fail = false
        override suspend fun read() = snapshot
        override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot {
            if (fail) throw SecureStorageException.WriteFailure()
            snapshot = SecureSnapshot(snapshot.revision + 1, transform(snapshot.values))
            return snapshot
        }
    }
}
