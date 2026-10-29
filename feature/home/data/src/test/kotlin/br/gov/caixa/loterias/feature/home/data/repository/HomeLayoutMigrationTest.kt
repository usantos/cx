package br.gov.caixa.loterias.feature.home.data.repository

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HomeLayoutMigrationTest {
    @Test fun migrationVerifiesEncryptedOrderBeforeRemovingSourceAndNeverReimportsStaleOrder() = runTest {
        val context = RuntimeEnvironment.getApplication()
        val preferences = context.getSharedPreferences("home_v2_layout", 0)
        preferences.edit().putString("modality_order", "QUINA,MEGA_SENA").commit()
        val store = MemoryStore()
        val factory = SecureStoreFactory { scope ->
            assertEquals("installation", scope.environmentKey)
            assertEquals("installation", scope.ownerKey)
            store
        }
        val repository = HomeLayoutRepositoryImpl(context, factory)
        assertEquals("QUINA,MEGA_SENA", repository.loadOrder())
        assertFalse(preferences.contains("modality_order"))
        repository.saveOrder("MEGA_SENA,QUINA")
        preferences.edit().putString("modality_order", "QUINA,MEGA_SENA").commit()
        assertEquals("MEGA_SENA,QUINA", HomeLayoutRepositoryImpl(context, factory).loadOrder())
    }

    private class MemoryStore : SecureRecordStore {
        var snapshot = SecureSnapshot(0, emptyMap())
        override suspend fun read() = snapshot
        override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot {
            snapshot = SecureSnapshot(snapshot.revision + 1, transform(snapshot.values))
            return snapshot
        }
    }
}
