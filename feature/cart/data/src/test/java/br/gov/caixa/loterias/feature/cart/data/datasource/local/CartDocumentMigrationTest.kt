package br.gov.caixa.loterias.feature.cart.data.datasource.local

import br.gov.caixa.loterias.feature.cart.data.repository.transferGraph
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CartDocumentMigrationTest {
    @Test fun `unknown environment is quarantined with receipt and high water before cleanup`() = runTest {
        val disk = CartMemorySecureStore()
        val original = CartMigrationSnapshot(
            transferGraph(),
            CartReceipt(
                "pending",
                "a".repeat(OWNER_KEY_LENGTH),
                CartReceiptPhase.PENDING,
                mapOf(1L to "b".repeat(OWNER_KEY_LENGTH))
            ),
            IMPORTED_HIGH_WATER,
            mapOf("15" to listOf("01/10/2026 12:00"))
        )
        val source = MemoryCartMigrationSource(original)
        CartDocumentMigration(SecureCartDocument(disk), source).migrate()
        assertTrue(source.cleaned)
        SecureCartDocument(disk).transaction {
            assertEquals(original.tables, tables())
            assertEquals(original.receipt, receipt())
            assertEquals(NEXT_IMPORTED_ID, nextId(2))
        }
        assertTrue(SecureCartDocument(disk).requiresLegacyReview())
    }

    @Test fun `advisory legacy records stay quarantined without blocking an unrelated guest cart`() = runTest {
        val disk = CartMemorySecureStore()
        val document = SecureCartDocument(disk)
        val source = MemoryCartMigrationSource(
            CartMigrationSnapshot(
                emptyMap(),
                null,
                0,
                mapOf("15" to listOf("01/10/2026 12:00"))
            )
        )
        CartDocumentMigration(document, source).migrate()
        assertTrue(document.requiresLegacyReview())
        assertFalse(document.requiresCartRecovery())
        assertEquals(listOf("01/10/2026 12:00"), document.read().purchases["15"])
        assertTrue(SecureCartDocument(CartMemorySecureStore()).read().purchases.isEmpty())
    }

    @Test fun `unresolved receipt blocks cart recovery even when its graph has already been removed`() = runTest {
        val document = SecureCartDocument(CartMemorySecureStore())
        val source = MemoryCartMigrationSource(
            CartMigrationSnapshot(
                emptyMap(),
                CartReceipt(
                    "uncertain",
                    "a".repeat(OWNER_KEY_LENGTH),
                    CartReceiptPhase.PENDING,
                    mapOf(RESERVED_LOCAL_ID to "b".repeat(OWNER_KEY_LENGTH))
                ),
                RESERVED_LOCAL_ID,
                emptyMap()
            )
        )
        CartDocumentMigration(document, source).migrate()
        assertTrue(document.requiresCartRecovery())
    }

    @Test fun `failed encrypted migration preserves every legacy row and retries safely`() = runTest {
        val disk = CartMemorySecureStore().apply { failWrite = true }
        val source = MemoryCartMigrationSource(CartMigrationSnapshot(transferGraph(), null, RESERVED_LOCAL_ID, emptyMap()))
        val migration = CartDocumentMigration(SecureCartDocument(disk), source)
        assertTrue(runCatching { migration.migrate() }.isFailure)
        assertFalse(source.cleaned)
        disk.failWrite = false
        migration.migrate()
        assertTrue(source.cleaned)
    }

    @Test fun `restart after commit does not reimport older data over later changes`() = runTest {
        val disk = CartMemorySecureStore()
        val source = MemoryCartMigrationSource(
            CartMigrationSnapshot(transferGraph(), null, RESERVED_LOCAL_ID, emptyMap())
        ).apply { failCleanup = true }
        val document = SecureCartDocument(disk)
        assertTrue(runCatching { CartDocumentMigration(document, source).migrate() }.isFailure)
        document.transaction { saveLastId(UPDATED_HIGH_WATER) }
        source.failCleanup = false
        CartDocumentMigration(SecureCartDocument(disk), source).migrate()
        document.transaction { assertEquals(NEXT_UPDATED_ID, nextId(2)) }
        assertTrue(source.cleaned)
    }

    private class MemoryCartMigrationSource(private val state: CartMigrationSnapshot) : CartMigrationSource {
        var cleaned = false
        var failCleanup = false
        override fun read(): CartMigrationSnapshot? = if (cleaned) null else state
        override fun cleanup(expected: CartMigrationSnapshot) {
            check(expected == state)
            check(!failCleanup) { "controlled cleanup interruption" }
            cleaned = true
        }
    }
}

private const val OWNER_KEY_LENGTH = 64
private const val IMPORTED_HIGH_WATER = 99L
private const val NEXT_IMPORTED_ID = 100L
private const val RESERVED_LOCAL_ID = 8L
private const val UPDATED_HIGH_WATER = 100L
private const val NEXT_UPDATED_ID = 101L
