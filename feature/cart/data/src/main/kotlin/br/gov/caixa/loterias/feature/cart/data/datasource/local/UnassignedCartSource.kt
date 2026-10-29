package br.gov.caixa.loterias.feature.cart.data.datasource.local

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.sqlite.transaction
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_PARENT
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE

/** Lê tabelas conhecidas do aplicativo; a limpeza verificada nunca invoca um auxiliar destrutivo do banco. */
internal class UnassignedCartSource(private val context: Context) : CartMigrationSource {
    override fun read(): CartMigrationSnapshot? {
        val file = context.getDatabasePath(DATABASE)
        if (!file.exists()) return null
        return SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY) {
            error("Unassigned cart unavailable; original preserved")
        }.use(::readState)
    }

    override fun cleanup(expected: CartMigrationSnapshot) {
        val file = context.getDatabasePath(DATABASE)
        if (!file.exists()) return
        SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READWRITE) {
            error("Unassigned cart unavailable; original preserved")
        }.use { db -> cleanupTransaction(db, expected) }
    }

    private fun cleanupTransaction(db: SQLiteDatabase, expected: CartMigrationSnapshot) {
        db.transaction {
            readState(db)?.let { current ->
                check(current == expected) { "Unassigned cart changed after encrypted migration" }
                deleteCapturedRoots(db, expected)
                listOf(RECEIPT, ITEMS, SEQUENCE, PURCHASES).filter { exists(db, it) }.forEach {
                    db.delete(it, null, null)
                }
            }
        }
    }

    private fun deleteCapturedRoots(db: SQLiteDatabase, expected: CartMigrationSnapshot) {
        val sportsTables = listOf("tb_partida_loteca", "tb_partida_lotogol").filter { exists(db, it) }
        expected.tables[LEGACY_CART_TABLE].orEmpty().forEach { row ->
            val args = arrayOf(checkNotNull(row["_id"]))
            sportsTables.forEach { db.delete(it, "$LEGACY_CART_PARENT=?", args) }
            check(db.delete(LEGACY_CART_TABLE, "_id=?", args) == 1)
        }
    }

    private fun readState(db: SQLiteDatabase): CartMigrationSnapshot? {
        val source = CartTableDataSourceImpl(db)
        val version = source.dataVersion()
        val tables = readLegacyCartSnapshot(source)
        val receipt = readReceipt(db)
        val lastId = readSequence(db)
        val purchases = readPurchases(db)
        check(version == source.dataVersion()) { "Unassigned cart changed during coherent snapshot" }
        val snapshot = CartMigrationSnapshot(tables, receipt, lastId, purchases)
        if (snapshot.isEmpty()) return null
        return snapshot.also { it.document().validate() }
    }

    private fun readSequence(db: SQLiteDatabase): Long {
        if (!exists(db, SEQUENCE)) return 0
        return db.rawQuery("SELECT slot,last_id FROM $SEQUENCE", null).use {
            if (!it.moveToFirst()) return@use 0L
            check(it.count == 1 && it.exactLong(0) == 1L)
            it.exactLong(1).also { value -> check(value >= 0) }
        }
    }

    private fun readPurchases(db: SQLiteDatabase): Map<String, List<String>> {
        if (!exists(db, PURCHASES)) return emptyMap()
        return db.rawQuery("SELECT valor,data FROM $PURCHASES ORDER BY id", null).use { cursor ->
            val rows = mutableMapOf<String, MutableList<String>>()
            while (cursor.moveToNext()) {
                check(!cursor.isNull(0) && !cursor.isNull(1))
                val amount = cursor.getString(0).toBigDecimal().stripTrailingZeros().toPlainString()
                rows.getOrPut(amount) { mutableListOf() }.add(cursor.getString(1))
            }
            rows.mapValues { it.value.toList() }
        }
    }

    private fun readReceipt(db: SQLiteDatabase): CartReceipt? {
        val present = exists(db, RECEIPT)
        check(present == exists(db, ITEMS)) { "Incomplete unassigned receipt" }
        if (!present) return null
        val items = readReceiptItems(db)
        return db.rawQuery(RECEIPT_QUERY, null).use { cursor ->
            if (!cursor.moveToFirst()) {
                check(items.isEmpty())
                return@use null
            }
            check(cursor.count == 1 && cursor.exactLong(SLOT_COLUMN) == 1L)
            check(cursor.exactLong(VERSION_COLUMN) == RECEIPT_SCHEMA)
            CartReceipt(
                checkNotNull(cursor.getString(TRANSFER_ID_COLUMN)),
                checkNotNull(cursor.getString(OWNER_COLUMN)),
                CartReceiptPhase.valueOf(checkNotNull(cursor.getString(PHASE_COLUMN))),
                items
            ).also(::validateCartReceipt)
        }
    }

    private fun readReceiptItems(db: SQLiteDatabase): Map<Long, String> =
        db.rawQuery("SELECT local_id,fingerprint FROM $ITEMS ORDER BY local_id", null).use { cursor ->
            buildMap {
                while (cursor.moveToNext()) {
                    val id = cursor.exactLong(0)
                    check(!containsKey(id))
                    put(id, checkNotNull(cursor.getString(1)))
                }
            }
        }

    private fun CartMigrationSnapshot.isEmpty() =
        tables[LEGACY_CART_TABLE].orEmpty().isEmpty() && receipt == null && lastId == 0L && purchases.isEmpty()

    private fun Cursor.exactLong(index: Int): Long = checkNotNull(getString(index)?.toLongOrNull())

    private fun exists(db: SQLiteDatabase, table: String) = db.rawQuery(
        "SELECT 1 FROM sqlite_master WHERE type='table' AND name=?",
        arrayOf(table)
    ).use { it.moveToFirst() }

    private companion object {
        const val DATABASE = "Loterias.db"
        const val RECEIPT = "compose_cart_transfer_receipt_v1"
        const val ITEMS = "compose_cart_transfer_items_v1"
        const val SEQUENCE = "compose_visitor_cart_sequence_v1"
        const val PURCHASES = "tb_ultima_compra"
        const val SLOT_COLUMN = 0
        const val TRANSFER_ID_COLUMN = 1
        const val OWNER_COLUMN = 2
        const val PHASE_COLUMN = 3
        const val VERSION_COLUMN = 4
        const val RECEIPT_SCHEMA = 1L
        const val RECEIPT_QUERY = "SELECT slot,transfer_id,owner_key,phase,encoding_version FROM $RECEIPT"
    }
}
