package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.cart.data.repository.transferGraph
import br.gov.caixa.loterias.feature.cart.data.repository.transferBet

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.data.mapper.legacyCartFingerprint
import br.gov.caixa.loterias.feature.cart.data.mapper.LEGACY_CART_TABLE

class CartFingerprintTest {
    @Test fun `canonical fingerprint ignores map order and unrelated data but tracks entire associated graph`() {
        val graph = transferGraph()
        val fingerprint = legacyCartFingerprint(graph, 1)
        assertTrue(fingerprint!!.matches(Regex("[0-9a-f]{64}")))
        assertEquals(
            fingerprint,
            legacyCartFingerprint(
                graph.entries.reversed().associate { (table, rows) ->
                    table to rows.reversed().map { it.entries.reversed().associate { it.toPair() } }
                },
                1
            )
        )
        assertEquals(fingerprint, legacyCartFingerprint(graph + ("tb_usuario" to emptyList()), 1))
        assertEquals(
            fingerprint,
            legacyCartFingerprint(graph + (LEGACY_CART_TABLE to (graph.getValue(LEGACY_CART_TABLE) + transferBet(3))), 1)
        )
        for (table in listOf(
            LEGACY_CART_TABLE,
            "tb_partida_loteca",
            "tb_partida_lotogol",
            "tb_equipe",
            "tb_parametro_equipe",
            "tb_mes_sorte"
        )) {
            val changed =
                graph + (table to graph.getValue(table).mapIndexed { index, row -> if (index == 0) row + ("extra" to "novo") else row })
            assertNotEquals(table, fingerprint, legacyCartFingerprint(changed, 1))
        }
    }

    @Test fun `encoding distinguishes absent null empty delimiters and unicode fields`() {
        // Fixa a versão 1 da codificação: uma mudança de formato não deve invalidar silenciosamente recibos pendentes.
        assertEquals(
            "741ddbed8ed467700af4edfc9547af54df596006314691c7d29641704291fd86",
            legacyCartFingerprint(mapOf(LEGACY_CART_TABLE to listOf(transferBet(1))), 1)
        )
        val values = listOf(
            emptyMap(),
            mapOf("extra" to null),
            mapOf("extra" to ""),
            mapOf("extra" to "a|b"),
            mapOf("extra|a" to "b"),
            mapOf("extra" to "çã🙂")
        )
        val hashes = values.map { fields -> legacyCartFingerprint(mapOf(LEGACY_CART_TABLE to listOf(transferBet(1) + fields)), 1) }
        assertEquals(values.size, hashes.distinct().size)
        assertNull(legacyCartFingerprint(emptyMap(), 1))
        assertTrue(runCatching { legacyCartFingerprint(mapOf(LEGACY_CART_TABLE to listOf(transferBet(1), transferBet(1))), 1) }.isFailure)
    }
}
