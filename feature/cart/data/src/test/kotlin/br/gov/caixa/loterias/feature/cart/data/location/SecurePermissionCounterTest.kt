package br.gov.caixa.loterias.feature.cart.data.location

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
class SecurePermissionCounterTest {
    @Test fun concurrentRequestsUseOneBoundedInstallationCounterAfterMigration() = runTest {
        val context = RuntimeEnvironment.getApplication()
        val source = context.getSharedPreferences("CONT_PERMISSION_LOCATION", 0)
        source.edit().putInt("CONT_PERMISSION_LOCATION", NEGATIVE_SOURCE_COUNT).commit()
        val store = MemoryStore()
        val factory = SecureStoreFactory { scope ->
            assertEquals("installation", scope.environmentKey)
            assertEquals("installation", scope.ownerKey)
            store
        }
        val first = AndroidCheckoutLocationSource(context, FixedCoroutineDispatchers(Dispatchers.Unconfined), factory)
        assertEquals(0, first.access().permissionRequestCount)
        assertFalse(source.contains("CONT_PERMISSION_LOCATION"))
        (1..CONCURRENT_REQUESTS).map { async { first.recordPermissionRequest() } }.awaitAll()
        val second = AndroidCheckoutLocationSource(context, FixedCoroutineDispatchers(Dispatchers.Unconfined), factory)
        assertEquals(2, second.access().permissionRequestCount)
    }

    private class MemoryStore : SecureRecordStore {
        private val mutex = Mutex()
        private var snapshot = SecureSnapshot(0, emptyMap())
        override suspend fun read() = mutex.withLock { snapshot }
        override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot = mutex.withLock {
            SecureSnapshot(snapshot.revision + 1, transform(snapshot.values)).also { snapshot = it }
        }
    }
    private companion object {
        const val NEGATIVE_SOURCE_COUNT = -5
        const val CONCURRENT_REQUESTS = 20
    }
}
