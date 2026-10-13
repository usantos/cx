package br.gov.caixa.loterias.core.preferences

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.domain.cart.LegacyCartCleanupResult
import br.gov.caixa.loterias.core.domain.cart.LegacyCartTransferPreparation
import java.io.File
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LegacyCartTransferStorageTest {
    private lateinit var context: Context
    private lateinit var directory: File
    private val owner = "a".repeat(64)
    private fun repository() = LegacyCartTransferLocalRepository.fromContext(context)
    private fun database() = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("Loterias.db"), null)

    @Before fun setUp() {
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        directory = File(target.cacheDir, "cart-transfer-test-${UUID.randomUUID()}")
        check(directory.mkdirs())
        context = object : ContextWrapper(target) {
            override fun getApplicationContext(): Context = this
            override fun getDatabasePath(name: String) = File(directory, name)
        }
    }
    @After fun tearDown() { check(directory.deleteRecursively()) }

    private fun seed() = database().use {
        it.version = 49
        it.execSQL("CREATE TABLE tb_identificao_de_uma_aposta_das_8_modalidades (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, modalidade TEXT, numeros_selecionados TEXT)")
        it.execSQL("INSERT INTO tb_identificao_de_uma_aposta_das_8_modalidades VALUES (1, 'mega-sena', '[1,2,3,4,5,6]'), (2, 'quina', '[1,2,3,4,5]')")
        it.execSQL("CREATE TABLE tb_partida_loteca (_id INTEGER PRIMARY KEY, id_identificao_de_uma_aposta_das_8_modalidades INTEGER, id_equipe_um INTEGER, id_equipe_dois INTEGER)")
        it.execSQL("INSERT INTO tb_partida_loteca VALUES (1, 1, 20, 21), (2, 99, 20, 21)")
        it.execSQL("CREATE TABLE tb_equipe (_id INTEGER PRIMARY KEY, nome TEXT)")
        it.execSQL("INSERT INTO tb_equipe VALUES (20, 'Primeiro'), (21, 'Segundo')")
        it.execSQL("CREATE TABLE tb_usuario (_id INTEGER, nome TEXT)")
        it.execSQL("INSERT INTO tb_usuario VALUES (1, 'preservar')")
    }

    private fun count(table: String) = database().use { db ->
        db.rawQuery("SELECT COUNT(*) FROM $table", null).use { it.moveToFirst(); it.getInt(0) }
    }

    @Test fun noDatabaseIsCreatedAndCorruptDatabaseIsNotDeleted() = runBlocking {
        assertEquals(LegacyCartTransferPreparation.Empty, repository().prepare(owner))
        assertFalse(repository().confirmReceived(owner, "missing"))
        assertTrue(runCatching { repository().finishConfirmed(owner, "missing") }.isFailure)
        val file = context.getDatabasePath("Loterias.db")
        assertFalse(file.exists())
        val invalid = ByteArray(4096) { 65 }
        file.writeBytes(invalid)
        assertTrue(runCatching { repository().prepare(owner) }.isFailure)
        assertArrayEquals(invalid, file.readBytes())
    }

    @Test fun receiptSurvivesRecreationAndCleanupPreservesNewBetsSchemaAndSharedData() = runBlocking {
        seed()
        val ready = repository().prepare(owner) as LegacyCartTransferPreparation.Ready
        assertEquals(2, ready.bets.size)
        assertEquals(LegacyCartTransferPreparation.RequiresReview, repository().prepare(owner))
        assertFalse(repository().confirmReceived("b".repeat(64), ready.transferId))
        assertTrue(runCatching { repository().finishConfirmed(owner, ready.transferId) }.isFailure)
        assertTrue(repository().confirmReceived(owner, ready.transferId))
        assertTrue(repository().confirmReceived(owner, ready.transferId))
        assertEquals(LegacyCartTransferPreparation.CleanupPending(ready.transferId), repository().prepare(owner))
        database().use { it.execSQL("INSERT INTO tb_identificao_de_uma_aposta_das_8_modalidades (modalidade, numeros_selecionados) VALUES ('quina', '[2,3,4,5,6]')") }
        assertEquals(LegacyCartCleanupResult.Complete, repository().finishConfirmed(owner, ready.transferId))
        assertEquals(1, count("tb_identificao_de_uma_aposta_das_8_modalidades"))
        assertEquals(1, count("tb_partida_loteca"))
        assertEquals(2, count("tb_equipe")); assertEquals(1, count("tb_usuario"))
        assertEquals(0, count("compose_cart_transfer_receipt_v1")); assertEquals(0, count("compose_cart_transfer_items_v1"))
        database().use { assertEquals(49, it.version) }
        val next = repository().prepare(owner) as LegacyCartTransferPreparation.Ready
        assertEquals(listOf(3L), next.bets.map { it.localId })
        assertNotEquals(ready.transferId, next.transferId)
    }

    @Test fun changedTeamIsPreservedWithItsBetAndRequiresReviewAcrossRecreation() = runBlocking {
        seed()
        val ready = repository().prepare(owner) as LegacyCartTransferPreparation.Ready
        assertTrue(repository().confirmReceived(owner, ready.transferId))
        database().use { it.execSQL("UPDATE tb_equipe SET nome='Alterado' WHERE _id=20") }
        assertEquals(LegacyCartCleanupResult.Conflict(setOf(1L)), repository().finishConfirmed(owner, ready.transferId))
        assertEquals(1, count("tb_identificao_de_uma_aposta_das_8_modalidades"))
        assertEquals(2, count("tb_partida_loteca")); assertEquals(2, count("tb_equipe"))
        assertEquals(LegacyCartTransferPreparation.RequiresReview, repository().prepare(owner))
        assertFalse(repository().confirmReceived(owner, ready.transferId))
    }

    @Test fun failedDeleteRollsBackEntireCleanupIncludingReceiptAndCanResumeWithoutAnotherUpload() = runBlocking {
        seed()
        val ready = repository().prepare(owner) as LegacyCartTransferPreparation.Ready
        repository().confirmReceived(owner, ready.transferId)
        database().use { it.execSQL("CREATE TRIGGER test_reject_delete BEFORE DELETE ON tb_identificao_de_uma_aposta_das_8_modalidades WHEN OLD._id=2 BEGIN SELECT RAISE(ABORT, 'test failure'); END") }
        assertTrue(runCatching { repository().finishConfirmed(owner, ready.transferId) }.isFailure)
        assertEquals(2, count("tb_identificao_de_uma_aposta_das_8_modalidades"))
        assertEquals(2, count("tb_partida_loteca"))
        assertEquals(LegacyCartTransferPreparation.CleanupPending(ready.transferId), repository().prepare(owner))
        database().use { it.execSQL("DROP TRIGGER test_reject_delete") }
        assertEquals(LegacyCartCleanupResult.Complete, repository().finishConfirmed(owner, ready.transferId))
    }

    @Test fun unknownReceiptVersionAndOrphanItemsFailClosedWithoutReplacingReceipt() = runBlocking {
        seed()
        repository().prepare(owner)
        database().use { it.execSQL("UPDATE compose_cart_transfer_receipt_v1 SET encoding_version=2") }
        assertTrue(runCatching { repository().prepare(owner) }.isFailure)
        assertEquals(2, count("tb_identificao_de_uma_aposta_das_8_modalidades"))
        database().use { it.execSQL("UPDATE compose_cart_transfer_receipt_v1 SET encoding_version=1, phase='UNKNOWN'") }
        assertTrue(runCatching { repository().prepare(owner) }.isFailure)
        database().use { it.execSQL("DELETE FROM compose_cart_transfer_receipt_v1") }
        assertTrue(runCatching { repository().prepare(owner) }.isFailure)
        assertEquals(2, count("compose_cart_transfer_items_v1"))
    }

    @Test fun concurrentIndependentRepositoriesIssueOnlyOneUploadPermit() {
        seed()
        val executor = Executors.newFixedThreadPool(2)
        try {
            val futures = (1..2).map { executor.submit<LegacyCartTransferPreparation> { runBlocking { repository().prepare(owner) } } }
            val results = futures.map { it.get(10, TimeUnit.SECONDS) }
            assertEquals(1, results.count { it is LegacyCartTransferPreparation.Ready })
            assertEquals(1, results.count { it == LegacyCartTransferPreparation.RequiresReview })
        } finally { executor.shutdownNow() }
    }
}
