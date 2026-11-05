package br.gov.caixa.loterias.feature.retail

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import br.gov.caixa.loterias.core.domain.contract.betting.model.BetEntryDecision
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.route.PrizeRedemptionBetEntryEffect
import br.gov.caixa.loterias.feature.retail.presentation.route.PrizeRedemptionInitialSelectionEffect
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

/** A aplicação da composição pode atualizar o estado antes de a coroutine do efeito começar. */
@OptIn(ExperimentalCoroutinesApi::class)
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(sdk = [EFFECT_TIMING_TEST_SDK], application = android.app.Application::class)
class PrizeRedemptionEffectTimingUiTest : PrizeRedemptionTestFixture() {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun `decision consumed while applying composition is not delivered again`() {
        val model = viewModel()
        dispatcher.scheduler.advanceUntilIdle()
        model.betNow()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(BetEntryDecision.Ready(game), model.state.value.betEntryDecision)
        val observedState = mutableStateOf(model.state.value)
        val delivered = mutableListOf<BetEntryDecision>()

        compose.setFeatureContent {
            PrizeRedemptionBetEntryEffect(
                viewModel = model,
                state = observedState.value,
                onBetEntry = { delivered += it },
                currentState = { observedState.value }
            )
            SideEffect {
                model.consumeBetEntryDecision()
                observedState.value = model.state.value
            }
        }
        compose.waitForIdle()

        compose.runOnIdle {
            assertNull(model.state.value.betEntryDecision)
            assertEquals(emptyList<BetEntryDecision>(), delivered)
        }
    }

    @Test fun `leaving options while applying composition prevents initial channel selection`() {
        val model = viewModel()
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.OPTIONS, model.state.value.stage)
        val observedState = mutableStateOf(model.state.value)
        var initialSelectionApplied = false

        compose.setFeatureContent {
            PrizeRedemptionInitialSelectionEffect(
                viewModel = model,
                state = observedState.value,
                initialChannel = PrizeRedemptionChannel.PIX,
                controlledPreviewChannel = null,
                initialSelectionApplied = { initialSelectionApplied },
                onInitialSelectionApplied = { initialSelectionApplied = true },
                currentState = { observedState.value }
            )
            SideEffect {
                observedState.value = observedState.value.copy(stage = PrizeRedemptionStage.LOADING)
            }
        }
        compose.waitForIdle()

        compose.runOnIdle {
            assertEquals(PrizeRedemptionStage.LOADING, observedState.value.stage)
            assertFalse(initialSelectionApplied)
            assertNull(model.state.value.selectedChannel)
            assertEquals(PrizeRedemptionStage.OPTIONS, model.state.value.stage)
        }
    }
}

private const val EFFECT_TIMING_TEST_SDK = 36
