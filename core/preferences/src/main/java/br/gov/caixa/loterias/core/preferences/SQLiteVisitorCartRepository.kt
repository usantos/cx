package br.gov.caixa.loterias.core.preferences

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Transactional writes to the same graph used by LegacyCartTransferLocalRepository. */
class SQLiteVisitorCartRepository(context: Context) : VisitorCartRepository {
    private val context = context.applicationContext
    override suspend fun current(): ShoppingCart = withContext(Dispatchers.IO) {
        legacyVisitorCart(LegacyLocalCartReader.fromContext(context).snapshot())
    }
    private fun <T> transaction(action: (SQLiteDatabase) -> T): T {
        val file = context.getDatabasePath("Loterias.db")
        file.parentFile?.mkdirs()
        return SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READWRITE or SQLiteDatabase.CREATE_IF_NECESSARY) {
            error("Banco local indisponível; arquivo preservado")
        }.use { db ->
            db.beginTransaction()
            try { action(db).also { db.setTransactionSuccessful() } } finally { db.endTransaction() }
        }
    }
    private fun tables(db: SQLiteDatabase) = readLegacyCartSnapshot(SQLiteLegacyCartTableSource(db))
    override suspend fun add(draft: BetDraft, parameters: BettingParameters): ShoppingCart = withContext(Dispatchers.IO) {
        transaction { db ->
            require(draft.quantity in 1..1000) { "Quantidade de apostas inválida." }
            val game = requireNotNull(parameters.games.firstOrNull { it.modality.id == draft.modalityId && it.contestType.equals(draft.contestType, true) && (draft.contestNumber == null || draft.contestNumber == it.contestNumber) }) {
                "Modalidade indisponível para apostar no momento."
            }
            require(game.open) { "O registro de apostas individuais para o concurso ${game.contestNumber} foi encerrado." }
            val before = tables(db)
            val existing = decodeLegacyCart(before)
            val count = draft.quantity * if (draft.generateMirror) 2 else 1
            require(parameters.maximumCartBets <= 0 || existing.size + count <= parameters.maximumCartBets) {
                "Não é permitido incluir uma nova aposta no carrinho, pois a quantidade máxima de aposta já foi atingida."
            }
            val price = if (draft.modalityId == 19) game.prices.firstOrNull {
                it.doubleQuantity == draft.lotecaSelections.count { s -> listOf(s.homeWin, s.draw, s.awayWin).count { it } == 2 } &&
                    it.tripleQuantity == draft.lotecaSelections.count { s -> s.homeWin && s.draw && s.awayWin }
            } else game.prices.firstOrNull { it.numberQuantity == draft.numberQuantity && it.cloverQuantity == draft.cloverQuantity }
                ?: game.prices.firstOrNull { it.numberQuantity == draft.numberQuantity }
            val unit = requireNotNull(price) { "Preço da aposta indisponível." }.amount
                .multiply(BigDecimal.valueOf(draft.recurringDraws.coerceAtLeast(1).toLong()))
            if (!draft.surprise && !draft.allowLocalDuplicate) {
                val candidate = decodeLegacyCart(guestCartRows(draft, game, unit, 1, 1)).single()
                val duplicate = existing.any { it.modality == candidate.modality && it.targetContest == candidate.targetContest &&
                    it.contestType.value == candidate.contestType.value && it.numbers == candidate.numbers &&
                    it.clovers.orEmpty() == candidate.clovers.orEmpty() && it.repeatCount == candidate.repeatCount &&
                    it.favoriteTeam?.number == candidate.favoriteTeam?.number && it.luckyMonth?.number == candidate.luckyMonth?.number &&
                    it.loteca == candidate.loteca && it.lotogol == candidate.lotogol }
                require(!duplicate) { VISITOR_DUPLICATE_CONFIRMATION }
            }
            db.execSQL("CREATE TABLE IF NOT EXISTS compose_visitor_cart_sequence_v1 (slot INTEGER PRIMARY KEY CHECK(slot=1), last_id INTEGER NOT NULL)")
            val highWater = db.rawQuery("SELECT last_id FROM compose_visitor_cart_sequence_v1 WHERE slot=1", null).use { if (it.moveToFirst()) it.getLong(0) else 0L }
            val hasReceiptItems = db.rawQuery("SELECT 1 FROM sqlite_master WHERE type='table' AND name='compose_cart_transfer_items_v1'", null).use { it.moveToFirst() }
            val reservedId = if (hasReceiptItems) db.rawQuery("SELECT MAX(local_id) FROM compose_cart_transfer_items_v1", null).use { if (it.moveToFirst()) it.getLong(0) else 0L } else 0L
            var id = maxOf(highWater, reservedId, existing.maxOfOrNull { it.localId } ?: 0L) + 1
            var teamId = listOf("tb_equipe", "tb_parametro_equipe").flatMap { before[it].orEmpty() }
                .maxOfOrNull { it["_id"]!!.toLong() }?.plus(1) ?: 1L
            repeat(draft.quantity) {
                // Like the legacy cart, Surpresinha remains opaque until the server generates it.
                val graph = guestCartRows(draft, game, unit, id, teamId)
                writeGraph(db, graph)
                teamId += graph["tb_equipe"].orEmpty().size
                if (draft.generateMirror && draft.modalityId == 16) {
                    val mirror = draft.copy(numbers = if (draft.surprise) emptyList() else (1..100).filterNot { it in draft.numbers }, generateMirror = false)
                    val mirrorGraph = guestCartRows(mirror, game, unit, id + 1, teamId).toMutableMap()
                    mirrorGraph[LEGACY_CART_TABLE] = mirrorGraph[LEGACY_CART_TABLE]!!.map { it + mapOf("espelho" to "1", "vinculo_espelho" to (id + game.contestNumber).toString()) }
                    writeGraph(db, mirrorGraph)
                    db.update(LEGACY_CART_TABLE, ContentValues().apply { put("vinculo_espelho", id + game.contestNumber) }, "_id=?", arrayOf(id.toString()))
                    id++
                }
                id++
            }
            db.insertWithOnConflict("compose_visitor_cart_sequence_v1", null, ContentValues().apply { put("slot", 1); put("last_id", id - 1) }, SQLiteDatabase.CONFLICT_REPLACE)
            legacyVisitorCart(decodeLegacyCart(tables(db)))
        }
    }
    private fun writeGraph(db: SQLiteDatabase, graph: Map<String, List<LegacyCartRow>>) {
        graph.forEach { (table, rows) -> rows.forEach { source ->
            val row = if (table.startsWith("tb_partida_")) source - "_id" else source
            val primary = if (table == "tb_mes_sorte") "numero" else "_id"
            val columns = (row.keys + primary).joinToString(",") { if (it == primary) "$it INTEGER PRIMARY KEY" else "$it TEXT" }
            db.execSQL("CREATE TABLE IF NOT EXISTS $table ($columns)")
            val values = ContentValues().apply { row.forEach { (key, value) -> if (value == null) putNull(key) else put(key, value) } }
            if (table == "tb_mes_sorte") {
                if (db.update(table, values, "numero=?", arrayOf(row["numero"]!!)) == 0) db.insertOrThrow(table, null, values)
            } else db.insertOrThrow(table, null, values)
        } }
    }
    override suspend fun remove(item: CartItem): ShoppingCart = withContext(Dispatchers.IO) {
        transaction { db ->
            val snapshot = decodeLegacyCart(tables(db))
            val bet = requireNotNull(snapshot.firstOrNull { it.localId == item.id }) { "Aposta local não encontrada." }
            val ids = if (bet.mirrorLink > 0) snapshot.filter { it.mirrorLink == bet.mirrorLink }.map { it.localId } else listOf(bet.localId)
            ids.forEach { delete(db, it) }
            legacyVisitorCart(decodeLegacyCart(tables(db)))
        }
    }
    override suspend fun clear(): ShoppingCart = withContext(Dispatchers.IO) {
        transaction { db ->
            decodeLegacyCart(tables(db)).forEach { delete(db, it.localId) }
            legacyVisitorCart(emptyList())
        }
    }
    private fun delete(db: SQLiteDatabase, id: Long) {
        val present = tables(db)
        listOf("tb_partida_loteca", "tb_partida_lotogol").filter { it in present }.forEach {
            db.delete(it, "$LEGACY_CART_PARENT=?", arrayOf(id.toString()))
        }
        db.delete(LEGACY_CART_TABLE, "_id=?", arrayOf(id.toString()))
    }
}

internal fun legacyVisitorCart(bets: List<LegacyLocalCartBet>): ShoppingCart {
    val items = bets.map { bet ->
        val rows = when (val numbers = bet.numbers) {
            is LegacyCartNumbers.Flat -> listOf(numbers.values)
            is LegacyCartNumbers.Columns -> numbers.values
            null -> emptyList()
        }
        CartItem(bet.localId, CartItemKind.INDIVIDUAL_BET, when (bet.modality) {
            LegacyCartModality.MEGA_SENA -> "Mega-Sena"; LegacyCartModality.MAIS_MILIONARIA -> "+Milionária"
            LegacyCartModality.LOTOFACIL -> "Lotofácil"; LegacyCartModality.SUPER_7 -> "Super Sete"
            LegacyCartModality.DIA_DE_SORTE -> "Dia de Sorte"; LegacyCartModality.DUPLA_SENA -> "Dupla Sena"
            else -> bet.modality.name.lowercase().replaceFirstChar { it.titlecase() }
        }, if (bet.quickPick && rows.flatten().isEmpty()) List(bet.numberCount) { "XX" }.joinToString(" - ")
            else rows.flatten().joinToString("  ") { it.toString().padStart(2, '0') }, bet.amount,
            modalityCode = bet.modality.name, contestNumber = bet.targetContest, numberQuantity = bet.numberCount,
            recurringDraws = bet.repeatCount.coerceAtLeast(1), selectedNumbers = rows, selectedClovers = bet.clovers.orEmpty(),
            badges = buildSet { if (bet.quickPick) add(CartItemBadge.SURPRISE); if (bet.repeatCount > 0) add(CartItemBadge.RECURRING); if (bet.mirrored) add(CartItemBadge.MIRROR) },
            complementaryLabel = bet.favoriteTeam?.name ?: bet.luckyMonth?.name,
            gameRows = bet.loteca.map { "${it.first.team.name} ${if (it.first.winner) "1" else ""} ${if (it.draw) "X" else ""} ${if (it.second.winner) "2" else ""} ${it.second.team.name}" } +
                bet.lotogol.map { "${it.first.team.name} ${it.first.score} × ${it.second.score} ${it.second.team.name}" })
    }
    return ShoppingCart(0, items.fold(BigDecimal.ZERO) { total, item -> total + item.amount }, items, totalBetCount = items.size)
}
