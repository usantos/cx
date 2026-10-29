package br.gov.caixa.loterias.feature.cart.data.datasource.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartTableDataSource
import br.gov.caixa.loterias.feature.cart.data.datasource.local.readLegacyCartSnapshot
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartSnapshotRow

class CartSnapshotTest {
    @Test fun `reads only cart tables between two version checks`() {
        val events = mutableListOf<String>()
        val row = mapOf("_id" to "1")
        val source = object : CartTableDataSource {
            override fun dataVersion(): Long {
                events += "version"
                return 7
            }
            override fun rows(table: String): List<CartSnapshotRow>? {
                events += table
                return if (table == LEGACY_CART_TABLE) listOf(row) else null
            }
        }
        assertEquals(mapOf(LEGACY_CART_TABLE to listOf(row)), readLegacyCartSnapshot(source))
        assertEquals(
            listOf(
                "version",
                LEGACY_CART_TABLE,
                "tb_partida_loteca",
                "tb_partida_lotogol",
                "tb_equipe",
                "tb_parametro_equipe",
                "tb_mes_sorte",
                "version"
            ),
            events
        )
    }

    @Test fun `concurrent commit rejects whole snapshot without automatic retry`() {
        var versionReads = 0L
        var tableReads = 0
        val source = object : CartTableDataSource {
            override fun dataVersion() = versionReads++
            override fun rows(table: String): List<CartSnapshotRow> {
                tableReads++
                return emptyList()
            }
        }
        assertTrue(runCatching { readLegacyCartSnapshot(source) }.isFailure)
        assertEquals(2L, versionReads)
        assertEquals(6, tableReads)
    }

    @Test fun `query failure is not replaced with empty successful snapshot`() {
        val failure = IllegalStateException("test read failure")
        val source = object : CartTableDataSource {
            override fun dataVersion() = 1L
            override fun rows(table: String): List<CartSnapshotRow> = throw failure
        }
        assertSame(failure, runCatching { readLegacyCartSnapshot(source) }.exceptionOrNull())
    }
}
