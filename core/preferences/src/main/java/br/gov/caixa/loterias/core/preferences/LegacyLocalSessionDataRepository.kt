package br.gov.caixa.loterias.core.preferences

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.gov.caixa.loterias.core.domain.account.LocalSessionDataRepository
import br.gov.caixa.loterias.core.domain.account.LocalSessionCleanupRecoveryRepository
import java.util.concurrent.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal interface LegacySessionDataStore {
    fun isCleanupPending(): Boolean
    fun setCleanupPending(pending: Boolean)
    fun clearLoginFlag()
    fun clearIdentityAndBiometricRecords()
}

/** Upgrade compatibility with DadosUsuarioBO/DadosBiometriaBO, not a database reset. */
class LegacyLocalSessionDataRepository internal constructor(
    private val store: LegacySessionDataStore,
) : LocalSessionDataRepository, LocalSessionCleanupRecoveryRepository {
    private val mutex = Mutex()

    override suspend fun clear() = mutex.withLock { clearLocked() }

    override suspend fun resumePending(): Boolean = mutex.withLock {
        if (!store.isCleanupPending()) return@withLock false
        clearLocked()
        true
    }

    private fun clearLocked() {
        // Commit before touching either store. Recovery may safely repeat every following step.
        store.setCleanupPending(true)
        var complete = true
        // Attempt both independent stores, even if one cannot be written; retry is idempotent.
        for (clear in listOf(store::clearLoginFlag, store::clearIdentityAndBiometricRecords)) {
            try { clear() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { complete = false }
        }
        check(complete) { "Não foi possível limpar os dados locais da sessão" }
        store.setCleanupPending(false)
    }

    companion object {
        fun fromContext(context: Context): LegacyLocalSessionDataRepository =
            LegacyLocalSessionDataRepository(AndroidLegacySessionDataStore(context.applicationContext))
    }
}

private class AndroidLegacySessionDataStore(private val context: Context) : LegacySessionDataStore {
    override fun isCleanupPending(): Boolean =
        context.getSharedPreferences("SESSION_CLEANUP_STATE", Context.MODE_PRIVATE).getBoolean("pending", false)

    override fun setCleanupPending(pending: Boolean) {
        check(context.getSharedPreferences("SESSION_CLEANUP_STATE", Context.MODE_PRIVATE)
            .edit().putBoolean("pending", pending).commit())
    }

    override fun clearLoginFlag() {
        check(context.getSharedPreferences("USUARIO_KEY", Context.MODE_PRIVATE)
            .edit().putBoolean("usuariologado", false).commit())
    }

    override fun clearIdentityAndBiometricRecords() {
        val file = context.getDatabasePath("Loterias.db")
        if (!file.exists()) return // A new installation needs no legacy database/schema.
        SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READWRITE).use { database ->
            // Limits deleted-record remnants in SQLite pages; not a forensic-erasure guarantee.
            database.rawQuery("PRAGMA secure_delete = ON", null).use {
                check(it.moveToFirst() && it.getInt(0) == 1)
            }
            database.beginTransaction()
            try {
                for (table in listOf("tb_usuario", "tb_biometria")) {
                    val exists = database.rawQuery(
                        "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?", arrayOf(table),
                    ).use { it.moveToFirst() }
                    if (exists) database.delete(table, null, null)
                }
                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }
        }
    }
}
