package br.gov.caixa.loterias.core.preferences

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/** Runs against Android SQLite, isolated from the application's real database. */
class LegacyRecentPurchaseStorageTest {
    private lateinit var directory: File
    private lateinit var context: Context
    private val amount = BigDecimal("30.50")
    private val now = Instant.parse("2026-09-07T12:00:00Z")

    @Before fun setUp() {
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        directory = File(target.cacheDir, "recent-purchase-test-${UUID.randomUUID()}")
        check(directory.mkdirs())
        context = object : ContextWrapper(target) {
            override fun getApplicationContext(): Context = this
            override fun getDatabasePath(name: String): File = File(directory, name)
            override fun openOrCreateDatabase(name: String, mode: Int, factory: SQLiteDatabase.CursorFactory?): SQLiteDatabase =
                SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory)
        }
    }

    @After fun tearDown() {
        // Only the unique test-owned directory; never the target application's databases directory.
        check(directory.deleteRecursively())
    }

    @Test fun missingDatabaseIsNotCreatedByReadAndNewRecordsSurviveRecreation() = runBlocking {
        val repository = LegacyRecentPurchaseRepository.fromContext(context)
        assertNull(repository.latestPurchaseAt(amount))
        assertFalse(context.getDatabasePath("Loterias.db").exists())
        repository.record(amount, now)
        val recreated = LegacyRecentPurchaseRepository.fromContext(context)
        assertEquals(now, recreated.latestPurchaseAt(BigDecimal("30.500")))
        assertNull(recreated.latestPurchaseAt(BigDecimal("30.51")))
    }

    @Test fun existingDatabaseVersionAndUnrelatedTablesAndAmountsArePreserved() = runBlocking {
        context.openOrCreateDatabase("Loterias.db", 0, null).use { database ->
            database.version = 49
            database.execSQL("CREATE TABLE unrelated (value TEXT)")
            database.execSQL("INSERT INTO unrelated VALUES ('keep')")
        }
        val repository = LegacyRecentPurchaseRepository.fromContext(context)
        assertNull(repository.latestPurchaseAt(amount)) // Database exists without this optional table.
        repository.record(amount, now)
        repository.record(BigDecimal("40"), now)
        repository.record(amount, now.plusSeconds(60))
        assertEquals(now.plusSeconds(60), repository.latestPurchaseAt(amount))
        assertEquals(now, repository.latestPurchaseAt(BigDecimal("40.00")))
        context.openOrCreateDatabase("Loterias.db", 0, null).use { database ->
            assertEquals(49, database.version)
            database.rawQuery("SELECT value FROM unrelated", null).use {
                assertTrue(it.moveToFirst()); assertEquals("keep", it.getString(0))
            }
            database.rawQuery("SELECT COUNT(*) FROM tb_ultima_compra", null).use {
                assertTrue(it.moveToFirst()); assertEquals(2, it.getInt(0))
            }
        }
    }

    @Test fun readsLegacyRowsAndRollsBackReplacementWhenInsertFails() = runBlocking {
        val date = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").format(now.atZone(ZoneId.systemDefault()))
        context.openOrCreateDatabase("Loterias.db", 0, null).use { database ->
            database.execSQL("CREATE TABLE tb_ultima_compra (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, valor REAL, data TEXT)")
            database.execSQL("INSERT INTO tb_ultima_compra (valor, data) VALUES (?, ?)", arrayOf(30.5, date))
            database.execSQL("INSERT INTO tb_ultima_compra (valor, data) VALUES (30.5, NULL)")
            database.execSQL("CREATE TRIGGER fail_insert BEFORE INSERT ON tb_ultima_compra BEGIN SELECT RAISE(ABORT, 'test write failure'); END")
        }
        val repository = LegacyRecentPurchaseRepository.fromContext(context)
        assertEquals(now, repository.latestPurchaseAt(amount))
        try {
            repository.record(amount, now.plusSeconds(60))
            fail("The forced insert failure must be reported")
        } catch (_: SQLiteException) { }
        assertEquals(now, repository.latestPurchaseAt(amount))
    }
}
