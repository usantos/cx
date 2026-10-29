package br.gov.caixa.loterias.feature.betting

import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowEffect
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingFlowViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BettingFlowPendingEffectTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun prepare() = Dispatchers.setMain(dispatcher)

    @After fun cleanup() = Dispatchers.resetMain()

    @Test fun terminalActionWaitsForTheReturningCollectorAndIsDeliveredOnlyOnce() = runTest(dispatcher) {
        val model = BettingFlowViewModel(WorkflowSpec("betting/tutorial", "Tutorial", "", primaryAction = ""))
        try {
            model.back()
            advanceUntilIdle()
            assertEquals(BettingFlowEffect.NavigateBack, withTimeoutOrNull(100) { model.effects.first() })
            assertEquals(null, withTimeoutOrNull(100) { model.effects.first() })
        } finally {
            model.viewModelScope.cancel()
        }
    }
}
