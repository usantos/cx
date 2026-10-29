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
        override fun isCurrent() = true
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

    @Test fun `server closing deadline prevents restoring an apparently open old contest`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val draft = BettingCheckpoint(2, "NORMAL", 100, numbers = setOf(4), numberQuantity = 6)
            val session = Session(BettingRecoveryRecord(draft))
            val closed = game.copy(closingDateTime = "2026-10-21T10:00:00-03:00")
            val model = BettingGameViewModel(
                2,
                GetBettingParametersUseCase {
                    AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, listOf(closed)))
                },
                AddBetToCartUseCase { error("No remote calls") },
                dispatchers = FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
                checkpointSession = session,
                serverTime = ServerTimeSource { Instant.parse("2026-10-22T10:00:00Z") }
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
}
