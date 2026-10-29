// Massas numéricas descrevem seleções de loteria, identificadores de concursos e limites de esquema.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting.recovery

import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingRecoveryRecord
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingMutationBarrier
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
class BettingMutationBarrierTest {
    @Test fun `write failure prevents remote mutation and successful remote completes intent before result`() = runTest {
        val events = mutableListOf<String>()
        var failWrites = true
        val session = object : BettingCheckpointSession {
            override fun isCurrent() = true
            override suspend fun read(): BettingRecoveryRecord? = null
            override suspend fun save(checkpoint: BettingCheckpoint) = Unit
            override suspend fun beginOperation(id: String, kind: String, checkpoint: BettingCheckpoint) = Unit
            override suspend fun beginOperation(id: String, kind: String) {
                events += "intent"
                if (failWrites) throw java.io.IOException()
            }
            override suspend fun completeOperation(id: String) {
                events += "confirmed"
            }
        }
        val barrier = BettingMutationBarrier(session)
        try {
            barrier.run("GENERATE_RAPID", { _: Boolean -> true }) {
                events += "remote"
                true
            }
            fail("Expected write failure")
        } catch (
            expected: java.io.IOException,
        ) {
            assertEquals(java.io.IOException::class.java, expected.javaClass)
        }
        assertEquals(listOf("intent"), events)
        failWrites = false
        events.clear()
        assertTrue(
            barrier.run("GENERATE_RAPID", { it }) {
                events += "remote"
                true
            }
        )
        assertEquals(listOf("intent", "remote", "confirmed"), events)
    }
}
