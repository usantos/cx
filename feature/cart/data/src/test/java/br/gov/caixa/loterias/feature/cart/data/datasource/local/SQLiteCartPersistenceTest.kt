package br.gov.caixa.loterias.feature.cart.data.datasource.local

import android.content.Context
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_PARENT
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE
import br.gov.caixa.loterias.feature.cart.data.repository.transferGraph
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/** Acesso legado somente para leitura e contratos da substituição criptografada visíveis aos consumidores. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SQLiteCartPersistenceTest {
    private val context: Context get() = RuntimeEnvironment.getApplication()
    private val receipt = CartReceipt(
        "transfer-1",
        "a".repeat(OWNER_KEY_LENGTH),
        CartReceiptPhase.PENDING,
        mapOf(1L to "b".repeat(OWNER_KEY_LENGTH))
    )

    @Before fun clean() {
        context.deleteDatabase("Loterias.db")
    }

    @Test fun `legacy read never creates missing database and retains nullable ordered graph`() = runTest {
        assertTrue(CartSnapshotLocalDataSourceImpl(context).read().isEmpty())
        assertFalse(context.getDatabasePath("Loterias.db").exists())
        context.openOrCreateDatabase("Loterias.db", 0, null).use {
            it.execSQL("CREATE TABLE $LEGACY_CART_TABLE (_id INTEGER PRIMARY KEY,label TEXT)")
            it.execSQL("INSERT INTO $LEGACY_CART_TABLE VALUES(2,NULL),(1,'first')")
            it.execSQL("CREATE TABLE tb_mes_sorte(numero INTEGER PRIMARY KEY,nome TEXT)")
            it.execSQL("INSERT INTO tb_mes_sorte VALUES(12,'Dezembro'),(1,'Janeiro')")
        }
        val snapshot = CartSnapshotLocalDataSourceImpl(context).read()
        assertEquals(listOf("1", "2"), snapshot.getValue(LEGACY_CART_TABLE).map { it["_id"] })
        assertNull(snapshot.getValue(LEGACY_CART_TABLE)[1]["label"])
        assertEquals(listOf("1", "12"), snapshot.getValue("tb_mes_sorte").map { it["numero"] })
        assertFalse("tb_equipe" in snapshot)
    }

    @Test fun `receipt replaces fingerprints and clears independently of graph`() = runTest {
        val disk = CartMemorySecureStore()
        val document = SecureCartDocument(disk)
        document.transaction {
            replaceTables(transferGraph())
            save(receipt)
        }
        val confirmed = receipt.copy(phase = CartReceiptPhase.CONFIRMED, fingerprints = mapOf(2L to "c".repeat(OWNER_KEY_LENGTH)))
        document.transaction { save(confirmed) }
        SecureCartDocument(disk).transaction {
            assertEquals(confirmed, receipt())
            clearReceipt()
            assertNull(receipt())
            assertEquals(2, tables().getValue(LEGACY_CART_TABLE).size)
        }
    }

    @Test fun `graph deletion rolls back together with receipt and preserves shared references`() = runTest {
        val document = SecureCartDocument(CartMemorySecureStore())
        document.transaction {
            replaceTables(transferGraph())
            save(receipt)
        }
        assertTrue(
            runCatching {
                document.transaction {
                    deleteBet(1)
                    clearReceipt()
                    error("crash")
                }
            }.isFailure
        )
        document.transaction {
            assertEquals(receipt, receipt())
            assertEquals(2, tables().getValue(LEGACY_CART_TABLE).size)
        }
        document.transaction { deleteBet(1) }
        document.transaction {
            assertEquals(listOf("2"), tables().getValue(LEGACY_CART_TABLE).map { it["_id"] })
            assertEquals(listOf("99"), tables().getValue("tb_partida_loteca").map { it[LEGACY_CART_PARENT] })
            assertEquals(2, tables().getValue("tb_equipe").size)
        }
        assertTrue(runCatching { document.transaction { deleteBet(0) } }.isFailure)
        assertTrue(runCatching { document.transaction { deleteBet(UNKNOWN_BET_ID) } }.isFailure)
    }

    @Test fun `invalid receipt cannot replace prior durable record`() = runTest {
        val disk = CartMemorySecureStore()
        val document = SecureCartDocument(disk)
        document.transaction { save(receipt) }
        for (invalid in listOf(
            receipt.copy(transferId = ""),
            receipt.copy(ownerKey = "bad"),
            receipt.copy(fingerprints = emptyMap()),
            receipt.copy(fingerprints = mapOf(0L to "a".repeat(OWNER_KEY_LENGTH))),
            receipt.copy(fingerprints = mapOf(1L to "bad"))
        )) {
            assertTrue(runCatching { document.transaction { save(invalid) } }.isFailure)
            document.transaction { assertEquals(receipt, receipt()) }
        }
    }
}

private const val OWNER_KEY_LENGTH = 64
private const val UNKNOWN_BET_ID = 999L
