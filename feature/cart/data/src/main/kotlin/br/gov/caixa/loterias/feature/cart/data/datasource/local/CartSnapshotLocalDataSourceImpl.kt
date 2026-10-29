package br.gov.caixa.loterias.feature.cart.data.datasource.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase

internal class CartSnapshotLocalDataSourceImpl(private val context: Context) : CartSnapshotLocalDataSource {
    override suspend fun read(): Map<String, List<CartSnapshotRow>> {
        val file = context.getDatabasePath("Loterias.db")
        if (!file.exists()) return emptyMap()
        // Nunca usa o tratador padrão de corrupção: ele pode excluir o banco de dados original do usuário.
        return SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY) {
            error("Banco legado indisponível; arquivo preservado")
        }.use { database ->
            // Um acesso privado somente para leitura, sem opções de ativação de WAL e com acesso síncrono: uma
            // única conexão durante toda a operação. data_version não deve ser comparado entre conexões.
            readLegacyCartSnapshot(CartTableDataSourceImpl(database))
        }
    }
}

internal class CartTableDataSourceImpl(private val database: SQLiteDatabase) : CartTableDataSource {
    override fun dataVersion(): Long = database.rawQuery("PRAGMA data_version", null).use {
        check(it.moveToFirst()) { "Versão do snapshot local indisponível" }
        it.getLong(0)
    }

    override fun rows(table: String): List<CartSnapshotRow>? {
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
