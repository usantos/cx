package br.gov.caixa.loterias.feature.cart.data.datasource.local

import android.content.Context
import android.content.ContextWrapper
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.feature.cart.data.repository.RecentPurchaseRepositoryImpl
import java.io.File
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RecentPurchaseStorageTest {
    private lateinit var directory: File
    private lateinit var context: Context
    private val amount = BigDecimal("30.50")
    private val now = Instant.parse("2026-09-07T12:00:00Z")
    private val first = SecureStoreScope("installation", "a".repeat(OPAQUE_KEY_LENGTH), "account-cart-fixture")
    private val second = first.copy(ownerKey = "b".repeat(OPAQUE_KEY_LENGTH))

    @Before fun setUp() {
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        directory = File(target.cacheDir, "recent-purchase-test-${UUID.randomUUID()}")
        check(directory.mkdirs())
        context = object : ContextWrapper(target) {
            override fun getApplicationContext(): Context = this
            override fun getDatabasePath(name: String) = File(directory, name)
            override fun getNoBackupFilesDir() = File(directory, "private").apply { mkdirs() }
        }
    }

    @After fun tearDown() {
        check(directory.deleteRecursively())
    }

    @Test fun encryptedPurchaseRecordsSurviveRecreationAndNormalizeAmounts() = runBlocking {
        val repository = RecentPurchaseRepositoryImpl.fromContext(context) { first }
        assertNull(repository.latestPurchaseAt(amount))
        repository.record(amount, now)
        val recreated = RecentPurchaseRepositoryImpl.fromContext(context) { first }
        assertEquals(now, recreated.latestPurchaseAt(BigDecimal("30.500")))
        repository.record(BigDecimal("40"), now)
        repository.record(amount, now.plusSeconds(PURCHASE_INTERVAL_SECONDS))
        assertEquals(now.plusSeconds(PURCHASE_INTERVAL_SECONDS), recreated.latestPurchaseAt(amount))
        assertEquals(now, recreated.latestPurchaseAt(BigDecimal("40.00")))
        assertFalse(context.getDatabasePath("Loterias.db").exists())
    }

    @Test fun accountAndEnvironmentChangesCannotReadAnotherOwnersHistory() = runBlocking {
        var scope = first
        val repository = RecentPurchaseRepositoryImpl.fromContext(context) { scope }
        repository.record(amount, now)
        scope = second
        assertNull(repository.latestPurchaseAt(amount))
        repository.record(amount, now.plusSeconds(PURCHASE_INTERVAL_SECONDS))
        scope = first.copy(environmentKey = "c".repeat(OPAQUE_KEY_LENGTH))
        assertNull(repository.latestPurchaseAt(amount))
        scope = first
        assertEquals(now, repository.latestPurchaseAt(amount))
        val state = SecureCartDocument(AndroidSecureStores(context).open(first)).read()
        assertEquals(setOf("30.5"), state.purchases.keys)
    }
}

private const val PURCHASE_INTERVAL_SECONDS = 60L

private const val OPAQUE_KEY_LENGTH = 64
