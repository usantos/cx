package br.gov.caixa.loterias.feature.cart.data.repository

import br.gov.caixa.loterias.feature.cart.domain.model.CartCleanupResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartTransferPreparation
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.data.repository.CartTransferRepositoryImpl
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartReceipt
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartReceiptPhase
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartTransferLocalDataSource
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartTransferTransaction
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_PARENT
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartSnapshotRow

class CartTransferRepositoryImplTest {
    private val owner = "a".repeat(64)
    private val otherOwner = "b".repeat(64)
    private fun repository(store: MemoryCartTransferStore) = CartTransferRepositoryImpl(store) { "transfer-test" }

    @Test fun `review discards only inspected batch and rejects wrong owner and changed snapshots`() = runTest {
        val store = MemoryCartTransferStore(transferGraph())
        val repo = repository(store)
        repo.prepare(owner)
        val review = repo.review(owner)!!
        assertNull(repo.review(otherOwner))
        assertFalse(repo.discardReviewed(otherOwner, review))
        assertFalse(repo.discardReviewed(owner, review.copy(bets = emptyList())))
        assertEquals(listOf(1L, 2L), store.ids())
        store.data = store.data + (LEGACY_CART_TABLE to (store.data[LEGACY_CART_TABLE].orEmpty() + transferBet(3)))
        assertTrue(repo.discardReviewed(owner, review))
        assertEquals(listOf(3L), store.ids())
        assertNull(store.savedReceipt)
    }

    @Test fun `empty storage never reserves a receipt`() = runTest {
        val store = MemoryCartTransferStore(emptyMap())
        val repo = CartTransferRepositoryImpl(store) { error("Must not allocate ID") }
        assertEquals(CartTransferPreparation.Empty, repo.prepare(owner))
        assertNull(store.savedReceipt)
        assertFalse(repo.confirmReceived(owner, "unknown"))
        assertTrue(runCatching { repo.finishConfirmed(owner, "unknown") }.isFailure)
    }

    @Test fun `pending receipt commits before Ready and survives repository recreation without a second permit`() = runTest {
        val store = MemoryCartTransferStore(transferGraph())
        val before = store.data
        val ready = repository(store).prepare(owner) as CartTransferPreparation.Ready
        assertEquals("transfer-test", ready.transferId)
        assertEquals(listOf(1L, 2L), ready.bets.map { it.localId })
        assertEquals(CartReceiptPhase.PENDING, store.savedReceipt?.phase)
        assertEquals(before, store.data)
        assertEquals(CartTransferPreparation.RequiresReview, repository(store).prepare(owner))
        assertEquals(CartTransferPreparation.RequiresReview, repository(store).prepare(otherOwner))
        assertEquals("CartTransferPreparation.Ready(redacted)", ready.toString())
        assertEquals("CartReceipt(redacted)", store.savedReceipt.toString())
    }

    @Test fun `wrong identity stale callback or unconfirmed receipt cannot delete bets`() = runTest {
        val store = MemoryCartTransferStore(transferGraph())
        val repo = repository(store)
        repo.prepare(owner)
        assertFalse(repo.confirmReceived(otherOwner, "transfer-test"))
        assertFalse(repo.confirmReceived(owner, "stale"))
        assertTrue(runCatching { repo.finishConfirmed(owner, "transfer-test") }.isFailure)
        assertTrue(repo.confirmReceived(owner, "transfer-test"))
        assertTrue(repo.confirmReceived(owner, "transfer-test"))
        assertEquals(CartTransferPreparation.CleanupPending("transfer-test"), repository(store).prepare(owner))
        assertEquals(CartTransferPreparation.RequiresReview, repo.prepare(otherOwner))
        assertTrue(runCatching { repo.finishConfirmed(otherOwner, "transfer-test") }.isFailure)
        assertTrue(runCatching { repo.finishConfirmed(owner, "stale") }.isFailure)
        assertEquals(listOf(1L, 2L), store.ids())
    }

    @Test fun `confirmed cleanup removes only original parents and their matches leaving new and shared data`() = runTest {
        val store = MemoryCartTransferStore(transferGraph())
        val repo = repository(store)
        repo.prepare(owner)
        repo.confirmReceived(owner, "transfer-test")
        store.data = store.data + (LEGACY_CART_TABLE to (store.data.getValue(LEGACY_CART_TABLE) + transferBet(3)))
        val teams = store.data.getValue("tb_equipe")
        assertEquals(CartCleanupResult.Complete, repository(store).finishConfirmed(owner, "transfer-test"))
        assertEquals(listOf(3L), store.ids())
        assertEquals(teams, store.data["tb_equipe"])
        assertEquals(listOf(mapOf("_id" to "99", "name" to "preserve")), store.data["tb_usuario"])
        assertEquals(1, store.data.getValue("tb_partida_loteca").size) // Registro pai 99 sem relação.
        assertTrue(store.data.getValue("tb_partida_lotogol").isEmpty())
        assertNull(store.savedReceipt)
        assertTrue(runCatching { repo.finishConfirmed(owner, "transfer-test") }.isFailure)
        assertEquals(listOf(3L), (repo.prepare(owner) as CartTransferPreparation.Ready).bets.map { it.localId })
    }

    @Test fun `any related edit preserves modified bet and prevents automatic reupload`() = runTest {
        for (table in listOf(
            LEGACY_CART_TABLE,
            "tb_partida_loteca",
            "tb_partida_lotogol",
            "tb_equipe",
            "tb_parametro_equipe",
            "tb_mes_sorte"
        )) {
            val store = MemoryCartTransferStore(transferGraph())
            val repo = repository(store)
            repo.prepare(owner)
            repo.confirmReceived(owner, "transfer-test")
            store.data = store.data + (
                table to store.data.getValue(table).mapIndexed { index, row ->
                    if (index == 0) row + ("future_column" to "changed") else row
                }
                )
            assertEquals(table, CartCleanupResult.Conflict(setOf(1L)), repo.finishConfirmed(owner, "transfer-test"))
            assertEquals(listOf(1L), store.ids())
            assertEquals(CartReceiptPhase.CONFLICT, store.savedReceipt?.phase)
            assertEquals(CartTransferPreparation.RequiresReview, repository(store).prepare(owner))
            assertFalse(repo.confirmReceived(owner, "transfer-test"))
            assertTrue(runCatching { repo.finishConfirmed(owner, "transfer-test") }.isFailure)
        }
    }

    @Test fun `already absent original rows do not cause broad deletion`() = runTest {
        val store = MemoryCartTransferStore(transferGraph())
        val repo = repository(store)
        repo.prepare(owner)
        repo.confirmReceived(owner, "transfer-test")
        store.data = store.data + (LEGACY_CART_TABLE to listOf(transferBet(9)))
        assertEquals(CartCleanupResult.Complete, repo.finishConfirmed(owner, "transfer-test"))
        assertEquals(listOf(9L), store.ids())
        assertEquals(2, store.data.getValue("tb_partida_loteca").size)
    }

    @Test fun `failed commits never return permission confirmation or partial cleanup`() = runTest {
        val store = MemoryCartTransferStore(transferGraph())
        val repo = repository(store)
        store.failCommit = true
        assertTrue(runCatching { repo.prepare(owner) }.isFailure)
        assertNull(store.savedReceipt)
        store.failCommit = false
        repo.prepare(owner)
        store.failCommit = true
        assertTrue(runCatching { repo.confirmReceived(owner, "transfer-test") }.isFailure)
        assertEquals(CartReceiptPhase.PENDING, store.savedReceipt?.phase)
        store.failCommit = false
        repo.confirmReceived(owner, "transfer-test")
        for (failureAtDelete in listOf(null, 2L)) {
            store.failCommit = failureAtDelete == null
            store.failDeleteId = failureAtDelete
            assertTrue(runCatching { repo.finishConfirmed(owner, "transfer-test") }.isFailure)
            assertEquals(listOf(1L, 2L), store.ids())
            assertEquals(CartReceiptPhase.CONFIRMED, store.savedReceipt?.phase)
        }
        store.failCommit = false
        store.failDeleteId = null
        assertEquals(CartCleanupResult.Complete, repo.finishConfirmed(owner, "transfer-test"))
    }

    @Test fun `invalid identity bad graph and cancellation do not start an upload`() = runTest {
        val store = MemoryCartTransferStore(transferGraph())
        val repo = repository(store)
        for (invalid in listOf("", "token", "A".repeat(64), "a".repeat(63))) {
            assertTrue(runCatching { repo.prepare(invalid) }.isFailure)
            assertTrue(runCatching { repo.confirmReceived(invalid, "transfer-test") }.isFailure)
            assertTrue(runCatching { repo.finishConfirmed(invalid, "transfer-test") }.isFailure)
        }
        assertNull(store.savedReceipt)
        assertTrue(runCatching { CartTransferRepositoryImpl(store) { " " }.prepare(owner) }.isFailure)
        store.failRead = CancellationException("test cancellation")
        assertTrue(runCatching { repo.prepare(owner) }.exceptionOrNull() is CancellationException)
        store.failRead = null
        store.data = store.data + (LEGACY_CART_TABLE to listOf(transferBet(1) + ("modalidade" to "invalid")))
        assertTrue(runCatching { repo.prepare(owner) }.isFailure)
        assertNull(store.savedReceipt)
    }

    @Test fun `two repository instances cannot reserve the same batch`() {
        val store = MemoryCartTransferStore(transferGraph())
        val executor = Executors.newFixedThreadPool(2)
        try {
            val futures = (1..2).map { executor.submit<CartTransferPreparation> { runBlocking { repository(store).prepare(owner) } } }
            val outcomes = futures.map { it.get(5, TimeUnit.SECONDS) }
            assertEquals(1, outcomes.count { it is CartTransferPreparation.Ready })
            assertEquals(1, outcomes.count { it == CartTransferPreparation.RequiresReview })
        } finally {
            executor.shutdownNow()
        }
    }
}

internal fun transferBet(id: Long): CartSnapshotRow = mapOf(
    "_id" to id.toString(),
    "modalidade" to "mega-sena",
    "numeros_selecionados" to "[1,2,3,4,5,6]"
)
internal fun transferGraph(): Map<String, List<CartSnapshotRow>> = mapOf(
    LEGACY_CART_TABLE to listOf(transferBet(1) + mapOf("id_time_coracao" to "9", "id_mes_sorte" to "12"), transferBet(2)),
    "tb_partida_loteca" to
        listOf(
            mapOf("_id" to "1", LEGACY_CART_PARENT to "1", "id_equipe_um" to "20", "id_equipe_dois" to "21"),
            mapOf(
                "_id" to "99",
                LEGACY_CART_PARENT to "99"
            )
        ),
    "tb_partida_lotogol" to listOf(mapOf("_id" to "2", LEGACY_CART_PARENT to "1", "id_equipe_um" to "20", "id_equipe_dois" to "21")),
    "tb_equipe" to listOf(mapOf("_id" to "20", "id_parametro_equipe" to "9", "nome" to "Time"), mapOf("_id" to "21", "nome" to "Outro")),
    "tb_parametro_equipe" to listOf(mapOf("_id" to "9", "nome" to "Time")),
    "tb_mes_sorte" to listOf(mapOf("numero" to "12", "nome" to "Dezembro")),
    "tb_usuario" to listOf(mapOf("_id" to "99", "name" to "preserve"))
)

internal class MemoryCartTransferStore(var data: Map<String, List<CartSnapshotRow>>) : CartTransferLocalDataSource {
    var savedReceipt: CartReceipt? = null
    var failCommit = false
    var failDeleteId: Long? = null
    var failRead: Throwable? = null
    fun ids() = data[LEGACY_CART_TABLE].orEmpty().map { it.getValue("_id")!!.toLong() }

    override suspend fun <T> transaction(action: (CartTransferTransaction) -> T): T = synchronized(this) {
        val beforeData = data
        val beforeReceipt = savedReceipt
        try {
            val result = action(object : CartTransferTransaction {
                override fun receipt() = savedReceipt
                override fun tables(): Map<String, List<CartSnapshotRow>> {
                    failRead?.let { throw it }
                    return data
                }
                override fun save(receipt: CartReceipt) {
                    savedReceipt = receipt
                }
                override fun clearReceipt() {
                    savedReceipt = null
                }
                override fun deleteBet(id: Long) {
                    check(id != failDeleteId) { "test delete failure" }
                    data = data.mapValues { (table, rows) ->
                        when (table) {
                            LEGACY_CART_TABLE -> rows.filterNot { it["_id"] == id.toString() }
                            "tb_partida_loteca", "tb_partida_lotogol" -> rows.filterNot { it[LEGACY_CART_PARENT] == id.toString() }
                            else -> rows
                        }
                    }
                }
            })
            check(!failCommit) { "test commit failure" }
            return result
        } catch (failure: Throwable) {
            data = beforeData
            savedReceipt = beforeReceipt
            throw failure
        }
    }
}
