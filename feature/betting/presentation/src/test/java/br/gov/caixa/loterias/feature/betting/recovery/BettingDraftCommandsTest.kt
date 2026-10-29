// Massas numéricas descrevem seleções de loteria, identificadores de concursos e limites de esquema.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting.recovery

import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.recovery.BettingDraftCommands
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
@OptIn(ExperimentalCoroutinesApi::class)
class BettingDraftCommandsTest {
    private val initial = BettingGameUiState(
        loading = false,
        game = LotteryGameParameters(
            LotteryModality(2, "Mega", "MEGA_SENA"), 100, "", 60, 6, 6, 10, listOf(0), emptyList()
        )
    )

    @Test fun `two taps are committed sequentially before visible acknowledgement and navigation`() = runTest {
        val gate = CompletableDeferred<Unit>()
        val writes = mutableListOf<Set<Int>>()
        val commands = BettingDraftCommands(
            this,
            initial,
            persist = {
                writes += it.selectedNumbers
                if (writes.size == 1) gate.await()
            },
            isCurrent = { true }
        )
        commands.edit(BettingGameAction.NumberToggled(1, warn = true))
        commands.edit(BettingGameAction.NumberToggled(2, warn = true))
        var navigated = false
        commands.navigate { navigated = true }
        runCurrent()
        assertTrue(commands.state.value.selectedNumbers.isEmpty())
        assertFalse(navigated)
        gate.complete(Unit)
        advanceUntilIdle()
        assertEquals(setOf(1, 2), commands.state.value.selectedNumbers)
        assertEquals(listOf(setOf(1), setOf(1, 2)), writes.take(2))
        assertTrue(navigated)
        commands.close()
    }

    @Test fun `failed checkpoint keeps acknowledged draft and prevents dependent navigation`() = runTest {
        val commands = BettingDraftCommands(
            this,
            initial,
            persist = { throw java.io.IOException("disk full") },
            isCurrent = { true }
        )
        commands.edit(BettingGameAction.NumberToggled(9, warn = true))
        var navigated = false
        commands.navigate { navigated = true }
        advanceUntilIdle()
        assertTrue(commands.state.value.selectedNumbers.isEmpty())
        assertNotNull(commands.state.value.errorMessage)
        assertFalse(navigated)
        commands.close()
    }

    @Test fun `lease revoked during write prevents private publication and navigation`() = runTest {
        var current = true
        val commands = BettingDraftCommands(
            this,
            initial,
            persist = { current = false },
            isCurrent = { current }
        )
        commands.edit(BettingGameAction.NumberToggled(9, warn = true))
        var navigated = false
        commands.navigate { navigated = true }
        advanceUntilIdle()
        assertTrue(commands.state.value.selectedNumbers.isEmpty())
        assertFalse(navigated)
        commands.close()
    }

    @Test fun `no op command cannot unlock navigation after a failed durable edit`() = runTest {
        var failWrites = true
        val commands = BettingDraftCommands(
            this,
            initial,
            persist = { if (failWrites) throw java.io.IOException() },
            isCurrent = { true }
        )
        commands.edit(BettingGameAction.NumberToggled(1, warn = true))
        advanceUntilIdle()
        failWrites = false
        commands.execute { }
        var navigated = false
        commands.navigate { navigated = true }
        advanceUntilIdle()
        assertFalse(navigated)
        commands.close()
    }
}
