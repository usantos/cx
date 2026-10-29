package br.gov.caixa.loterias.feature.cart.data.datasource.local

import android.content.Context
import android.content.ContextWrapper
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE
import br.gov.caixa.loterias.feature.cart.data.repository.CartTransferRepositoryImpl
import br.gov.caixa.loterias.feature.cart.domain.model.CartCleanupResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartTransferPreparation
import java.io.File
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Apenas registros sintéticos; use uma instalação isolada de teste. */
class CartTransferStorageTest {
    private lateinit var context: Context
    private lateinit var directory: File
    private val owner = "a".repeat(64)
    private val scope = SecureStoreScope("installation", "guest", "cart-transfer-fixture")
    private fun document() = SecureCartDocument(AndroidSecureStores(context).open(scope))
    private fun repository() = CartTransferRepositoryImpl(document())

    @Before fun setUp() {
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        directory = File(target.cacheDir, "cart-transfer-test-${UUID.randomUUID()}")
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
    private suspend fun seed() = document().transaction {
        replaceTables(
            mapOf(
                LEGACY_CART_TABLE to listOf(
                    mapOf("_id" to "1", "modalidade" to "mega-sena", "numeros_selecionados" to "[1,2,3,4,5,6]"),
                    mapOf("_id" to "2", "modalidade" to "quina", "numeros_selecionados" to "[1,2,3,4,5]")
                )
            )
        )
        saveLastId(2)
    }

    @Test fun receiptSurvivesRecreationWithoutReissuingUncertainUploadAndCiphertextHidesSelection() = runBlocking {
        seed()
        val ready = repository().prepare(owner) as CartTransferPreparation.Ready
        assertEquals(2, ready.bets.size)
        assertEquals(CartTransferPreparation.RequiresReview, repository().prepare(owner))
        assertFalse(repository().confirmReceived("b".repeat(64), ready.transferId))
        assertTrue(repository().confirmReceived(owner, ready.transferId))
        assertEquals(CartTransferPreparation.CleanupPending(ready.transferId), repository().prepare(owner))
        assertEquals(CartCleanupResult.Complete, repository().finishConfirmed(owner, ready.transferId))
        assertTrue(document().read().tables.getValue(LEGACY_CART_TABLE).isEmpty())
        assertNull(document().read().receipt)
        val files = context.noBackupFilesDir.walkTopDown().filter { it.isFile }.toList()
        assertTrue(files.isNotEmpty())
        files.forEach { assertFalse(it.readText(Charsets.ISO_8859_1).contains("[1,2,3,4,5,6]")) }
        assertFalse(context.getDatabasePath("Loterias.db").exists())
    }

    @Test fun changedGraphIsPreservedAsConflictAcrossRecreation() = runBlocking {
        seed()
        val ready = repository().prepare(owner) as CartTransferPreparation.Ready
        repository().confirmReceived(owner, ready.transferId)
        document().transaction {
            replaceTables(
                tables().mapValues { (_, rows) ->
                    rows.map { row ->
                        if (row["_id"] == "1") row + ("numeros_selecionados" to "[2,3,4,5,6,7]") else row
                    }
                }
            )
        }
        assertEquals(CartCleanupResult.Conflict(setOf(1L)), repository().finishConfirmed(owner, ready.transferId))
        assertEquals(CartTransferPreparation.RequiresReview, repository().prepare(owner))
        assertEquals(listOf("1"), document().read().tables.getValue(LEGACY_CART_TABLE).map { it["_id"] })
    }

    @Test fun unknownLegacyEnvironmentRequiresReviewAndDoesNotGrantUpload() = runBlocking {
        android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("Loterias.db"), null).use {
            it.execSQL("CREATE TABLE $LEGACY_CART_TABLE (_id INTEGER PRIMARY KEY,modalidade TEXT,numeros_selecionados TEXT)")
            it.execSQL("INSERT INTO $LEGACY_CART_TABLE VALUES(1,'mega-sena','[1,2,3,4,5,6]')")
        }
        assertEquals(CartTransferPreparation.RequiresReview, CartTransferRepositoryImpl.fromContext(context, scope).prepare(owner))
        assertTrue(document().read().tables.isEmpty())
        val quarantine =
            SecureCartDocument(AndroidSecureStores(context).open(SecureStoreScope("installation", "installation", "cart-legacy-v1")))
        assertTrue(quarantine.requiresLegacyReview())
        assertEquals(1, quarantine.read().tables.getValue(LEGACY_CART_TABLE).size)
    }
}
