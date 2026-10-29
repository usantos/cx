package br.gov.caixa.loterias.feature.cart.data.datasource.local

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureCartAccessTest {
    @Test fun `same owner reauthentication invalidates suspended private record publication`() = runTest {
        val scope = SecureStoreScope("installation", "a".repeat(OWNER_KEY_LENGTH), "account-cart-v1")
        var lease: Any = Any()
        val disk = CartMemorySecureStore()
        val changingStore = object : SecureRecordStore {
            override suspend fun read(): SecureSnapshot = disk.read().also { lease = Any() }
            override suspend fun update(transform: (Map<String, String>) -> Map<String, String>) = disk.update(transform)
        }
        val access = SecureCartAccess(SecureStoreFactory { changingStore }, { scope }, captureLease = {
            val captured = lease;
            {
                lease ===
                    captured
            }
        })
        assertTrue(runCatching { access.use { it.read() } }.exceptionOrNull() is CancellationException)
    }

    @Test fun `owner change during migration cannot apply a private mutation`() = runTest {
        var scope = SecureStoreScope("installation", "a".repeat(OWNER_KEY_LENGTH), "account-cart-v1")
        val disk = CartMemorySecureStore()
        val access = SecureCartAccess(SecureStoreFactory { disk }, { scope }, beforeAccess = {
            scope = scope.copy(ownerKey = "b".repeat(OWNER_KEY_LENGTH))
        })
        assertTrue(
            runCatching {
                access.use { it.transaction { updateFavorites { setOf(FAVORITE_ID) } } }
            }.exceptionOrNull() is CancellationException
        )
        assertTrue(disk.snapshot.values.isEmpty())
    }
}

private const val OWNER_KEY_LENGTH = 64
private const val FAVORITE_ID = 7L
