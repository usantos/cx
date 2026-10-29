package br.gov.caixa.loterias.feature.cart.data.datasource.local

import android.content.Context
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CartMigrationSourceTest {
    private val context: Context get() = RuntimeEnvironment.getApplication()

    @Before fun clean() {
        context.deleteDatabase("Loterias.db")
    }

    @Test fun `absent database migration never creates plaintext database`() {
        assertNull(UnassignedCartSource(context).read())
        assertFalse(context.getDatabasePath("Loterias.db").exists())
    }

    @Test fun `verified cleanup removes only captured graph roots preserving shared and unknown tables`() {
        fixture()
        val source = UnassignedCartSource(context)
        val state = source.read()!!
        assertEquals(IMPORTED_HIGH_WATER, state.lastId)
        assertEquals(listOf("01/10/2026 12:00"), state.purchases["15"])
        source.cleanup(state)
        context.openOrCreateDatabase("Loterias.db", 0, null).use { db ->
            db.rawQuery("SELECT COUNT(*) FROM $LEGACY_CART_TABLE", null).use {
                assertTrue(it.moveToFirst())
                assertEquals(0, it.getInt(0))
            }
            for (table in listOf("tb_equipe", "unknown_owned_elsewhere")) {
                db.rawQuery("SELECT COUNT(*) FROM $table", null).use {
                    assertTrue(it.moveToFirst())
                    assertEquals(1, it.getInt(0))
                }
            }
        }
    }

    @Test fun `changed original data aborts cleanup preserving both versions`() {
        fixture()
        val source = UnassignedCartSource(context)
        val expected = source.read()!!
        context.openOrCreateDatabase("Loterias.db", 0, null).use {
            it.execSQL("UPDATE $LEGACY_CART_TABLE SET numeros_selecionados='[2,3,4,5,6,7]' WHERE _id=1")
        }
        assertTrue(runCatching { source.cleanup(expected) }.isFailure)
        assertEquals("[2,3,4,5,6,7]", source.read()!!.tables.getValue(LEGACY_CART_TABLE).single()["numeros_selecionados"])
    }

    @Test fun `fractional or nonnumeric allocator metadata is rejected without cleaning original graph`() {
        fixture()
        for (invalid in listOf("1.5", "not-an-integer")) {
            context.openOrCreateDatabase("Loterias.db", 0, null).use {
                it.execSQL("UPDATE compose_visitor_cart_sequence_v1 SET last_id=?", arrayOf(invalid))
            }
            assertTrue(runCatching { UnassignedCartSource(context).read() }.isFailure)
            context.openOrCreateDatabase("Loterias.db", 0, null).use { db ->
                db.rawQuery("SELECT COUNT(*) FROM $LEGACY_CART_TABLE", null).use {
                    assertTrue(it.moveToFirst())
                    assertEquals(1, it.getInt(0))
                }
            }
        }
    }

    @Test fun `fractional receipt metadata never becomes a valid transfer receipt`() {
        fixture()
        context.openOrCreateDatabase("Loterias.db", 0, null).use {
            it.execSQL(
                "CREATE TABLE compose_cart_transfer_receipt_v1(" +
                    "slot INTEGER,transfer_id TEXT,owner_key TEXT,phase TEXT,encoding_version TEXT)"
            )
            it.execSQL("CREATE TABLE compose_cart_transfer_items_v1(local_id TEXT,fingerprint TEXT)")
            it.execSQL(
                "INSERT INTO compose_cart_transfer_receipt_v1 VALUES(1,'pending',?,'PENDING','1')",
                arrayOf("a".repeat(OWNER_KEY_LENGTH))
            )
            it.execSQL("INSERT INTO compose_cart_transfer_items_v1 VALUES('1.5',?)", arrayOf("b".repeat(OWNER_KEY_LENGTH)))
        }
        assertTrue(runCatching { UnassignedCartSource(context).read() }.isFailure)
        context.openOrCreateDatabase("Loterias.db", 0, null).use {
            it.execSQL("UPDATE compose_cart_transfer_items_v1 SET local_id='1'")
            it.execSQL("UPDATE compose_cart_transfer_receipt_v1 SET encoding_version='1.5'")
        }
        assertTrue(runCatching { UnassignedCartSource(context).read() }.isFailure)
    }

    private fun fixture() = context.openOrCreateDatabase("Loterias.db", 0, null).use { db ->
        db.execSQL("CREATE TABLE $LEGACY_CART_TABLE (_id INTEGER PRIMARY KEY, modalidade TEXT, numeros_selecionados TEXT)")
        db.execSQL("INSERT INTO $LEGACY_CART_TABLE VALUES(1,'mega-sena','[1,2,3,4,5,6]')")
        db.execSQL("CREATE TABLE tb_equipe (_id INTEGER PRIMARY KEY, nome TEXT)")
        db.execSQL("INSERT INTO tb_equipe VALUES(9,'shared')")
        db.execSQL("CREATE TABLE unknown_owned_elsewhere (_id INTEGER PRIMARY KEY)")
        db.execSQL("INSERT INTO unknown_owned_elsewhere VALUES(1)")
        db.execSQL("CREATE TABLE compose_visitor_cart_sequence_v1 (slot INTEGER PRIMARY KEY,last_id INTEGER)")
        db.execSQL("INSERT INTO compose_visitor_cart_sequence_v1 VALUES(1,80)")
        db.execSQL("CREATE TABLE tb_ultima_compra (id INTEGER PRIMARY KEY,valor REAL,data TEXT)")
        db.execSQL("INSERT INTO tb_ultima_compra VALUES(1,15.0,'01/10/2026 12:00')")
    }
}

private const val OWNER_KEY_LENGTH = 64

private const val IMPORTED_HIGH_WATER = 80L
