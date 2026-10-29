package br.gov.caixa.loterias.feature.betting

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetShakeEnabledUseCase
import br.gov.caixa.loterias.feature.betting.presentation.state.NavigationPreferenceFailure
import br.gov.caixa.loterias.feature.betting.presentation.state.NavigationPreferenceMode
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidCompletion
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingNavigationPreferencesViewModel
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BettingNavigationPreferencesViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `tutorial read failure is recoverable without exposing an initial page`() = runTest(dispatcher) {
        var fail = true
        val vm = model(readTutorial = { if (fail) throw SecureStorageException.ReadFailure() else true })
        advanceUntilIdle()
        assertNull(vm.state.value.tutorialDismissed)
        assertEquals(NavigationPreferenceFailure.READ, vm.state.value.failure)
        assertFalse(vm.state.value.loading)
        fail = false
        vm.retry()
        advanceUntilIdle()
        assertEquals(true, vm.state.value.tutorialDismissed)
        assertNull(vm.state.value.failure)
    }

    @Test fun `shake read failure keeps shake disabled and retries the read`() = runTest(dispatcher) {
        var fail = true
        val vm =
            model(mode = NavigationPreferenceMode.SHAKE, readShake = { if (fail) throw SecureStorageException.KeyUnavailable() else true })
        advanceUntilIdle()
        assertFalse(vm.state.value.shakeEnabled)
        assertEquals(NavigationPreferenceFailure.READ, vm.state.value.failure)
        fail = false
        vm.retry()
        advanceUntilIdle()
        assertTrue(vm.state.value.shakeEnabled)
    }

    @Test fun `reactivating a shake destination disables the old preference until reread succeeds`() = runTest(dispatcher) {
        var fail = false
        val vm = model(mode = NavigationPreferenceMode.SHAKE, readShake = {
            if (fail) throw SecureStorageException.ReadFailure() else true
        })
        advanceUntilIdle()
        assertTrue(vm.state.value.shakeEnabled)
        vm.deactivate()
        fail = true
        vm.activate()
        assertFalse(vm.state.value.shakeEnabled)
        advanceUntilIdle()
        assertFalse(vm.state.value.shakeEnabled)
        assertEquals(NavigationPreferenceFailure.READ, vm.state.value.failure)
    }

    @Test fun `tutorial write failure never navigates and retry only repeats preference completion`() = runTest(dispatcher) {
        var fail = true
        var writes = 0
        val vm = model(readTutorial = { true }, writeTutorial = {
            writes++
            if (fail) throw SecureStorageException.WriteFailure()
        })
        advanceUntilIdle()
        vm.completeRapidConfiguration(returnToPrevious = true)
        advanceUntilIdle()
        assertNull(vm.state.value.rapidCompletion)
        assertEquals(NavigationPreferenceFailure.WRITE, vm.state.value.failure)
        fail = false
        vm.retry()
        advanceUntilIdle()
        assertEquals(2, writes)
        assertEquals(RapidCompletion.POP_BACK, vm.state.value.rapidCompletion)
        vm.acknowledgeCompletion()
        assertNull(vm.state.value.rapidCompletion)
    }

    @Test fun `first tutorial completion opens list even from explicit configuration`() = runTest(dispatcher) {
        val vm = model()
        advanceUntilIdle()
        vm.completeRapidConfiguration(returnToPrevious = true)
        advanceUntilIdle()
        assertEquals(RapidCompletion.OPEN_LIST, vm.state.value.rapidCompletion)
    }

    @Test fun `completion rereads persisted tutorial before choosing return navigation`() = runTest(dispatcher) {
        var dismissed = false
        val vm = model(readTutorial = { dismissed })
        advanceUntilIdle()
        dismissed = true
        vm.completeRapidConfiguration(returnToPrevious = true)
        advanceUntilIdle()
        assertEquals(RapidCompletion.POP_BACK, vm.state.value.rapidCompletion)
    }

    @Test fun `completion read failure retries the completion without publishing navigation`() = runTest(dispatcher) {
        var fail = false
        var writes = 0
        val vm = model(readTutorial = { if (fail) throw SecureStorageException.ReadFailure() else true }, writeTutorial = { writes++ })
        advanceUntilIdle()
        fail = true
        vm.completeRapidConfiguration(returnToPrevious = true)
        advanceUntilIdle()
        assertNull(vm.state.value.rapidCompletion)
        assertEquals(0, writes)
        assertEquals(NavigationPreferenceFailure.READ, vm.state.value.failure)
        fail = false
        vm.retry()
        advanceUntilIdle()
        assertEquals(1, writes)
        assertEquals(RapidCompletion.POP_BACK, vm.state.value.rapidCompletion)
    }

    @Test fun `duplicate saved callbacks serialize preference completion`() = runTest(dispatcher) {
        val result = CompletableDeferred<Unit>()
        var writes = 0
        val vm = model(writeTutorial = {
            writes++
            result.await()
        })
        advanceUntilIdle()
        vm.completeRapidConfiguration(returnToPrevious = false)
        vm.completeRapidConfiguration(returnToPrevious = false)
        runCurrent()
        assertEquals(1, writes)
        result.complete(Unit)
        advanceUntilIdle()
        assertEquals(RapidCompletion.OPEN_LIST, vm.state.value.rapidCompletion)
    }

    @Test fun `replaced session cannot publish delayed preference read`() = runTest(dispatcher) {
        var current = true
        val result = CompletableDeferred<Boolean>()
        val vm = model(readTutorial = { result.await() }, isCurrent = { current })
        runCurrent()
        current = false
        result.complete(true)
        advanceUntilIdle()
        assertNull(vm.state.value.tutorialDismissed)
    }

    @Test fun `cleared destination cannot publish non cooperative completion`() = runTest(dispatcher) {
        val result = CompletableDeferred<Unit>()
        val vm = model(writeTutorial = { withContext(NonCancellable) { result.await() } })
        val store = ViewModelStore().apply { put("preferences", vm) }
        advanceUntilIdle()
        vm.completeRapidConfiguration(returnToPrevious = false)
        runCurrent()
        store.clear()
        result.complete(Unit)
        advanceUntilIdle()
        assertNull(vm.state.value.rapidCompletion)
    }

    private fun model(
        mode: NavigationPreferenceMode = NavigationPreferenceMode.RAPID,
        readTutorial: suspend () -> Boolean = { false },
        writeTutorial: suspend () -> Unit = {},
        readShake: suspend () -> Boolean = { false },
        isCurrent: () -> Boolean = { true },
    ) = BettingNavigationPreferencesViewModel(
        mode,
        GetBettingTutorialDismissedUseCase { readTutorial() },
        SetBettingTutorialDismissedUseCase { _, _ -> writeTutorial() },
        GetShakeEnabledUseCase { readShake() },
        FixedCoroutineDispatchers(dispatcher),
        isCurrent
    )
}
