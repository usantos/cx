package br.gov.caixa.loterias.feature.retail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationGateViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `represents every legacy location state`() {
        val viewModel = LocationGateViewModel()
        viewModel.permissionRequired()
        assertEquals(LocationGateStatus.PERMISSION_REQUIRED, viewModel.state.value.status)
        viewModel.permissionRequired(permanentlyDenied = true)
        assertEquals(LocationGateStatus.PERMISSION_PERMANENTLY_DENIED, viewModel.state.value.status)
        viewModel.providerDisabled()
        assertEquals(LocationGateStatus.PROVIDER_DISABLED, viewModel.state.value.status)
        viewModel.outsideBrazil()
        assertEquals(LocationGateStatus.OUTSIDE_BRAZIL, viewModel.state.value.status)
        viewModel.failed()
        assertEquals(LocationGateStatus.ERROR, viewModel.state.value.status)
        viewModel.retry()
        assertEquals(LocationGateStatus.CHECKING, viewModel.state.value.status)
    }

    @Test fun `allows navigation only after Brazil was confirmed`() = runTest(dispatcher) {
        val viewModel = LocationGateViewModel()
        val effect = async { viewModel.effects.first() }
        viewModel.allowed()
        advanceUntilIdle()
        assertEquals(LocationGateEffect.Allowed, effect.await())
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
