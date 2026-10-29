package br.gov.caixa.loterias.feature.account.data.datasource.local

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SessionEncryptedMigrationTest {
    @Test fun pendingCleanupAndLoginFlagMigrateBeforeLegacyPreferencesAreRemoved() = runTest {
        val context = RuntimeEnvironment.getApplication()
        val pending = context.getSharedPreferences("SESSION_CLEANUP_STATE", 0)
        val login = context.getSharedPreferences("USUARIO_KEY", 0)
        pending.edit().putBoolean("pending", true).commit()
        login.edit().putBoolean("usuariologado", true).commit()
        val store = MemoryStore()
        val source = SessionLocalDataSourceImpl(context, SecureStoreFactory { store })
        assertTrue(source.isCleanupPending())
        assertFalse(pending.contains("pending"))
        assertTrue(login.contains("usuariologado"))
        source.clearLoginFlag()
        assertFalse(login.contains("usuariologado"))
        assertFalse(store.snapshot.values.getValue("login").toBooleanStrict())
        assertTrue(source.isCleanupPending())
        source.setCleanupPending(false)
        assertFalse(source.isCleanupPending())
    }

    @Test fun failedEncryptedMigrationKeepsOriginalCleanupMarkerAndLoginFlag() = runTest {
        val context = RuntimeEnvironment.getApplication()
        val pending = context.getSharedPreferences("SESSION_CLEANUP_STATE", 0)
        pending.edit().putBoolean("pending", true).commit()
        val source = SessionLocalDataSourceImpl(context, SecureStoreFactory { MemoryStore().apply { fail = true } })
        try {
            source.setCleanupPending(true)
            throw AssertionError("Expected durable marker failure")
        } catch (_: SecureStorageException.WriteFailure) {
            assertTrue(pending.getBoolean("pending", false))
        }
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
