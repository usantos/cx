package br.gov.caixa.loterias.core.preferences

import br.gov.caixa.loterias.core.domain.cart.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class LegacyLocalCartReaderTest {
    private fun bet(vararg fields: Pair<String, String?>): LegacyCartRow = mapOf("_id" to "1", "modalidade" to "mega-sena") + fields

    @Test fun `snapshot preserves numeric selections and every scalar without mutating source`() = runTest {
        val row = bet("id_identificador" to "0", "tipo_concurso_valor" to "E", "tipo_concurso_descricao" to "Especial",
            "numeros_selecionados" to "[1,2,3,4,5,6]", "trevos_selecionados" to "[1,3]",
            "indicador_surpresinha_valor" to "2", "indicador_surpresinha_descricao" to "Parcial",
            "valor" to "35.50", "concurso_alvo" to "10", "quantidade_teimosinhas" to "5", "quantidade_apostas" to "3",
            "espelho" to "1", "gera_espelho" to "1", "concurso_inicial" to "8", "situacao_valor" to "2", "situacao_descricao" to "teste",
            "troca" to "1", "data_efetivacao" to "2026-09-07", "hora_efetivacao" to "10:00", "vinculo_espelho" to "90",
            "quantidade_numeros" to "6", "quantidade_trevos" to "2", "surpresinha" to "1", "quantidade_surpresinha" to "4")
        val tables = mapOf(LEGACY_CART_TABLE to listOf(row))
        val repository = LegacyLocalCartReader { tables }
        val value = repository.snapshot().single()
        assertEquals(1L, value.localId)
        assertNull(value.serverId)
        assertEquals(LegacyCartModality.MEGA_SENA, value.modality)
        assertEquals(LegacyCartCode("E", "Especial"), value.contestType)
        assertEquals(LegacyCartNumbers.Flat(listOf(1, 2, 3, 4, 5, 6)), value.numbers)
        assertEquals(listOf(1, 3), value.clovers)
        assertEquals(LegacyCartCode(2, "Parcial"), value.quickPickIndicator)
        assertEquals(BigDecimal("35.50"), value.amount)
        assertEquals(10, value.targetContest); assertEquals(5, value.repeatCount); assertEquals(3, value.betCount)
        assertTrue(value.mirrored); assertTrue(value.generateMirror); assertEquals(8, value.initialContest)
        assertEquals(LegacyCartCode(2L, "teste"), value.status)
        assertTrue(value.replacement); assertEquals("2026-09-07", value.effectiveDate); assertEquals("10:00", value.effectiveTime)
        assertEquals(90L, value.mirrorLink); assertEquals(6, value.numberCount); assertEquals(2, value.cloverCount)
        assertTrue(value.quickPick); assertEquals(4, value.quickPickCount)
        assertNull(value.favoriteTeam); assertNull(value.luckyMonth)
        assertTrue(value.loteca.isEmpty()); assertTrue(value.lotogol.isEmpty())
        assertEquals("LegacyLocalCartBet(redacted)", value.toString())
        assertEquals("LegacyCartNumbers.Flat(redacted)", value.numbers.toString())
        assertEquals("0", row["id_identificador"])
        assertEquals(value, repository.snapshot().single())
    }

    @Test fun `Super Seven columns and null selections are not flattened or invented`() {
        val value = decodeLegacyCart(mapOf(LEGACY_CART_TABLE to listOf(bet("modalidade" to "super sete", "numeros_selecionados" to "[[0,1],[],[2],[3],[4],[5],[6]]", "id_identificador" to "99")))).single()
        assertEquals(99L, value.serverId)
        assertEquals(LegacyCartNumbers.Columns(listOf(listOf(0, 1), emptyList(), listOf(2), listOf(3), listOf(4), listOf(5), listOf(6))), value.numbers)
        assertEquals("LegacyCartNumbers.Columns(redacted)", value.numbers.toString())
        for (selection in listOf(null, "", " ", "null")) {
            val empty = decodeLegacyCart(mapOf(LEGACY_CART_TABLE to listOf(bet("numeros_selecionados" to selection)))).single()
            assertNull(empty.numbers); assertNull(empty.serverId); assertNull(empty.clovers)
            assertEquals(BigDecimal.ZERO, empty.amount); assertFalse(empty.quickPick)
        }
    }

    @Test fun `legacy names map all modalities and unknown names fail instead of changing the game`() {
        val names = listOf("dia de sorte", "dupla sena", "loteca", "lotofácil", "lotogol", "lotomania", "mega-sena", "quina", "timemania", "super sete", "+Milionária", "instantânea", "combo", "bolão")
        assertEquals(LegacyCartModality.entries.toSet(), names.map(::localModality).toSet())
        for (alias in listOf("dupla-sena", "mais milionária", "maismilionária", "bolao")) localModality(alias)
        assertTrue(runCatching { localModality("unknown") }.isFailure)
    }

    @Test fun `team month and both sports graphs remain tied to their parent bet`() {
        val parameter = mapOf("_id" to "9", "numero" to "80", "nome" to "Time", "numero_pais" to "55", "pais" to "Brasil", "sigla_pais" to "BR", "uf" to "DF", "nome_class" to "team", "indicadorSelecao" to "1", "descricao_curta" to "T", "descricao_longa" to "Time teste")
        val team = parameter + mapOf("_id" to "20", "id_parametro_equipe" to "9", "indicador_selecao" to "1", "placar" to "2", "vitoria" to "1")
        val other = team + mapOf("_id" to "21", "id_parametro_equipe" to "0", "vitoria" to "0")
        val match = mapOf(LEGACY_CART_PARENT to "1", "id_equipe_um" to "20", "id_equipe_dois" to "21", "empate" to "1", "index_placar_equipe_um" to "2", "index_placar_equipe_dois" to "3")
        val tables = mapOf(LEGACY_CART_TABLE to listOf(bet("id_time_coracao" to "9", "id_mes_sorte" to "12")),
            "tb_parametro_equipe" to listOf(parameter), "tb_equipe" to listOf(team, other),
            "tb_mes_sorte" to listOf(mapOf("numero" to "12", "nome" to "Dezembro", "abreviacao" to "Dez")),
            "tb_partida_loteca" to listOf(match, match + (LEGACY_CART_PARENT to "2")), "tb_partida_lotogol" to listOf(match))
        val value = decodeLegacyCart(tables).single()
        assertEquals(LegacyCartMonth(12, "Dezembro", "Dez"), value.luckyMonth)
        assertEquals(LegacyCartTeam(80, "Time", 55, "Brasil", "BR", "DF", "team", true, "T", "Time teste"), value.favoriteTeam)
        assertEquals(1, value.loteca.size); assertTrue(value.loteca.single().draw)
        assertEquals(value.favoriteTeam, value.loteca.single().first.parameter)
        assertEquals("2", value.loteca.single().first.score); assertTrue(value.loteca.single().first.winner)
        assertNull(value.loteca.single().second.parameter); assertFalse(value.loteca.single().second.winner)
        assertEquals(value.favoriteTeam, value.loteca.single().first.team)
        assertEquals(2, value.lotogol.single().firstScoreIndex); assertEquals(3, value.lotogol.single().secondScoreIndex)
        assertEquals(value.loteca.single().first, value.lotogol.single().first)
        assertEquals(value.loteca.single().second, value.lotogol.single().second)
        for (missing in listOf("tb_equipe", "tb_parametro_equipe", "tb_mes_sorte")) {
            assertTrue(runCatching { decodeLegacyCart(tables - missing) }.isFailure)
        }
    }

    @Test fun `invalid rows and malformed selections never produce a partial success snapshot`() = runTest {
        for (bad in listOf(bet("_id" to "0"), bet("_id" to null), bet("modalidade" to null), bet("valor" to "bad"),
            bet("quantidade_apostas" to "2.5"), bet("numeros_selecionados" to "[[1]]"), bet("numeros_selecionados" to "{}"),
            bet("numeros_selecionados" to "[1.5]"), bet("numeros_selecionados" to "[true]"), bet("numeros_selecionados" to "[2147483648]"),
            bet("trevos_selecionados" to "[null]"), bet("modalidade" to "super sete", "numeros_selecionados" to "[1]"))) {
            assertTrue(runCatching { decodeLegacyCart(mapOf(LEGACY_CART_TABLE to listOf(bad))) }.isFailure)
        }
        assertTrue(runCatching { decodeLegacyCart(mapOf(LEGACY_CART_TABLE to listOf(bet(), bet()))) }.isFailure)
        assertTrue(LegacyLocalCartReader { emptyMap() }.snapshot().isEmpty())
        val failure = IllegalStateException("read failed")
        try { LegacyLocalCartReader { throw failure }.snapshot(); fail("Must propagate read failure") }
        catch (actual: Exception) { assertSame(failure, actual) }
    }
}
