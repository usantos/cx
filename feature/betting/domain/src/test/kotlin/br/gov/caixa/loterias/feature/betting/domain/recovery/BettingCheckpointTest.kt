// Massas numéricas descrevem seleções de loteria, identificadores de concursos e limites de esquema.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting.domain.recovery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
class BettingCheckpointTest {
    @Test fun `checkpoint preserves partial sports and all editable fields through primitive document`() {
        val checkpoint = BettingCheckpoint(
            modalityId = 14, contestType = "NORMAL", contestNumber = 100,
            mode = "SURPRISE", step = "COMPLEMENT", numbers = setOf(1, 2), clovers = setOf(1),
            teamNumber = 33, teamQuery = "query", monthNumber = 2,
            loteca = mapOf(1 to setOf("HOME", "DRAW")), lotogol = mapOf(1 to (2 to null), 2 to (null to 0)),
            superSeven = List(7) { setOf(it) }, mirror = true, numberQuantity = 9,
            recurringDraws = 3, surpriseQuantity = 4, surpriseCloverQuantity = 2,
            manualCloverQuantity = 3, favoriteName = "Meu favorito", favoriteExpanded = true
        )
        assertEquals(checkpoint, BettingCheckpointCodec.decode(BettingCheckpointCodec.encode(checkpoint)))
    }

    @Test fun `invalid version or unbounded query cannot replace a recoverable checkpoint`() {
        val valid = BettingCheckpointCodec.encode(BettingCheckpoint(2, "NORMAL", 100))
        assertThrows(IllegalArgumentException::class.java) { BettingCheckpointCodec.decode(valid + ("version" to "99")) }
        assertThrows(IllegalArgumentException::class.java) {
            BettingCheckpointCodec.encode(BettingCheckpoint(2, "NORMAL", 100, teamQuery = "x".repeat(257)))
        }
    }

    @Test fun `strict decoding rejects unknown flags scores and excess columns`() {
        val valid = BettingCheckpointCodec.encode(BettingCheckpoint(2, "NORMAL", 100))
        listOf("mirror" to "yes", "lotogol" to "1:100:-", "superSeven" to "1;2;3;4;5;6;7;8").forEach {
            assertThrows(IllegalArgumentException::class.java) { BettingCheckpointCodec.decode(valid + it) }
        }
    }
}
