package br.gov.caixa.loterias.feature.cart.data.datasource.local

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_PARENT
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE
import br.gov.caixa.loterias.feature.cart.data.mapper.decodeLegacyCart

/** Uma confirmação criptografada controla o grafo completo, a intenção de transferência e os identificadores reservados monotonicamente. */
internal class SecureCartDocument(private val store: SecureRecordStore) {
    suspend fun read(): CartDocumentState = decodeCartDocument(store.read().values)

    suspend fun <T> transaction(action: SecureCartTransaction.() -> T): T {
        var result: Any? = null
        store.update { values ->
            val transaction = SecureCartTransaction(decodeCartDocument(values))
            result = transaction.action()
            transaction.state.validate()
            values + (CART_DOCUMENT_KEY to encodeCartDocument(transaction.state))
        }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    suspend fun requiresLegacyReview(): Boolean = read().legacyReview
    suspend fun requiresCartRecovery(): Boolean = read().let {
        it.legacyReview && (it.tables[LEGACY_CART_TABLE].orEmpty().isNotEmpty() || it.receipt != null)
    }
}

internal data class CartDocumentState(
    val tables: Map<String, List<CartSnapshotRow>> = emptyMap(),
    val receipt: CartReceipt? = null,
    val lastId: Long = 0,
    val purchases: Map<String, List<String>> = emptyMap(),
    val favorites: Set<Long> = emptySet(),
    val legacyReview: Boolean = false,
    val legacyDigest: String? = null,
) {
    override fun toString() = "CartDocumentState(redacted)"
    fun validate() {
        require(lastId >= 0)
        require(tables.size <= MAX_TABLES && tables.values.sumOf { it.size } <= MAX_GRAPH_ROWS)
        tables.forEach { (table, rows) ->
            require(table.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]{0,127}")))
            rows.forEach { row ->
                require(row.size <= MAX_COLUMNS)
                row.forEach { (key, value) ->
                    require(key.length in 1..MAX_IDENTIFIER_LENGTH && (value == null || value.length <= MAX_CELL_LENGTH))
                }
            }
        }
        decodeLegacyCart(tables)
        receipt?.let(::validateCartReceipt)
        require(
            purchases.size <= MAX_PURCHASE_AMOUNTS && purchases.all { (amount, dates) ->
                amount.toBigDecimalOrNull()?.signum()?.let { it >= 0 } == true &&
                    dates.size <= MAX_DATES_PER_AMOUNT && dates.all { it.length <= MAX_DATE_LENGTH }
            }
        )
        require(favorites.size <= MAX_FAVORITE_MARKERS && favorites.all { it > 0 })
        require(legacyDigest == null || legacyDigest.matches(Regex("[0-9a-f]{64}")))
    }
}

internal class SecureCartTransaction(internal var state: CartDocumentState) : CartTransferTransaction {
    override fun receipt() = state.receipt
    override fun tables() = state.tables
    fun replaceTables(tables: Map<String, List<CartSnapshotRow>>) {
        state =
            state.copy(tables = tables.mapValues { (_, rows) -> rows.map { it.toMap() } })
    }
    override fun save(receipt: CartReceipt) {
        validateCartReceipt(receipt)
        state =
            state.copy(receipt = receipt.copy(fingerprints = receipt.fingerprints.toMap()))
    }
    override fun clearReceipt() {
        state = state.copy(receipt = null)
    }
    override fun deleteBet(id: Long) {
        require(id > 0)
        check(state.tables[LEGACY_CART_TABLE].orEmpty().count { it["_id"] == id.toString() } == 1)
        replaceTables(
            state.tables.mapValues { (table, rows) ->
                when (table) {
                    LEGACY_CART_TABLE -> rows.filterNot { it["_id"] == id.toString() }
                    "tb_partida_loteca", "tb_partida_lotogol" -> rows.filterNot { it[LEGACY_CART_PARENT] == id.toString() }
                    else -> rows
                }
            }
        )
    }
    fun delete(id: Long) = deleteBet(id)
    fun nextId(existingMaximumId: Long): Long = Math.addExact(
        maxOf(
            state.lastId,
            existingMaximumId,
            state.receipt?.fingerprints?.keys?.maxOrNull() ?: 0
        ),
        1
    )
    fun saveLastId(id: Long) {
        require(id >= state.lastId)
        state = state.copy(lastId = id)
    }
    fun nextTeamId(before: Map<String, List<CartSnapshotRow>>): Long = Math.addExact(
        listOf("tb_equipe", "tb_parametro_equipe").flatMap {
            before[it].orEmpty()
        }.maxOfOrNull { checkNotNull(it["_id"]).toLong() } ?: 0,
        1
    )
    fun linkMirror(id: Long, link: Long) {
        replaceTables(
            state.tables + (
                LEGACY_CART_TABLE to state.tables[LEGACY_CART_TABLE].orEmpty().map {
                    if (it["_id"] == id.toString()) it + ("vinculo_espelho" to link.toString()) else it
                }
                )
        )
    }
    fun writeGraph(graph: Map<String, List<CartSnapshotRow>>) {
        val candidate = state.tables.toMutableMap()
        graph.forEach { (table, rows) ->
            var existing = candidate[table].orEmpty()
            rows.forEach { row ->
                when {
                    table == "tb_mes_sorte" -> existing = existing.filterNot { it["numero"] == row["numero"] } + row

                    table.startsWith("tb_partida_") -> {
                        val id = Math.addExact(existing.maxOfOrNull { it["_id"]?.toLong() ?: 0 } ?: 0, 1)
                        existing = existing + (row + ("_id" to id.toString()))
                    }

                    else -> {
                        check(existing.none { it["_id"] == row["_id"] })
                        existing = existing + row
                    }
                }
            }
            candidate[table] = existing
        }
        replaceTables(candidate)
    }
    fun datesForAmount(amount: String) = state.purchases[amount].orEmpty()
    fun replacePurchase(amount: String, date: String) {
        state = state.copy(purchases = state.purchases + (amount to listOf(date)))
    }
    fun favorites() = state.favorites
    fun updateFavorites(transform: (Set<Long>) -> Set<Long>) {
        state = state.copy(favorites = transform(state.favorites).toSet())
    }
}

internal fun validateCartReceipt(receipt: CartReceipt) {
    require(receipt.transferId.isNotBlank() && receipt.transferId.length <= MAX_TRANSFER_ID_LENGTH)
    require(receipt.ownerKey.matches(Regex("[0-9a-f]{64}")))
    require(receipt.fingerprints.isNotEmpty() && receipt.fingerprints.size <= MAX_GRAPH_ROWS)
    require(receipt.fingerprints.all { (id, hash) -> id > 0 && hash.matches(Regex("[0-9a-f]{64}")) })
}

private const val MAX_TABLES = 32
private const val MAX_GRAPH_ROWS = 20_000
private const val MAX_COLUMNS = 128
private const val MAX_IDENTIFIER_LENGTH = 128
private const val MAX_CELL_LENGTH = 32_768
private const val MAX_PURCHASE_AMOUNTS = 2_000
private const val MAX_DATES_PER_AMOUNT = 2_000
private const val MAX_DATE_LENGTH = 64
private const val MAX_FAVORITE_MARKERS = 10_000
private const val MAX_TRANSFER_ID_LENGTH = 256
