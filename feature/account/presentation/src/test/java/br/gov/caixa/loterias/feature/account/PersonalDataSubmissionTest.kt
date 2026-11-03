package br.gov.caixa.loterias.feature.account

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PersonalDataSubmissionTest : PersonalDataViewModelFixture() {
    @Test fun `two submissions before IO starts reserve one operation and preserve success`() = runTest {
        val io = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        var calls = 0
        val model = viewModel(ioDispatcher = io, update = { _, _ ->
            calls++
            if (calls == 1) AppResult.Success(Unit) else AppResult.Failure(AppError.Network)
        })
        io.scheduler.advanceUntilIdle()
        model.submit()
        model.submit()
        io.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        assertTrue(model.state.value.saved)
        assertEquals(null, model.state.value.errorMessage)
        val terminal = model.state.value
        io.scheduler.advanceUntilIdle()
        assertEquals(terminal, model.state.value)
    }

    @Test fun `a queued profile refresh cannot release an unfinished personal data update`() = runTest {
        val io = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        val pending = CompletableDeferred<AppResult<Unit>>()
        var updates = 0
        val model = viewModel(ioDispatcher = io, update = { _, _ ->
            updates++
            pending.await()
        })
        io.scheduler.advanceUntilIdle()
        model.onResume()
        model.onResume()
        model.submit()
        io.scheduler.runCurrent()
        model.submit()
        io.scheduler.runCurrent()
        pending.complete(AppResult.Success(Unit))
        io.scheduler.advanceUntilIdle()
        assertEquals(1, updates)
        assertTrue(model.state.value.saved)
        assertNull(model.state.value.errorMessage)
    }

    @Test fun `disabled module keeps edited personal data and rejects another update`() = runTest(dispatcherRule.dispatcher) {
        var enabled = true
        var writes = 0
        var recalculations = 0
        val model = viewModel(update = { _, _ ->
            writes++
            AppResult.Success(Unit)
        }, canStartWrite = { enabled }, recalculate = {
            recalculations++
            AppResult.Success(BigDecimal("300"))
        })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.changeGender(Gender.FEMALE)
        val draft = model.state.value
        enabled = false
        model.submit()
        model.recalculateLimit()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, writes)
        assertEquals(0, recalculations)
        assertEquals(draft, model.state.value)
        enabled = true
        model.submit()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, writes)
        assertTrue(model.state.value.saved)
    }

    @Test fun `personal data update already submitted finishes after module disabled`() = runTest(dispatcherRule.dispatcher) {
        var enabled = true
        var writes = 0
        val pending = CompletableDeferred<AppResult<Unit>>()
        val model = viewModel(update = { _, _ ->
            writes++
            pending.await()
        }, canStartWrite = { enabled })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        model.submit()
        dispatcherRule.dispatcher.scheduler.runCurrent()
        enabled = false
        model.submit()
        pending.complete(AppResult.Success(Unit))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, writes)
        assertTrue(model.state.value.saved)
        assertTrue(model.consumeCompletion())
    }

    @Test fun `module disabled while personal data waits for IO skips the update and keeps the edited draft`() = runTest {
        val io = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        var enabled = true
        var writes = 0
        val model = viewModel(ioDispatcher = io, update = { _, _ ->
            writes++
            AppResult.Success(Unit)
        }, canStartWrite = { enabled })
        io.scheduler.advanceUntilIdle()
        model.changeGender(Gender.FEMALE)
        val draft = model.state.value
        model.submit()
        enabled = false
        io.scheduler.advanceUntilIdle()
        assertEquals(0, writes)
        assertEquals(draft, model.state.value)
        enabled = true
        model.submit()
        io.scheduler.advanceUntilIdle()
        assertEquals(1, writes)
        assertTrue(model.state.value.saved)
    }
}
