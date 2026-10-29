package br.gov.caixa.loterias.feature.cart.data.datasource.local

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE
import br.gov.caixa.loterias.feature.cart.data.repository.CartTransferRepositoryImpl
import br.gov.caixa.loterias.feature.cart.data.repository.transferGraph
import br.gov.caixa.loterias.feature.cart.domain.model.CartCleanupResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartTransferPreparation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureCartDocumentTest {
    private val owner = "a".repeat(OWNER_KEY_LENGTH)

    @Test fun `complete graph receipt and allocator survive reopening in one document`() = runTest {
        val disk = CartMemorySecureStore()
        val document = SecureCartDocument(disk)
        document.transaction {
            replaceTables(transferGraph())
            saveLastId(IMPORTED_HIGH_WATER)
        }
        val repo = CartTransferRepositoryImpl(document) { "transfer" }
        assertTrue(repo.prepare(owner) is CartTransferPreparation.Ready)
        val reopened = SecureCartDocument(disk)
        reopened.transaction {
            assertEquals(transferGraph(), tables())
            assertEquals(CartReceiptPhase.PENDING, receipt()?.phase)
            assertEquals(NEXT_IMPORTED_ID, nextId(2))
        }
        assertEquals(CartTransferPreparation.RequiresReview, CartTransferRepositoryImpl(reopened).prepare(owner))
    }

    @Test fun `failed encrypted commit cannot publish a receipt or partial cleanup`() = runTest {
        val disk = CartMemorySecureStore()
        val document = SecureCartDocument(disk)
        document.transaction { replaceTables(transferGraph()) }
        val repo = CartTransferRepositoryImpl(document) { "transfer" }
        disk.failWrite = true
        assertTrue(runCatching { repo.prepare(owner) }.isFailure)
        disk.failWrite = false
        document.transaction { assertNull(receipt()) }
        repo.prepare(owner)
        repo.confirmReceived(owner, "transfer")
        disk.failWrite = true
        assertTrue(runCatching { repo.finishConfirmed(owner, "transfer") }.isFailure)
        disk.failWrite = false
        document.transaction {
            assertEquals(listOf("1", "2"), tables().getValue(LEGACY_CART_TABLE).map { it["_id"] })
            assertEquals(CartReceiptPhase.CONFIRMED, receipt()?.phase)
        }
        assertEquals(CartCleanupResult.Complete, repo.finishConfirmed(owner, "transfer"))
    }

    @Test fun `pending receipt IDs remain reserved after removing graph`() = runTest {
        val document = SecureCartDocument(CartMemorySecureStore())
        document.transaction {
            replaceTables(transferGraph())
            save(CartReceipt("transfer", owner, CartReceiptPhase.PENDING, mapOf(RESERVED_RECEIPT_ID to "b".repeat(OWNER_KEY_LENGTH))))
            deleteBet(1)
            deleteBet(2)
        }
        document.transaction { assertEquals(NEXT_RESERVED_ID, nextId(0)) }
    }

    @Test fun `invalid persisted graph is rejected without replacement`() = runTest {
        val disk = CartMemorySecureStore()
        val document = SecureCartDocument(disk)
        document.transaction { replaceTables(transferGraph()) }
        val before = disk.snapshot
        assertTrue(
            runCatching {
                document.transaction { replaceTables(mapOf(LEGACY_CART_TABLE to listOf(mapOf("_id" to "-1")))) }
            }.isFailure
        )
        assertEquals(before, disk.snapshot)
    }

    @Test fun `concurrent documents share receipt and allocate unique monotonically increasing IDs`() = runTest {
        val disk = CartMemorySecureStore()
        val allocated = (1..CONCURRENT_WRITES).map {
            async {
                SecureCartDocument(disk).transaction {
                    nextId(0).also(::saveLastId)
                }
            }
        }.awaitAll()
        assertEquals((1L..CONCURRENT_WRITES.toLong()).toList(), allocated.sorted())
    }

    @Test fun `receipt related mutation survives confirmed cleanup as conflict`() = runTest {
        val document = SecureCartDocument(CartMemorySecureStore())
        document.transaction { replaceTables(transferGraph()) }
        val repository = CartTransferRepositoryImpl(document) { "transfer" }
        repository.prepare(owner)
        repository.confirmReceived(owner, "transfer")
        document.transaction {
            replaceTables(
                tables().mapValues { (table, rows) ->
                    if (table == "tb_equipe") rows.map { it + ("nome" to "changed") } else rows
                }
            )
        }
        assertEquals(CartCleanupResult.Conflict(setOf(1L)), repository.finishConfirmed(owner, "transfer"))
        assertEquals(CartTransferPreparation.RequiresReview, repository.prepare(owner))
    }

    @Test fun `unknown document schema or malformed ciphertext payload never becomes an empty cart`() = runTest {
        for (values in listOf(mapOf("cart-document-v2" to "future"), mapOf("cart-document-v1" to "bad-json"))) {
            val disk = CartMemorySecureStore().apply { snapshot = SecureSnapshot(COMMITTED_REVISION, values) }
            assertTrue(runCatching { SecureCartDocument(disk).read() }.isFailure)
            assertEquals(values, disk.snapshot.values)
        }
    }

    @Test fun `sequence overflow rejects update and preserves prior record`() = runTest {
        val disk = CartMemorySecureStore()
        val document = SecureCartDocument(disk)
        document.transaction { saveLastId(Long.MAX_VALUE) }
        val before = disk.snapshot
        assertTrue(runCatching { document.transaction { nextId(0) } }.isFailure)
        assertEquals(before, disk.snapshot)
    }
}

internal class CartMemorySecureStore : SecureRecordStore {
    private val mutex = Mutex()
    var snapshot = SecureSnapshot(0, emptyMap())
    var failWrite = false
    override suspend fun read(): SecureSnapshot = mutex.withLock { snapshot }
    override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot = mutex.withLock {
        val candidate = transform(snapshot.values)
        check(!failWrite) { "controlled disk failure" }
        SecureSnapshot(snapshot.revision + 1, candidate.toMap()).also { snapshot = it }
    }
}

private const val OWNER_KEY_LENGTH = 64
private const val IMPORTED_HIGH_WATER = 90L
private const val NEXT_IMPORTED_ID = 91L
private const val RESERVED_RECEIPT_ID = 400L
private const val NEXT_RESERVED_ID = 401L
private const val CONCURRENT_WRITES = 40
private const val COMMITTED_REVISION = 8L
