package br.gov.caixa.loterias.core.preferences

import android.content.Context
import android.database.sqlite.SQLiteDatabase

internal class AndroidLegacyCartSnapshotStore(private val context: Context) : LegacyCartSnapshotStore {
    override fun read(): Map<String, List<LegacyCartRow>> {
        val file = context.getDatabasePath("Loterias.db")
        if (!file.exists()) return emptyMap()
        // Never use the default corruption handler: it can delete the user's original database.
        return SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY) {
            error("Banco legado indisponível; arquivo preservado")
        }.use { database ->
            // A private read-only handle, no WAL-enabling flags and synchronous access: one
            // connection throughout. data_version must not be compared across connections.
            readLegacyCartSnapshot(SQLiteLegacyCartTableSource(database))
        }
    }
}

internal class SQLiteLegacyCartTableSource(private val database: SQLiteDatabase) : LegacyCartTableSource {
    override fun dataVersion(): Long = database.rawQuery("PRAGMA data_version", null).use {
        check(it.moveToFirst()) { "Versão do snapshot local indisponível" }
        it.getLong(0)
    }

    override fun rows(table: String): List<LegacyCartRow>? {
        val exists = database.rawQuery(
            "SELECT 1 FROM sqlite_master WHERE type='table' AND name=?", arrayOf(table),
        ).use { it.moveToFirst() }
        if (!exists) return null
        return database.query(table, null, null, null, null, null,
            if (table == "tb_mes_sorte") "numero" else "_id").use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(cursor.columnNames.mapIndexed { index, column ->
                        column to if (cursor.isNull(index)) null else cursor.getString(index)
                    }.toMap())
                }
            }
        }
    }
}
