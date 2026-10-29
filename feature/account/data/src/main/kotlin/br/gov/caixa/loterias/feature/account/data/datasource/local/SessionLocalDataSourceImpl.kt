package br.gov.caixa.loterias.feature.account.data.datasource.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.core.persistence.SecurePreferenceMigration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class SessionLocalDataSourceImpl(private val context: Context, factory: SecureStoreFactory = AndroidSecureStores(context)) :
    SessionLocalDataSource {
    private val store = SecurePreferenceMigration(
        factory.open(SecureStoreScope("installation", "installation", "session_cleanup")),
        readLegacy = {
            withContext(Dispatchers.IO) {
                val preferences = context.getSharedPreferences("SESSION_CLEANUP_STATE", Context.MODE_PRIVATE)
                if (preferences.contains("pending")) {
                    mapOf("pending" to preferences.getBoolean("pending", false).toString())
                } else {
                    emptyMap()
                }
            }
        },
        clearLegacy = {
            withContext(Dispatchers.IO) {
                val preferences = context.getSharedPreferences("SESSION_CLEANUP_STATE", Context.MODE_PRIVATE)
                if (preferences.contains("pending") && !preferences.edit().remove("pending").commit()) {
                    throw SecureStorageException.WriteFailure()
                }
            }
        }
    )

    override suspend fun isCleanupPending(): Boolean {
        val value = store.read().values["pending"] ?: return false
        return value.toBooleanStrictOrNull() ?: throw SecureStorageException.InvalidRecord()
    }

    override suspend fun setCleanupPending(pending: Boolean) {
        store.update { it + ("pending" to pending.toString()) }
    }

    override suspend fun clearLoginFlag() {
        store.update { it + ("login" to "false") }
        if (store.read().values["login"] != "false") throw SecureStorageException.ReadFailure()
        withContext(Dispatchers.IO) {
            val preferences = context.getSharedPreferences("USUARIO_KEY", Context.MODE_PRIVATE)
            if (preferences.contains("usuariologado") && !preferences.edit().remove("usuariologado").commit()) {
                throw SecureStorageException.WriteFailure()
            }
        }
    }

    override suspend fun clearIdentityAndBiometricRecords() = withContext(Dispatchers.IO) {
        val file = context.getDatabasePath("Loterias.db")
        if (!file.exists()) return@withContext // Uma instalação nova não precisa de banco de dados ou esquema legado.
        SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READWRITE).use { database ->
            // Limita resíduos de registros excluídos nas páginas SQLite, sem garantir apagamento forense.
            database.rawQuery("PRAGMA secure_delete = ON", null).use {
                check(it.moveToFirst() && it.getInt(0) == 1)
            }
            database.beginTransaction()
            try {
                for (table in listOf("tb_usuario", "tb_biometria")) {
                    val exists = database.rawQuery(
                        "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?",
                        arrayOf(table)
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
