package br.gov.caixa.loterias.core.preferences

import java.util.concurrent.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class LegacyLocalSessionDataRepositoryTest {
    @Test fun `clears flag and identity stores in order on every explicit call`() = runTest {
        val store = MemoryStore()
        val repository = LegacyLocalSessionDataRepository(store)
        repository.clear()
        repository.clear()
        assertEquals(listOf("pending:true", "flag", "identity", "pending:false", "pending:true", "flag", "identity", "pending:false"), store.calls)
        assertFalse(store.pending)
    }

    @Test fun `failure in either store is reported after attempting both and retry can finish`() = runTest {
        for (failures in listOf(setOf("flag"), setOf("identity"), setOf("flag", "identity"))) {
            val store = MemoryStore().apply { this.failures = failures }
            val repository = LegacyLocalSessionDataRepository(store)
            try { repository.clear(); fail("Must reject incomplete cleanup") }
            catch (error: IllegalStateException) {
                assertEquals("Não foi possível limpar os dados locais da sessão", error.message)
                assertNull(error.cause) // No credential/disk payload in UI/log diagnostics.
            }
            assertEquals(listOf("pending:true", "flag", "identity"), store.calls)
            assertTrue(store.pending)
            store.failures = emptySet()
            repository.clear()
            assertEquals(listOf("pending:true", "flag", "identity", "pending:true", "flag", "identity", "pending:false"), store.calls)
            assertFalse(store.pending)
        }
    }

    @Test fun `cancellation is not swallowed as a disk failure`() = runTest {
        val cancellation = CancellationException("cancelled")
        val repository = LegacyLocalSessionDataRepository(object : LegacySessionDataStore {
            override fun isCleanupPending() = true
            override fun setCleanupPending(pending: Boolean) { assertTrue(pending) }
            override fun clearLoginFlag() { throw cancellation }
            override fun clearIdentityAndBiometricRecords() = error("Must not continue after cancellation")
        })
        try { repository.clear(); fail("Must propagate cancellation") }
        catch (actual: CancellationException) { assertSame(cancellation, actual) }
    }

    @Test fun `recreated repository resumes persisted failure but not a completed or nonexistent cleanup`() = runTest {
        val store = MemoryStore()
        assertFalse(LegacyLocalSessionDataRepository(store).resumePending())
        assertEquals(listOf("read"), store.calls)
        store.calls.clear()
        store.failures = setOf("identity")
        try { LegacyLocalSessionDataRepository(store).clear(); fail("Must fail") } catch (_: IllegalStateException) { }
        assertTrue(store.pending)
        store.failures = emptySet()
        assertTrue(LegacyLocalSessionDataRepository(store).resumePending())
        assertFalse(store.pending)
        assertFalse(LegacyLocalSessionDataRepository(store).resumePending())
        assertEquals(listOf("pending:true", "flag", "identity", "read", "pending:true", "flag", "identity", "pending:false", "read"), store.calls)
    }

    @Test fun `marker read and writes cannot be treated as successful recovery when they fail`() = runTest {
        val store = MemoryStore().apply { failures = setOf("pending:true") }
        val repository = LegacyLocalSessionDataRepository(store)
        try { repository.clear(); fail("Must persist marker first") } catch (_: IllegalStateException) { }
        assertEquals(listOf("pending:true"), store.calls)
        store.failures = setOf("pending:false")
        try { repository.clear(); fail("Must persist completion") } catch (_: IllegalStateException) { }
        assertTrue(store.pending)
        store.calls.clear()
        store.failures = setOf("read")
        try { repository.resumePending(); fail("Unreadable marker is not false") } catch (_: IllegalStateException) { }
        assertEquals(listOf("read"), store.calls)
        store.failures = emptySet()
        assertTrue(repository.resumePending())
        assertFalse(repository.resumePending())
    }

    @Test fun `cleanup and entry recovery on same singleton are serialized`() {
        val entered = java.util.concurrent.CountDownLatch(1)
        val release = java.util.concurrent.CountDownLatch(1)
        val calls = java.util.Collections.synchronizedList(mutableListOf<String>())
        val repository = LegacyLocalSessionDataRepository(object : LegacySessionDataStore {
            var pending = false
            override fun isCleanupPending(): Boolean { calls += "read"; return pending }
            override fun setCleanupPending(pending: Boolean) { this.pending = pending; calls += "pending:$pending" }
            override fun clearLoginFlag() { calls += "flag" }
            override fun clearIdentityAndBiometricRecords() {
                calls += "identity"
                entered.countDown()
                check(release.await(3, java.util.concurrent.TimeUnit.SECONDS))
            }
        })
        val executor = java.util.concurrent.Executors.newFixedThreadPool(2)
        try {
            val clear = executor.submit { kotlinx.coroutines.runBlocking { repository.clear() } }
            assertTrue(entered.await(3, java.util.concurrent.TimeUnit.SECONDS))
            val recovery = executor.submit<Boolean> { kotlinx.coroutines.runBlocking { repository.resumePending() } }
            assertEquals(listOf("pending:true", "flag", "identity"), calls.toList())
            release.countDown()
            clear.get(3, java.util.concurrent.TimeUnit.SECONDS)
            assertFalse(recovery.get(3, java.util.concurrent.TimeUnit.SECONDS))
            assertEquals(listOf("pending:true", "flag", "identity", "pending:false", "read"), calls.toList())
        } finally { release.countDown(); executor.shutdownNow() }
    }

    private class MemoryStore : LegacySessionDataStore {
        val calls = mutableListOf<String>()
        var pending = false
        var failures = emptySet<String>()
        private fun clear(name: String) {
            calls += name
            check(name !in failures) { "Sensitive internal failure must not escape" }
        }
        override fun clearLoginFlag() = clear("flag")
        override fun clearIdentityAndBiometricRecords() = clear("identity")
        override fun isCleanupPending(): Boolean { clear("read"); return pending }
        override fun setCleanupPending(pending: Boolean) { clear("pending:$pending"); this.pending = pending }
    }
}
