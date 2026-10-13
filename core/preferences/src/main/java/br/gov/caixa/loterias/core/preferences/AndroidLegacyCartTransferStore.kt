package br.gov.caixa.loterias.core.preferences

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

internal class AndroidLegacyCartTransferStore(private val context: Context) : LegacyCartTransferStore {
    override fun <T> transaction(action: (LegacyCartTransferTransaction) -> T): T {
        val file = context.getDatabasePath("Loterias.db")
        if (!file.exists()) return action(AbsentLegacyCartTransaction)
        return SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READWRITE) {
            error("Banco legado indisponível; arquivo preservado")
        }.use { database ->
            database.beginTransaction()
            try {
                val result = action(SQLiteLegacyCartTransferTransaction(database))
                database.setTransactionSuccessful()
                result
            } finally {
                database.endTransaction()
            }
        }
    }
}

private object AbsentLegacyCartTransaction : LegacyCartTransferTransaction {
    override fun receipt(): LegacyCartReceipt? = null
    override fun tables(): Map<String, List<LegacyCartRow>> = emptyMap()
    override fun save(receipt: LegacyCartReceipt): Unit = error("Banco do carrinho ausente")
    override fun deleteBet(id: Long): Unit = error("Banco do carrinho ausente")
    override fun clearReceipt(): Unit = error("Banco do carrinho ausente")
}

private const val RECEIPT = "compose_cart_transfer_receipt_v1"
private const val ITEMS = "compose_cart_transfer_items_v1"

private class SQLiteLegacyCartTransferTransaction(private val database: SQLiteDatabase) : LegacyCartTransferTransaction {
    private fun exists(table: String) = database.rawQuery(
        "SELECT 1 FROM sqlite_master WHERE type='table' AND name=?", arrayOf(table),
    ).use { it.moveToFirst() }

    override fun receipt(): LegacyCartReceipt? {
        val hasReceipt = exists(RECEIPT)
        check(hasReceipt == exists(ITEMS)) { "Estrutura de recibo incompleta" }
        if (!hasReceipt) return null
        val fingerprints = database.rawQuery("SELECT local_id, fingerprint FROM $ITEMS ORDER BY local_id", null).use { cursor ->
            buildMap {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(0)
                    val fingerprint = checkNotNull(cursor.getString(1))
                    check(id > 0 && fingerprint.matches(Regex("[0-9a-f]{64}")) && !containsKey(id)) { "Item de recibo inválido" }
                    put(id, fingerprint)
                }
            }
        }
        return database.rawQuery("SELECT slot, transfer_id, owner_key, phase, encoding_version FROM $RECEIPT", null).use { cursor ->
            if (!cursor.moveToFirst()) {
                check(fingerprints.isEmpty()) { "Itens sem recibo de transferência" }
                return@use null
            }
            check(cursor.count == 1 && cursor.getInt(0) == 1 && cursor.getInt(4) == 1 && fingerprints.isNotEmpty()) { "Recibo de transferência inválido" }
            val id = checkNotNull(cursor.getString(1)).also { check(it.isNotBlank()) }
            val owner = checkNotNull(cursor.getString(2)).also { check(it.matches(Regex("[0-9a-f]{64}"))) }
            LegacyCartReceipt(id, owner, LegacyCartReceiptPhase.valueOf(checkNotNull(cursor.getString(3))), fingerprints)
        }
    }

    override fun tables() = readLegacyCartSnapshot(SQLiteLegacyCartTableSource(database))

    override fun save(receipt: LegacyCartReceipt) {
        check(receipt.transferId.isNotBlank() && receipt.ownerKey.matches(Regex("[0-9a-f]{64}")) && receipt.fingerprints.isNotEmpty())
        check(receipt.fingerprints.all { (id, hash) -> id > 0 && hash.matches(Regex("[0-9a-f]{64}")) })
        database.execSQL("CREATE TABLE IF NOT EXISTS $RECEIPT (slot INTEGER PRIMARY KEY CHECK(slot=1), transfer_id TEXT NOT NULL, owner_key TEXT NOT NULL, phase TEXT NOT NULL, encoding_version INTEGER NOT NULL)")
        database.execSQL("CREATE TABLE IF NOT EXISTS $ITEMS (local_id INTEGER PRIMARY KEY, fingerprint TEXT NOT NULL)")
        database.delete(ITEMS, null, null)
        database.delete(RECEIPT, null, null)
        database.insertOrThrow(RECEIPT, null, ContentValues().apply {
            put("slot", 1); put("transfer_id", receipt.transferId); put("owner_key", receipt.ownerKey)
            put("phase", receipt.phase.name); put("encoding_version", 1)
        })
        for ((id, hash) in receipt.fingerprints) {
            database.insertOrThrow(ITEMS, null, ContentValues().apply { put("local_id", id); put("fingerprint", hash) })
        }
    }

    override fun deleteBet(id: Long) {
        require(id > 0)
        val args = arrayOf(id.toString())
        // Shared teams, parameters, months, favorite carts and identity are deliberately untouched.
        for (table in listOf("tb_partida_loteca", "tb_partida_lotogol")) {
            if (exists(table)) database.delete(table, "$LEGACY_CART_PARENT = ?", args)
        }
        check(database.delete(LEGACY_CART_TABLE, "_id = ?", args) == 1) { "Aposta local alterada durante confirmação" }
    }

    override fun clearReceipt() {
        database.delete(ITEMS, null, null)
        database.delete(RECEIPT, null, null)
    }
}
