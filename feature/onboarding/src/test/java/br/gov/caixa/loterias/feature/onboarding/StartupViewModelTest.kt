package br.gov.caixa.loterias.feature.onboarding

import br.gov.caixa.loterias.core.domain.configuration.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilterParameters
import br.gov.caixa.loterias.core.domain.configuration.GetMarketplaceFilterParameters
import br.gov.caixa.loterias.core.domain.configuration.IsVersionSupported
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.domain.configuration.PrepareNetworkSecurityUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StartupViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `construction waits for the queue without preparing or calling business services`() {
        val calls = mutableListOf<String>()
        val repository = object : AppConfigurationRepository {
            override suspend fun isVersionSupported(versionName: String): Boolean { calls += "version"; return true }
            override suspend fun marketplaceFilterParameters(): MarketplaceFilterParameters { calls += "parameters"; return MarketplaceFilterParameters(2, 4) }
        }
        val vm = StartupViewModel(IsVersionSupported(repository), GetMarketplaceFilterParameters(repository), "1.24.0",
            PrepareNetworkSecurityUseCase { calls += "security"; AppResult.Success(Unit) })
        assertEquals(StartupState.WAITING_QUEUE, vm.state.value)
        assertEquals(emptyList<String>(), calls)
    }

    @Test fun `moves to ready when version and configuration are valid`() = runTest(dispatcher) {
        val viewModel = viewModel(repository(), "1.24.0")
        viewModel.queuePassed()
        viewModel.queuePassed()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(StartupState.READY, viewModel.state.value)
    }

    @Test fun `requires update when backend rejects version`() = runTest(dispatcher) {
        val viewModel = viewModel(repository(supported = false), "old")
        viewModel.queuePassed()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(StartupState.UPDATE_REQUIRED, viewModel.state.value)
    }

    @Test fun `shows unavailable and supports retry`() = runTest(dispatcher) {
        var fail = true
        val repository = object : AppConfigurationRepository {
            override suspend fun isVersionSupported(versionName: String): Boolean {
                if (fail) error("offline")
                return true
            }
            override suspend fun marketplaceFilterParameters() = MarketplaceFilterParameters(2, 4)
        }
        val viewModel = viewModel(repository, "1.24.0")
        viewModel.queuePassed()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(StartupState.UNAVAILABLE, viewModel.state.value)
        fail = false
        viewModel.retry()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(StartupState.READY, viewModel.state.value)
    }

    @Test fun `security finishes before business calls and repeated clicks do not start parallel validation`() = runTest(dispatcher) {
        val gate = CompletableDeferred<Unit>()
        val calls = mutableListOf<String>()
        val repository = object : AppConfigurationRepository {
            override suspend fun isVersionSupported(versionName: String): Boolean { calls += "version"; return true }
            override suspend fun marketplaceFilterParameters(): MarketplaceFilterParameters { calls += "parameters"; return MarketplaceFilterParameters(2, 4) }
        }
        val vm = viewModel(repository, "1.24.0", PrepareNetworkSecurityUseCase {
            calls += "security-start"
            gate.await()
            calls += "security-ready"
            AppResult.Success(Unit)
        })
        vm.queuePassed()
        dispatcher.scheduler.runCurrent()
        vm.retry()
        vm.queuePassed()
        assertEquals(StartupState.CHECKING, vm.state.value)
        assertEquals(listOf("security-start"), calls)
        gate.complete(Unit)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("security-start", "security-ready", "version", "parameters"), calls)
        assertEquals(StartupState.READY, vm.state.value)
    }

    @Test fun `security failure blocks business calls and retry prepares again`() = runTest(dispatcher) {
        var securityCalls = 0
        var businessCalls = 0
        val repository = object : AppConfigurationRepository {
            override suspend fun isVersionSupported(versionName: String): Boolean { businessCalls++; return true }
            override suspend fun marketplaceFilterParameters() = MarketplaceFilterParameters(2, 4)
        }
        val vm = viewModel(repository, "1.24.0", PrepareNetworkSecurityUseCase {
            securityCalls++
            if (securityCalls == 1) AppResult.Failure(AppError.Network) else AppResult.Success(Unit)
        })
        vm.queuePassed()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(StartupState.UNAVAILABLE, vm.state.value)
        assertEquals(0, businessCalls)
        vm.retry()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, securityCalls)
        assertEquals(1, businessCalls)
        assertEquals(StartupState.READY, vm.state.value)
    }

    @Test fun `cancelled security check releases checking state for another attempt`() = runTest(dispatcher) {
        var cancelled = true
        val vm = viewModel(repository(), "1.24.0", PrepareNetworkSecurityUseCase {
            if (cancelled) throw CancellationException("lifecycle stopped")
            AppResult.Success(Unit)
        })
        vm.queuePassed()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(StartupState.UNAVAILABLE, vm.state.value)
        cancelled = false
        vm.retry()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(StartupState.READY, vm.state.value)
    }

    @Test fun `configuration failure remains unavailable after successful security and version`() = runTest(dispatcher) {
        val repo = object : AppConfigurationRepository {
            override suspend fun isVersionSupported(versionName: String) = true
            override suspend fun marketplaceFilterParameters(): MarketplaceFilterParameters = error("unavailable")
        }
        val vm = viewModel(repo, "1.24.0")
        vm.queuePassed()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(StartupState.UNAVAILABLE, vm.state.value)
    }

    private fun repository(supported: Boolean = true) = object : AppConfigurationRepository {
        override suspend fun isVersionSupported(versionName: String) = supported
        override suspend fun marketplaceFilterParameters() = MarketplaceFilterParameters(2, 4)
    }

    private fun viewModel(repository: AppConfigurationRepository, version: String,
        security: PrepareNetworkSecurityUseCase = PrepareNetworkSecurityUseCase { AppResult.Success(Unit) },
    ) = StartupViewModel(
        IsVersionSupported(repository),
        GetMarketplaceFilterParameters(repository),
        version,
        security,
        FixedCoroutineDispatchers(dispatcher),
    )
}
