package br.gov.caixa.loterias.feature.notifications.data.push

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.notifications.data.repository.PushRegistrationLocalRepositoryImpl
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
class PushEncryptedRegistrationTest {
    @Test fun markersAreBoundToCapturedOwnerAndEnvironmentWithoutGuessingLegacyOwnership() = runTest {
        val context = RuntimeEnvironment.getApplication()
        val marker = "c".repeat(DIGEST_WIDTH)
        context.getSharedPreferences("push_registration", 0).edit().putString("confirmed", marker).commit()
        val first = AuthenticatedSessionIdentity("a".repeat(DIGEST_WIDTH))
        val second = AuthenticatedSessionIdentity("b".repeat(DIGEST_WIDTH))
        var current = first
        var environment = "d".repeat(DIGEST_WIDTH)
        val stores = mutableMapOf<SecureStoreScope, MemoryStore>()
        val factory = SecureStoreFactory { stores.getOrPut(it) { MemoryStore() } }
        val repository = PushRegistrationLocalRepositoryImpl(context, { environment }, { it === current }, factory)
        assertFalse(repository.contains(first, marker))
        assertTrue(context.getSharedPreferences("push_registration", 0).contains("confirmed"))
        repository.remember(first, marker)
        assertTrue(repository.contains(first, marker))
        current = second
        assertFalse(repository.contains(second, marker))
        current = first
        environment = "e".repeat(DIGEST_WIDTH)
        assertFalse(repository.contains(first, marker))
    }

    private class MemoryStore : SecureRecordStore {
        var snapshot = SecureSnapshot(0, emptyMap())
        override suspend fun read() = snapshot
        override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot {
            snapshot = SecureSnapshot(snapshot.revision + 1, transform(snapshot.values))
            return snapshot
        }
    }
    private companion object {
        const val DIGEST_WIDTH = 64
    }
}
