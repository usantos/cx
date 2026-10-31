// Massas numéricas descrevem seleções de loteria, identificadores de concursos e limites de esquema.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting.recovery

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpointSession
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingRecoveryRecord
import br.gov.caixa.loterias.feature.betting.domain.recovery.BettingUncertainOperation
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
@OptIn(ExperimentalCoroutinesApi::class)
class BettingGameDurabilityTest {
    private val game = LotteryGameParameters(
        LotteryModality(2, "Mega", "MEGA_SENA"), 100, "", 60,
        6, 6, 10, listOf(0, 3), listOf(BetPrice(6, 0, BigDecimal.ONE))
    )
    private class Session(var record: BettingRecoveryRecord? = null) : BettingCheckpointSession {
        var gate: CompletableDeferred<Unit>? = null
        var fail = false
        var writes = 0
        var current = true
        override fun isCurrent() = current
        override suspend fun read() = record
        override suspend fun save(checkpoint: BettingCheckpoint) {
            writes++
            gate?.await()
            if (fail) throw java.io.IOException()
            record = BettingRecoveryRecord(checkpoint)
        }
        override suspend fun beginOperation(id: String, kind: String, checkpoint: BettingCheckpoint) {
            if (fail) throw java.io.IOException()
            record = BettingRecoveryRecord(checkpoint, BettingUncertainOperation(id, kind))
        }
        override suspend fun completeOperation(id: String) {
            record = BettingRecoveryRecord(null, reconcileCart = true)
        }
    }

    @Test fun `game tap remains invisible until durable commit and queued taps keep order`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val session = Session()
            val model =
                BettingGameViewModel(
                    2,
                    GetBettingParametersUseCase {
                        AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(game)))
                    },
                    AddBetToCartUseCase {
                        error("No remote calls")
                    },
                    dispatchers = FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
                    checkpointSession = session
                )
            advanceUntilIdle()
            session.gate = CompletableDeferred()
            model.toggleNumber(1)
            model.toggleNumber(2)
            runCurrent()
            assertTrue(model.state.value.selectedNumbers.isEmpty())
            session.gate!!.complete(Unit)
            advanceUntilIdle()
            assertEquals(setOf(1, 2), model.state.value.selectedNumbers)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test fun `random numeric bets publish and persist every modality without replacing manual picks`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val modalities = listOf(
                Triple(2, 60, 6),
                Triple(3, 80, 5),
                Triple(16, 100, 50),
                Triple(18, 50, 6),
                Triple(17, 25, 15),
                Triple(20, 80, 10),
                Triple(11, 31, 7),
                Triple(9, 50, 6)
            )
            val games = modalities.flatMap { (id, maximum, quantity) ->
                listOf(true, false).map { open ->
                    game.copy(
                        modality = LotteryModality(id, "Modalidade $id", ""),
                        maximumNumber = maximum,
                        minimumSelection = quantity,
                        maximumSelection = quantity,
                        prices = listOf(BetPrice(quantity, 0, BigDecimal.ONE)),
                        open = open
                    )
                }
            }
            for (parameters in games) {
                val id = parameters.modality.id
                val session = Session()
                var submissions = 0
                val model = BettingGameViewModel(
                    id,
                    GetBettingParametersUseCase {
                        AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(parameters)))
                    },
                    AddBetToCartUseCase {
                        submissions++
                        error("Não deve enviar aposta")
                    },
                    dispatchers = FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
                    checkpointSession = session
                )
                advanceUntilIdle()
                model.fillShake(false)
                advanceUntilIdle()
                assertEquals(parameters.minimumSelection, model.state.value.selectedNumbers.size)
                model.clearSelection()
                model.toggleNumber(1)
                model.fillShake(false)
                advanceUntilIdle()
                val selected = model.state.value.selectedNumbers
                assertEquals("Modalidade $id", parameters.minimumSelection, selected.size)
                assertTrue("Modalidade $id", 1 in selected && selected.all { it in 1..parameters.maximumNumber })
                assertEquals(selected, session.record?.checkpoint?.numbers)
                assertEquals(null, model.state.value.errorMessage)
                model.fillShake(true)
                advanceUntilIdle()
                assertEquals(parameters.minimumSelection, model.state.value.selectedNumbers.size)
                assertTrue(selected != model.state.value.selectedNumbers)
                if (!parameters.open) {
                    assertFalse(model.state.value.canAddToCart)
                    model.addToCart()
                    advanceUntilIdle()
                    assertNotNull(model.state.value.errorMessage)
                    assertEquals(parameters.minimumSelection, model.state.value.selectedNumbers.size)
                }
                assertEquals(model.state.value.selectedNumbers, session.record?.checkpoint?.numbers)
                assertEquals(0, submissions)
            }
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test fun `rejected draft can leave through back and navigation without writing or submitting`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val draft = BettingCheckpoint(2, "NORMAL", 99, numbers = setOf(4), numberQuantity = 6)
            val session = Session(BettingRecoveryRecord(draft))
            val model = BettingGameViewModel(
                2,
                GetBettingParametersUseCase {
                    AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(game)))
                },
                AddBetToCartUseCase { error("Não deve enviar aposta") },
                dispatchers = FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
                checkpointSession = session
            )
            advanceUntilIdle()
            var exits = 0
            model.requestBack { exits++ }
            advanceUntilIdle()
            assertEquals(1, exits)
            model.checkpointNavigation { exits++ }
            advanceUntilIdle()
            assertEquals(2, exits)
            session.current = false
            model.requestBack { exits++ }
            model.checkpointNavigation { exits++ }
            advanceUntilIdle()
            assertEquals(2, exits)
            assertEquals(draft, session.record?.checkpoint)
            assertEquals(0, session.writes)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test fun `explicit current contest entry does not restore an obsolete draft from the same modality`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val draft = BettingCheckpoint(2, "NORMAL", 99, numbers = setOf(4), numberQuantity = 6)
            val session = Session(BettingRecoveryRecord(draft))
            val model = BettingGameViewModel(
                2,
                GetBettingParametersUseCase {
                    AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(game)))
                },
                AddBetToCartUseCase { error("Não deve enviar aposta") },
                dispatchers = FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
                checkpointSession = session,
                expectedContestNumber = game.contestNumber
            )
            advanceUntilIdle()
            assertEquals(game, model.state.value.game)
            assertTrue(model.state.value.selectedNumbers.isEmpty())
            assertEquals(null, model.state.value.recoveryReview)
            assertEquals(draft, session.record?.checkpoint)
            model.toggleNumber(5)
            advanceUntilIdle()
            assertEquals(game.contestNumber, session.record?.checkpoint?.contestNumber)
            assertEquals(setOf(5), session.record?.checkpoint?.numbers)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test fun `expired contest preserves encrypted selection without substituting refreshed contest`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val draft = BettingCheckpoint(2, "NORMAL", 99, numbers = setOf(4), numberQuantity = 6)
            val session = Session(BettingRecoveryRecord(draft))
            val model =
                BettingGameViewModel(
                    2,
                    GetBettingParametersUseCase {
                        AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(game)))
                    },
                    AddBetToCartUseCase {
                        error("No remote calls")
                    },
                    dispatchers = FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
                    checkpointSession = session
                )
            advanceUntilIdle()
            assertEquals(draft, session.record!!.checkpoint)
            assertEquals(draft, model.state.value.recoveryReview)
            assertEquals(0, session.writes)
            assertNotNull(model.state.value.errorMessage)
            assertFalse(model.state.value.canAddToCart)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test fun `closed sales restore the same contest for local editing without permitting registration`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val draft = BettingCheckpoint(2, "NORMAL", 100, numbers = (1..6).toSet(), numberQuantity = 6)
            val closedGames = listOf(game.copy(open = false), game.copy(closingDateTime = "2026-10-21T10:00:00-03:00"))
            for (closed in closedGames) {
                val session = Session(BettingRecoveryRecord(draft))
                var submissions = 0
                val model = BettingGameViewModel(
                    2,
                    GetBettingParametersUseCase {
                        AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(closed)))
                    },
                    AddBetToCartUseCase {
                        submissions++
                        error("Não deve enviar aposta")
                    },
                    dispatchers = FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
                    checkpointSession = session,
                    serverTime = ServerTimeSource { Instant.parse("2026-10-22T10:00:00Z") }
                )
                advanceUntilIdle()
                assertEquals(draft.numbers, model.state.value.selectedNumbers)
                assertEquals(null, model.state.value.recoveryReview)
                assertEquals(null, model.state.value.errorMessage)
                assertTrue(model.state.value.numbersAreComplete)
                assertFalse(requireNotNull(model.state.value.game).open)
                assertFalse(model.state.value.canAddToCart)
                model.toggleNumber(1)
                model.fillShake(false)
                advanceUntilIdle()
                assertEquals(6, model.state.value.selectedNumbers.size)
                assertTrue(model.state.value.selectedNumbers.containsAll((2..6).toSet()))
                model.addToCart()
                advanceUntilIdle()
                assertEquals(0, submissions)
                assertFalse(model.state.value.addedToCart)
                assertEquals(model.state.value.selectedNumbers, session.record?.checkpoint?.numbers)
            }
        } finally {
            Dispatchers.resetMain()
        }
    }
}
