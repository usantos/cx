package br.gov.caixa.loterias.feature.onboarding

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ShouldShowIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.state.StartupRecoveryTarget
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.StartupRecoveryNavigationViewModel
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
class StartupRecoveryNavigationViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `recovery reads introduction only after startup reports recovery`() = runTest(dispatcher) {
        var reads = 0
        val vm = model {
            reads++
            true
        }
        advanceUntilIdle()
        assertEquals(0, reads)
        assertNull(vm.state.value.target)
        vm.recover()
        assertTrue(vm.state.value.loading)
        advanceUntilIdle()
        assertEquals(StartupRecoveryTarget.INTRODUCTION, vm.state.value.target)
        assertFalse(vm.state.value.loading)
        vm.acknowledgeTarget()
        assertNull(vm.state.value.target)
    }

    @Test fun `completed introduction chooses login`() = runTest(dispatcher) {
        val vm = model { false }
        vm.recover()
        advanceUntilIdle()
        assertEquals(StartupRecoveryTarget.LOGIN, vm.state.value.target)
    }

    @Test fun `storage failure stays in Redux until explicit retry succeeds`() = runTest(dispatcher) {
        var fail = true
        val vm = model { if (fail) throw SecureStorageException.ReadFailure() else false }
        vm.recover()
        advanceUntilIdle()
        assertTrue(vm.state.value.storageFailed)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.target)
        fail = false
        vm.retry()
        advanceUntilIdle()
        assertFalse(vm.state.value.storageFailed)
        assertEquals(StartupRecoveryTarget.LOGIN, vm.state.value.target)
    }

    @Test fun `duplicate recovery callbacks share the active request`() = runTest(dispatcher) {
        var reads = 0
        val response = CompletableDeferred<Boolean>()
        val vm = model {
            reads++
            response.await()
        }
        vm.recover()
        vm.recover()
        runCurrent()
        assertEquals(1, reads)
        response.complete(true)
        advanceUntilIdle()
        assertEquals(StartupRecoveryTarget.INTRODUCTION, vm.state.value.target)
    }

    @Test fun `destination disposal rejects a non cooperative stale result`() = runTest(dispatcher) {
        val response = CompletableDeferred<Boolean>()
        val vm = model { withContext(NonCancellable) { response.await() } }
        vm.recover()
        runCurrent()
        vm.deactivate()
        response.complete(true)
        advanceUntilIdle()
        assertNull(vm.state.value.target)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `cleared owner cannot publish delayed result`() = runTest(dispatcher) {
        val response = CompletableDeferred<Boolean>()
        val vm = model { withContext(NonCancellable) { response.await() } }
        val store = ViewModelStore().apply { put("recovery", vm) }
        vm.recover()
        runCurrent()
        store.clear()
        response.complete(true)
        advanceUntilIdle()
        assertNull(vm.state.value.target)
    }

    private fun model(read: suspend () -> Boolean) = StartupRecoveryNavigationViewModel(
        ShouldShowIntroductionUseCase { read() },
        FixedCoroutineDispatchers(dispatcher)
    )
}
