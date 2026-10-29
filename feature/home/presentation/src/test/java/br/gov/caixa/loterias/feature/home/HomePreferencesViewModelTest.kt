package br.gov.caixa.loterias.feature.home

import kotlinx.coroutines.test.*
import org.junit.Assert.*
import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import java.util.concurrent.Executors
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomePreferencesViewModelTest {
    @get:Rule val rule = MainDispatcherRule()
    private val calls = mutableListOf<String>()
    private var saved: String? = "QUINA,UNKNOWN,MEGA_SENA,QUINA"
    private var enabled = false
    private var hidden = false
    private val tutorials = mutableMapOf<String?, Boolean>()
    private val tooltips = mutableMapOf<String?, Boolean>(null to true)
    private var fail = false
    private var beforeWrite: () -> Unit = {}
    private var onLoad: () -> Unit = {}
    private val layout = object : HomeLayoutRepository {
        override suspend fun loadOrder(): String? {
            onLoad()
            calls += "load"
            return saved
        }
        override suspend fun saveOrder(order: String) {
            calls += "order:$order"
            check(!fail)
            saved = order
        }
    }
    private val shake = object : ShakeSettingsRepository {
        override suspend fun enabled() = enabled
        override suspend fun setEnabled(enabled: Boolean) {
            beforeWrite()
            calls += "enabled:$enabled"
            check(!fail)
            this@HomePreferencesViewModelTest.enabled =
                enabled
        }
        override suspend fun warningHidden() = hidden
        override suspend fun setWarningHidden(hidden: Boolean) {
            calls += "hidden:$hidden"
            this@HomePreferencesViewModelTest.hidden =
                hidden
        }
        override suspend fun tooltipPending(ownerKey: String?) = tooltips[ownerKey] ?: false
        override suspend fun consumeTooltip(ownerKey: String?) {
            calls += "tooltip"
            tooltips[ownerKey] = false
        }
        override suspend fun tutorialCompleted(ownerKey: String?) = tutorials[ownerKey] ?: false
        override suspend fun completeTutorial(ownerKey: String?) {
            calls += "tutorial"
            tutorials[ownerKey] = true
            tooltips[ownerKey] =
                true
        }
    }
    private fun model(ownerKey: String? = null) =
        HomePreferencesViewModel(HomePreferencesUseCases(layout, shake), FixedCoroutineDispatchers(rule.dispatcher), ownerKey)

    @Test fun `failed order write retains last confirmed visual order`() = runTest(rule.dispatcher) {
        val vm = model()
        rule.dispatcher.scheduler.advanceUntilIdle()
        val original = vm.state.value.cardOrder
        fail = true
        vm.moveModality("QUINA", "MEGA_SENA")
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(original, vm.state.value.cardOrder)
        assertNotNull(vm.state.value.errorMessage)
    }

    @Test fun `load is dispatched and restores known modalities exactly once`() = runTest(rule.dispatcher) {
        val vm = model()
        assertTrue(calls.isEmpty())
        assertFalse(vm.state.value.loaded)
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.loaded)
        assertEquals(listOf("QUINA", "MEGA_SENA"), vm.state.value.cardOrder.take(2))
        assertEquals(HomePreferencesViewModel.DEFAULT_ORDER.size, vm.state.value.cardOrder.size)
        assertEquals(listOf("load"), calls)
    }

    @Test fun `moves persist in order and same or unknown targets never write`() = runTest(rule.dispatcher) {
        val vm = model()
        rule.dispatcher.scheduler.advanceUntilIdle()
        vm.moveModality("QUINA", "MEGA_SENA")
        vm.moveModality("MEGA_SENA", "LOTECA")
        vm.moveModality("UNKNOWN", "MEGA_SENA")
        vm.moveModality("QUINA", "QUINA")
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, calls.count { it.startsWith("order:") })
        assertEquals(vm.state.value.cardOrder.joinToString(","), saved)
    }

    @Test fun `shake confirmation writes warning before enabled and queued toggles use latest value`() = runTest(rule.dispatcher) {
        val vm = model()
        rule.dispatcher.scheduler.advanceUntilIdle()
        vm.toggleShake(true)
        vm.toggleShake()
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("load", "hidden:true", "enabled:true", "enabled:false"), calls)
        assertFalse(vm.state.value.shakeEnabled)
        assertTrue(vm.state.value.shakeWarningHidden)
    }

    @Test fun `tooltip and tutorial retain durable consumption ordering`() = runTest(rule.dispatcher) {
        val vm = model()
        rule.dispatcher.scheduler.advanceUntilIdle()
        vm.consumeShakeTooltip()
        vm.completeShakeTutorial()
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("load", "tooltip", "tutorial"), calls)
        assertTrue(vm.state.value.shakeTooltipPending)
        assertTrue(vm.state.value.shakeTutorialCompleted)
    }

    @Test fun `tutorial completion and pending tooltip are independent for visitor and signed in mode`() = runTest(rule.dispatcher) {
        val visitor = model()
        rule.dispatcher.scheduler.advanceUntilIdle()
        visitor.completeShakeTutorial()
        rule.dispatcher.scheduler.advanceUntilIdle()
        val signedIn = model(ownerKey = "a".repeat(64))
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(signedIn.state.value.shakeTutorialCompleted)
        assertFalse(signedIn.state.value.shakeTooltipPending)
        signedIn.completeShakeTutorial()
        signedIn.consumeShakeTooltip()
        rule.dispatcher.scheduler.advanceUntilIdle()
        val visitorAgain = model()
        val signedInAgain = model(ownerKey = "a".repeat(64))
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(visitorAgain.state.value.shakeTutorialCompleted)
        assertTrue(visitorAgain.state.value.shakeTooltipPending)
        assertTrue(signedInAgain.state.value.shakeTutorialCompleted)
        assertFalse(signedInAgain.state.value.shakeTooltipPending)
    }

    @Test fun `signed in completion does not suppress visitor tutorial`() = runTest(rule.dispatcher) {
        val signedIn = model(ownerKey = "a".repeat(64))
        rule.dispatcher.scheduler.advanceUntilIdle()
        signedIn.completeShakeTutorial()
        rule.dispatcher.scheduler.advanceUntilIdle()
        val visitor = model()
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(visitor.state.value.shakeTutorialCompleted)
    }

    @Test fun `one account completing tutorial cannot suppress another account on same device`() = runTest(rule.dispatcher) {
        val accountA = model(ownerKey = "a".repeat(64))
        rule.dispatcher.scheduler.advanceUntilIdle()
        accountA.completeShakeTutorial()
        rule.dispatcher.scheduler.advanceUntilIdle()
        val accountB = model(ownerKey = "b".repeat(64))
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(accountB.state.value.shakeTutorialCompleted)
        assertFalse(accountB.state.value.shakeTooltipPending)
        accountB.completeShakeTutorial()
        accountB.consumeShakeTooltip()
        rule.dispatcher.scheduler.advanceUntilIdle()
        val accountAReturning = model(ownerKey = "a".repeat(64))
        val accountBReturning = model(ownerKey = "b".repeat(64))
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(accountAReturning.state.value.shakeTutorialCompleted)
        assertTrue(accountAReturning.state.value.shakeTooltipPending)
        assertTrue(accountBReturning.state.value.shakeTutorialCompleted)
        assertFalse(accountBReturning.state.value.shakeTooltipPending)
    }

    @Test fun `failed preference write surfaces safe error and retains persisted shake state`() = runTest(rule.dispatcher) {
        val vm = model()
        rule.dispatcher.scheduler.advanceUntilIdle()
        fail = true
        vm.toggleShake()
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.shakeEnabled)
        assertNotNull(vm.state.value.errorMessage)
        vm.dismissError()
        assertNull(vm.state.value.errorMessage)
    }

    @Test fun `storage runs on the injected worker and command submission preserves order`() = runTest(rule.dispatcher) {
        Executors.newSingleThreadExecutor { task -> Thread(task, "home-storage-test") }.asCoroutineDispatcher().use { io ->
            val threads = java.util.concurrent.CopyOnWriteArrayList<String>()
            onLoad = { threads += Thread.currentThread().name }
            beforeWrite = { threads += Thread.currentThread().name }
            val vm = HomePreferencesViewModel(HomePreferencesUseCases(layout, shake), FixedCoroutineDispatchers(io))
            val store = ViewModelStore().apply { put("settings", vm) }
            try {
                vm.state.first { it.loaded }
                vm.toggleShake(true)
                vm.toggleShake().join()
                assertEquals(
                    listOf("home-storage-test", "home-storage-test", "home-storage-test"),
                    threads.map {
                        it.substringBefore(" @coroutine#")
                    }
                )
                assertEquals(listOf("load", "hidden:true", "enabled:true", "enabled:false"), calls)
            } finally {
                store.clear()
            }
        }
    }

    @Test fun `blocking write may finish after clearing but cannot publish late state or failure`() = runTest(rule.dispatcher) {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { io ->
            val started = CountDownLatch(1)
            val release = CountDownLatch(1)
            val vm = HomePreferencesViewModel(HomePreferencesUseCases(layout, shake), FixedCoroutineDispatchers(io))
            val store = ViewModelStore().apply { put("settings", vm) }
            try {
                vm.state.first { it.loaded }
                val before = vm.state.value
                beforeWrite = {
                    started.countDown()
                    check(release.await(5, TimeUnit.SECONDS))
                }
                val command = vm.toggleShake()
                assertTrue(withContext(Dispatchers.IO) { started.await(5, TimeUnit.SECONDS) })
                store.clear()
                release.countDown()
                command.join()
                assertEquals(before, vm.state.value)
            } finally {
                release.countDown()
                store.clear()
            }
        }
    }

    @Test fun `cleared model cancels queued storage commands`() = runTest(rule.dispatcher) {
        val vm = model()
        rule.dispatcher.scheduler.advanceUntilIdle()
        val store = ViewModelStore().apply { put("settings", vm) }
        vm.toggleShake()
        store.clear()
        rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("load"), calls)
    }
}
