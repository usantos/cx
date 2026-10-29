package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.feature.retail.domain.port.RetailCountrySource
import br.gov.caixa.loterias.feature.retail.domain.usecase.CheckRetailLocation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateEffect
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStatus
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.LocationGateViewModel
import br.gov.caixa.loterias.feature.retail.presentation.mapper.locationActionLabel
import br.gov.caixa.loterias.feature.retail.presentation.mapper.locationMessage

@OptIn(ExperimentalCoroutinesApi::class)
class LocationGateViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `reports permission and provider prerequisites and resets for retry`() {
        val viewModel = LocationGateViewModel(CheckRetailLocation(RetailCountrySource { "BR" }))
        viewModel.permissionRequired()
        assertEquals(LocationGateStatus.PERMISSION_REQUIRED, viewModel.state.value.status)
        viewModel.permissionRequired(permanentlyDenied = true)
        assertEquals(LocationGateStatus.PERMISSION_PERMANENTLY_DENIED, viewModel.state.value.status)
        viewModel.providerDisabled()
        assertEquals(LocationGateStatus.PROVIDER_DISABLED, viewModel.state.value.status)
        viewModel.retry()
        assertEquals(LocationGateStatus.CHECKING, viewModel.state.value.status)
    }

    @Test fun `allows navigation only after Brazil was confirmed`() = runTest(dispatcher) {
        val viewModel = LocationGateViewModel(CheckRetailLocation(RetailCountrySource { "BR" }))
        val effect = async { viewModel.effects.first() }
        viewModel.checkLocation()
        advanceUntilIdle()
        assertEquals(LocationGateEffect.Allowed, effect.await())
    }

    @Test fun `foreign country produces blocked state without navigation`() = runTest(dispatcher) {
        val viewModel = LocationGateViewModel(CheckRetailLocation(RetailCountrySource { "AR" }))
        val effects = mutableListOf<LocationGateEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.collect { effects.add(it) }
        }
        viewModel.checkLocation()
        advanceUntilIdle()
        assertEquals(LocationGateStatus.OUTSIDE_BRAZIL, viewModel.state.value.status)
        assertEquals(emptyList<LocationGateEffect>(), effects)
    }

    @Test fun `failed lookup shows retry and successful retry allows entry`() = runTest(dispatcher) {
        var country: String? = null
        val viewModel = LocationGateViewModel(CheckRetailLocation(RetailCountrySource { country }))
        viewModel.checkLocation()
        advanceUntilIdle()
        assertEquals(LocationGateStatus.ERROR, viewModel.state.value.status)
        country = "BR"
        viewModel.retry()
        assertEquals(LocationGateStatus.CHECKING, viewModel.state.value.status)
        val effect = async { viewModel.effects.first() }
        viewModel.checkLocation()
        advanceUntilIdle()
        assertEquals(LocationGateEffect.Allowed, effect.await())
    }

    @Test fun `mock configuration allows entry without location lookup`() = runTest(dispatcher) {
        val viewModel = LocationGateViewModel(CheckRetailLocation(RetailCountrySource { error("device accessed") }))
        val effect = async { viewModel.effects.first() }
        viewModel.checkLocation(mockInsideBrazil = true)
        advanceUntilIdle()
        assertEquals(LocationGateEffect.Allowed, effect.await())
    }

    @Test fun `permission loss cancels pending lookup so it cannot navigate`() = runTest(dispatcher) {
        val country = CompletableDeferred<String?>()
        val viewModel = LocationGateViewModel(CheckRetailLocation(RetailCountrySource { country.await() }))
        val effects = mutableListOf<LocationGateEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.collect { effects.add(it) }
        }
        viewModel.checkLocation()
        advanceUntilIdle()
        viewModel.permissionRequired()
        country.complete("BR")
        advanceUntilIdle()
        assertEquals(LocationGateStatus.PERMISSION_REQUIRED, viewModel.state.value.status)
        assertEquals(emptyList<LocationGateEffect>(), effects)
    }

    @Test fun `non cooperative lookup cannot overwrite revoked permission or authorize navigation`() = runTest(dispatcher) {
        for (countryCode in listOf("BR", "AR", null)) {
            lateinit var callback: kotlin.coroutines.Continuation<String?>
            val viewModel = LocationGateViewModel(CheckRetailLocation(RetailCountrySource {
                kotlin.coroutines.suspendCoroutine { callback = it }
            }))
            val effects = mutableListOf<LocationGateEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.effects.collect { effects.add(it) }
            }
            viewModel.checkLocation()
            advanceUntilIdle()
            viewModel.permissionRequired(permanentlyDenied = true)
            callback.resumeWith(Result.success(countryCode))
            advanceUntilIdle()
            assertEquals(LocationGateStatus.PERMISSION_PERMANENTLY_DENIED, viewModel.state.value.status)
            assertEquals(emptyList<LocationGateEffect>(), effects)
        }
    }

    @Test fun `non cooperative older lookup cannot overwrite a replacement check`() = runTest(dispatcher) {
        lateinit var callback: kotlin.coroutines.Continuation<String?>
        var firstLookup = true
        val viewModel = LocationGateViewModel(CheckRetailLocation(RetailCountrySource {
            if (firstLookup) {
                firstLookup = false
                kotlin.coroutines.suspendCoroutine { callback = it }
            } else "BR"
        }))
        val effects = mutableListOf<LocationGateEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.collect { effects.add(it) }
        }
        viewModel.checkLocation()
        advanceUntilIdle()
        viewModel.retry()
        viewModel.checkLocation()
        advanceUntilIdle()
        callback.resumeWith(Result.success("AR"))
        advanceUntilIdle()
        assertEquals(LocationGateStatus.CHECKING, viewModel.state.value.status)
        assertEquals(listOf(LocationGateEffect.Allowed), effects)
    }

    @Test fun `retry during pending lookup triggers a fresh check`() = runTest(dispatcher) {
        val pendingCountry = CompletableDeferred<String?>()
        var firstLookup = true
        val viewModel = LocationGateViewModel(CheckRetailLocation(RetailCountrySource {
            if (firstLookup) {
                firstLookup = false
                pendingCountry.await()
            } else "BR"
        }))
        val effects = mutableListOf<LocationGateEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.collect { effects.add(it) }
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { if (it.status == LocationGateStatus.CHECKING) viewModel.checkLocation() }
        }
        advanceUntilIdle()
        viewModel.retry()
        advanceUntilIdle()
        assertEquals(listOf(LocationGateEffect.Allowed), effects)
    }

    @Test fun `legacy messages and actions cover provider permission territory and failure`() {
        assertEquals(
            "Precisamos conferir se você está em território brasileiro.\nHabilite sua localização.",
            locationMessage(LocationGateStatus.PERMISSION_REQUIRED),
        )
        assertEquals("Ir para configurações", locationActionLabel(LocationGateStatus.PROVIDER_DISABLED))
        assertEquals(
            "As apostas de Loterias só podem ser realizadas em território brasileiro.",
            locationMessage(LocationGateStatus.OUTSIDE_BRAZIL),
        )
        assertNull(locationActionLabel(LocationGateStatus.OUTSIDE_BRAZIL))
        assertEquals("Tentar novamente", locationActionLabel(LocationGateStatus.ERROR))
        assertNull(locationActionLabel(LocationGateStatus.CHECKING))
    }
}
