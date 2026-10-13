package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.core.presentation.WorkflowSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentTerminalViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `pix guidance requires acknowledgement and emits navigation effects`() = runTest {
        val effects = mutableListOf<PaymentTerminalEffect>()
        val viewModel = viewModel("payment/pix-guidance")
        val collector = backgroundScope.launch(dispatcher) { viewModel.effects.collect(effects::add) }

        viewModel.submit()
        assertTrue(effects.isEmpty())
        viewModel.togglePixTerms()
        assertTrue(viewModel.state.value.pixTermsAccepted)
        viewModel.submit()
        assertEquals(PaymentTerminalEffect.Completed, effects.last())
        viewModel.back()
        assertEquals(PaymentTerminalEffect.NavigateBack, effects.last())
        viewModel.togglePixTerms()
        assertFalse(viewModel.state.value.pixTermsAccepted)
        collector.cancel()
    }

    @Test fun `terminal payment state completes without local form data`() = runTest {
        val effects = mutableListOf<PaymentTerminalEffect>()
        val viewModel = viewModel("payment/success")
        val collector = backgroundScope.launch(dispatcher) { viewModel.effects.collect(effects::add) }
        viewModel.submit()
        assertEquals(listOf(PaymentTerminalEffect.Completed), effects)
        collector.cancel()
    }

    private fun viewModel(route: String) = PaymentTerminalViewModel(WorkflowSpec(route, "Pagamento", ""))
}
