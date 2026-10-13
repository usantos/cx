package br.gov.caixa.loterias.core.preferences

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/** Only uses a unique test-owned directory/preferences, never the installed app's user records. */
class LegacyLocalSessionStorageTest {
    private lateinit var target: Context
    private lateinit var context: Context
    private lateinit var directory: File
    private val prefix = "local-session-test-${UUID.randomUUID()}-"

    @Before fun setUp() {
        target = InstrumentationRegistry.getInstrumentation().targetContext
        directory = File(target.cacheDir, prefix)
        check(directory.mkdirs())
        context = object : ContextWrapper(target) {
            override fun getApplicationContext(): Context = this
            override fun getDatabasePath(name: String) = File(directory, name)
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences =
                target.getSharedPreferences(prefix + name, mode)
        }
    }

    @After fun tearDown() {
        check(directory.deleteRecursively())
        target.deleteSharedPreferences(prefix + "USUARIO_KEY")
        target.deleteSharedPreferences(prefix + "INTRODUCAO_KEY")
        target.deleteSharedPreferences(prefix + "SESSION_CLEANUP_STATE")
    }

    private fun database() = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath("Loterias.db"), null)
    private fun count(table: String): Int = database().use { database ->
        database.rawQuery("SELECT COUNT(*) FROM $table", null).use { it.moveToFirst(); it.getInt(0) }
    }

    private fun seed() {
        database().use {
            it.version = 49
            it.execSQL("CREATE TABLE tb_usuario (id INTEGER PRIMARY KEY, nome TEXT, cpf TEXT)")
            it.execSQL("CREATE TABLE tb_biometria (id INTEGER PRIMARY KEY, refresh TEXT, nome TEXT, cpf TEXT)")
            it.execSQL("CREATE TABLE tb_ultima_compra (valor REAL, data TEXT)")
            it.execSQL("CREATE TABLE unrelated (value TEXT)")
            it.execSQL("INSERT INTO tb_usuario VALUES (1, 'test-name', 'test-cpf')")
            it.execSQL("INSERT INTO tb_biometria VALUES (1, 'test-refresh', 'test-name', 'test-cpf')")
            it.execSQL("INSERT INTO tb_ultima_compra VALUES (20, '07/09/2026 12:00')")
            it.execSQL("INSERT INTO unrelated VALUES ('keep')")
        }
        check(context.getSharedPreferences("USUARIO_KEY", 0).edit()
            .putBoolean("usuariologado", true).putString("unrelated", "keep").commit())
        check(context.getSharedPreferences("INTRODUCAO_KEY", 0).edit().putBoolean("INTRODUCAO_KEY", true).commit())
    }

    @Test fun clearsOnlyLegacyIdentityAndBiometricsAndLoginFlagAcrossRecreation() = runBlocking {
        seed()
        LegacyLocalSessionDataRepository.fromContext(context).clear()
        LegacyLocalSessionDataRepository.fromContext(context).clear()
        assertEquals(0, count("tb_usuario"))
        assertEquals(0, count("tb_biometria"))
        assertEquals(1, count("tb_ultima_compra"))
        assertEquals(1, count("unrelated"))
        database().use { assertEquals(49, it.version) }
        val preferences = context.getSharedPreferences("USUARIO_KEY", 0)
        assertFalse(preferences.getBoolean("usuariologado", true))
        assertFalse(context.getSharedPreferences("SESSION_CLEANUP_STATE", 0).getBoolean("pending", true))
        assertEquals("keep", preferences.getString("unrelated", null))
        assertTrue(context.getSharedPreferences("INTRODUCAO_KEY", 0).getBoolean("INTRODUCAO_KEY", false))
    }

    @Test fun freshInstallationDoesNotCreateDatabaseAndMissingTablesAreAccepted() = runBlocking {
        LegacyLocalSessionDataRepository.fromContext(context).clear()
        assertFalse(context.getDatabasePath("Loterias.db").exists())
        database().use {
            it.version = 7
            it.execSQL("CREATE TABLE unrelated (value TEXT)")
            it.execSQL("INSERT INTO unrelated VALUES ('keep')")
        }
        LegacyLocalSessionDataRepository.fromContext(context).clear()
        assertEquals(1, count("unrelated"))
        database().use {
            assertEquals(7, it.version)
            it.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE name IN ('tb_usuario', 'tb_biometria')", null).use { cursor ->
                assertTrue(cursor.moveToFirst()); assertEquals(0, cursor.getInt(0))
            }
        }
    }

    @Test fun deleteFailureRollsBackBothTablesReportsFailureAndRetrySucceeds() = runBlocking {
        seed()
        database().use {
            it.execSQL("CREATE TRIGGER reject_biometric_delete BEFORE DELETE ON tb_biometria BEGIN SELECT RAISE(ABORT, 'test failure'); END")
        }
        try { LegacyLocalSessionDataRepository.fromContext(context).clear(); fail("Must reject incomplete cleanup") }
        catch (_: IllegalStateException) { }
        assertEquals(1, count("tb_usuario"))
        assertEquals(1, count("tb_biometria"))
        assertFalse(context.getSharedPreferences("USUARIO_KEY", 0).getBoolean("usuariologado", true))
        assertTrue(context.getSharedPreferences("SESSION_CLEANUP_STATE", 0).getBoolean("pending", false))
        database().use { it.execSQL("DROP TRIGGER reject_biometric_delete") }
        assertTrue(LegacyLocalSessionDataRepository.fromContext(context).resumePending())
        assertFalse(LegacyLocalSessionDataRepository.fromContext(context).resumePending())
        assertEquals(0, count("tb_usuario"))
        assertEquals(0, count("tb_biometria"))
        assertEquals(1, count("tb_ultima_compra"))
    }

    @Test fun preferencesFailureStillAttemptsCredentialsButNeverReportsComplete() = runBlocking {
        seed()
        val failing = object : ContextWrapper(context) {
            override fun getApplicationContext(): Context = this
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences =
                if (name == "USUARIO_KEY") error("test preferences failure") else super.getSharedPreferences(name, mode)
        }
        try { LegacyLocalSessionDataRepository.fromContext(failing).clear(); fail("Must reject incomplete cleanup") }
        catch (_: IllegalStateException) { }
        assertEquals(0, count("tb_usuario"))
        assertEquals(0, count("tb_biometria"))
        assertEquals(1, count("unrelated"))
        LegacyLocalSessionDataRepository.fromContext(context).clear()
        assertFalse(context.getSharedPreferences("USUARIO_KEY", 0).getBoolean("usuariologado", true))
    }

    @Test fun entryWithoutPendingMarkerDoesNotEraseLegacyIdentityOrCreateDatabase() = runBlocking {
        assertFalse(LegacyLocalSessionDataRepository.fromContext(context).resumePending())
        assertFalse(context.getDatabasePath("Loterias.db").exists())
        seed()
        assertFalse(LegacyLocalSessionDataRepository.fromContext(context).resumePending())
        assertEquals(1, count("tb_usuario"))
        assertEquals(1, count("tb_biometria"))
        assertTrue(context.getSharedPreferences("USUARIO_KEY", 0).getBoolean("usuariologado", false))
    }

    @Test fun invalidMarkerTypeIsReportedInsteadOfReleasingEntry() = runBlocking {
        seed()
        check(context.getSharedPreferences("SESSION_CLEANUP_STATE", 0).edit().putString("pending", "invalid").commit())
        try { LegacyLocalSessionDataRepository.fromContext(context).resumePending(); fail("Must reject unreadable marker") }
        catch (_: ClassCastException) { }
        assertEquals(1, count("tb_usuario"))
        assertEquals(1, count("tb_biometria"))
    }
}
