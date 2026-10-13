package br.gov.caixa.loterias.core.preferences

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.gov.caixa.loterias.core.domain.cart.RecentPurchaseRepository
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

internal interface RecentPurchaseStore {
    fun datesForAmount(amount: String): List<String>
    fun replaceForAmount(amount: String, date: String)
}

/** Compatibility adapter: retains the legacy database, table and minute-resolution dates. */
class LegacyRecentPurchaseRepository internal constructor(
    private val store: RecentPurchaseStore,
    private val zone: () -> ZoneId,
) : RecentPurchaseRepository {
    override suspend fun latestPurchaseAt(amount: BigDecimal): Instant? =
        store.datesForAmount(amount.stripTrailingZeros().toPlainString()).mapNotNull { date ->
            try {
                LocalDateTime.parse(date, FORMAT).atZone(zone()).toInstant()
            } catch (_: DateTimeParseException) {
                null
            }
        }.maxOrNull()

    override suspend fun record(amount: BigDecimal, purchasedAt: Instant) {
        store.replaceForAmount(
            amount.stripTrailingZeros().toPlainString(),
            FORMAT.format(purchasedAt.atZone(zone())),
        )
    }

    companion object {
        private val FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT)
        fun fromContext(context: Context): LegacyRecentPurchaseRepository =
            LegacyRecentPurchaseRepository(AndroidRecentPurchaseStore(context.applicationContext), ZoneId::systemDefault)
    }
}

private class AndroidRecentPurchaseStore(private val context: Context) : RecentPurchaseStore {
    override fun datesForAmount(amount: String): List<String> {
        val file = context.getDatabasePath(DATABASE)
        if (!file.exists()) return emptyList()
        return SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY).use { database ->
            val exists = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?", arrayOf(TABLE),
            ).use { it.moveToFirst() }
            if (!exists) return@use emptyList()
            database.query(TABLE, arrayOf("data"), "valor = ?", arrayOf(amount), null, null, null).use { cursor ->
                buildList { while (cursor.moveToNext()) cursor.getString(0)?.let(::add) }
            }
        }
    }

    override fun replaceForAmount(amount: String, date: String) {
        context.openOrCreateDatabase(DATABASE, Context.MODE_PRIVATE, null).use { database ->
            database.beginTransaction()
            try {
                database.execSQL("CREATE TABLE IF NOT EXISTS $TABLE (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, valor REAL, data TEXT)")
                // Do not repeat the old CRUD bug that deleted unrelated amounts without a WHERE.
                database.delete(TABLE, "valor = ?", arrayOf(amount))
                database.insertOrThrow(TABLE, null, ContentValues().apply {
                    put("valor", amount)
                    put("data", date)
                })
                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }
        }
    }

    private companion object {
        const val DATABASE = "Loterias.db"
        const val TABLE = "tb_ultima_compra"
    }
}
