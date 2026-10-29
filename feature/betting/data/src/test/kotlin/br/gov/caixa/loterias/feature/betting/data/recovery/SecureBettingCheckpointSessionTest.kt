// Massas numéricas descrevem seleções de loteria, identificadores de concursos e limites de esquema.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting.data.recovery

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
class SecureBettingCheckpointSessionTest {
    private class MemoryStore : SecureRecordStore {
        var snapshot = SecureSnapshot(0, emptyMap())
        override suspend fun read() = snapshot
        override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot {
            snapshot = SecureSnapshot(snapshot.revision + 1, transform(snapshot.values))
            return snapshot
        }
    }

    @Test fun `operation is durable before remote intent and reopening never clears uncertainty`() = runTest {
        val store = MemoryStore()
        val session = SecureBettingCheckpointSession(store) { true }
        val checkpoint = BettingCheckpoint(2, "NORMAL", 100, numbers = setOf(1))
        session.save(checkpoint)
        session.beginOperation("intent-1", "ADD_BET", checkpoint)
        val restored = SecureBettingCheckpointSession(store) { true }.read()!!
        assertEquals(checkpoint, restored.checkpoint)
        assertEquals("intent-1", restored.uncertainOperation!!.id)
        assertThrows(IllegalStateException::class.java) {
            runBlocking { session.beginOperation("intent-2", "ADD_BET", checkpoint) }
        }
        session.completeOperation("intent-1")
        assertNull(session.read()!!.uncertainOperation)
    }

    @Test fun `stale lease cannot read publish or overwrite existing checkpoint`() = runTest {
        val store = MemoryStore()
        val active = SecureBettingCheckpointSession(store) { true }
        active.save(BettingCheckpoint(2, "NORMAL", 100, numbers = setOf(1)))
        val previous = store.snapshot
        val revoked = SecureBettingCheckpointSession(store) { false }
        assertThrows(IllegalStateException::class.java) { runBlocking { revoked.read() } }
        assertThrows(IllegalStateException::class.java) {
            runBlocking { revoked.save(BettingCheckpoint(2, "NORMAL", 100, numbers = setOf(2))) }
        }
        assertEquals(previous, store.snapshot)
    }

    @Test fun `rapid operation without simulator draft still survives process restart`() = runTest {
        val store = MemoryStore()
        val session = SecureBettingCheckpointSession(store) { true }
        session.beginOperation("rapid-intent", "GENERATE_RAPID")
        val restored = SecureBettingCheckpointSession(store) { true }.read()!!
        assertNull(restored.checkpoint)
        assertEquals("GENERATE_RAPID", restored.uncertainOperation!!.kind)
    }
}
