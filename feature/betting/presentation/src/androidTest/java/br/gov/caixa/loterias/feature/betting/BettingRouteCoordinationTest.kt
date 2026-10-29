package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingFlowViewModel
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Caracterização de rota: perder uma ligação de ação ou manter um coletor de efeitos antigo quebra estas asserções. */
class BettingRouteCoordinationTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<BettingFlowViewModel>()

    @After fun cleanup() {
        models.forEach { it.viewModelScope.cancel() }
    }

    @Test fun skippingToFinalThenChoosingPoolPersistsOnlyTheRequestedDismissalAndCompletesOnce() {
        val dismissed = mutableListOf<Pair<BettingTutorial, Boolean>>()
        val model = model(SetBettingTutorialDismissedUseCase { kind, value -> dismissed += kind to value })
        var completed = 0
        var back = 0
        compose.setContent { CaixaDSTheme { BettingRoute(model, { back++ }, { completed++ }) } }
        compose.onNodeWithTag("pool-tutorial-skip").performClick()
        compose.onNodeWithTag("pool-tutorial-checkbox-row").performClick()
        compose.onNodeWithTag("pool-tutorial-primary").performClick()
        compose.runOnIdle {
            assertEquals(4, model.state.value.page)
            assertEquals(listOf(BettingTutorial.POOL to true), dismissed)
            assertEquals(1, completed)
            assertEquals(0, back)
        }
    }

    @Test fun replacingModelRebindsActionsAndCancelsThePreviousEffectCollector() {
        val first = model()
        val second = model()
        val replace = mutableStateOf(false)
        var back = 0
        compose.setContent { CaixaDSTheme { BettingRoute(if (replace.value) second else first, { back++ }, {}) } }
        compose.runOnIdle { replace.value = true }
        compose.waitForIdle()
        compose.runOnIdle { first.back() }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(0, back) }
        compose.onNodeWithTag("pool-tutorial-close").performClick()
        compose.runOnIdle { assertEquals(1, back) }
    }

    private fun model(dismiss: SetBettingTutorialDismissedUseCase = SetBettingTutorialDismissedUseCase { _, _ -> }) =
        BettingFlowViewModel(WorkflowSpec("betting/tutorial", "Tutorial", "", primaryAction = ""), dismiss).also(models::add)
}
