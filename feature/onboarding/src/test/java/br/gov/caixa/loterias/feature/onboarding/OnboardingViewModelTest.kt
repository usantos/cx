package br.gov.caixa.loterias.feature.onboarding

import br.gov.caixa.loterias.core.domain.onboarding.CompleteIntroductionUseCase
import br.gov.caixa.loterias.core.domain.onboarding.BettingTutorial
import br.gov.caixa.loterias.core.domain.onboarding.SetBettingTutorialDismissedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
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
class OnboardingViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `introduction clamps pages and confirms adult`() {
        val viewModel = OnboardingViewModel(
            "onboarding/introduction",
            CompleteIntroductionUseCase {},
        )

        assertTrue(viewModel.state.value.ageConfirmationVisible)
        viewModel.pageChanged(9)
        viewModel.confirmAdult()

        assertEquals(2, viewModel.state.value.introductionPage)
        assertFalse(viewModel.state.value.ageConfirmationVisible)
        viewModel.pageChanged(-2)
        assertEquals(0, viewModel.state.value.introductionPage)
    }

    @Test
    fun `rejecting age emits underage effect`() = runTest(dispatcher) {
        val viewModel = OnboardingViewModel(
            "onboarding/introduction",
            CompleteIntroductionUseCase {},
        )
        val effect = async { viewModel.effects.first() }

        viewModel.rejectAdult()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(OnboardingEffect.Underage, effect.await())
        assertFalse(viewModel.state.value.ageConfirmationVisible)
    }

    @Test
    fun `finishing introduction persists completion and emits effect`() = runTest(dispatcher) {
        var completed = false
        val viewModel = OnboardingViewModel(
            route = "onboarding/introduction",
            completeIntroduction = CompleteIntroductionUseCase { completed = true },
        )
        val effect = async { viewModel.effects.first() }

        viewModel.complete()
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(completed)
        assertEquals(OnboardingEffect.Completed, effect.await())
    }

    @Test
    fun `finishing a non introduction route does not change preference`() = runTest(dispatcher) {
        var calls = 0
        val viewModel = OnboardingViewModel(
            "onboarding/age",
            CompleteIntroductionUseCase { calls++ },
        )
        val effect = async { viewModel.effects.first() }

        viewModel.complete()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, calls)
        assertEquals(OnboardingEffect.Completed, effect.await())
        assertFalse(viewModel.state.value.ageConfirmationVisible)
    }

    @Test
    fun `starting plus tutorial honors legacy checkbox while skip never suppresses it`() = runTest(dispatcher) {
        val dismissals = mutableListOf<Pair<BettingTutorial, Boolean>>()
        val viewModel = OnboardingViewModel(
            route = "onboarding/tutorial",
            completeIntroduction = CompleteIntroductionUseCase {},
            setTutorialDismissed = SetBettingTutorialDismissedUseCase { tutorial, dismissed ->
                dismissals += tutorial to dismissed
            },
        )
        val effects = mutableListOf<OnboardingEffect>()
        val collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.collect { effects += it }
        }

        viewModel.skipBettingTutorial()
        viewModel.toggleTutorialDoNotShowAgain()
        assertTrue(viewModel.state.value.tutorialDoNotShowAgain)
        viewModel.startBettingTutorial()
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(BettingTutorial.MAIS_MILIONARIA to true), dismissals)
        assertEquals(
            listOf(OnboardingEffect.SkipBettingTutorial, OnboardingEffect.StartBettingTutorial),
            effects,
        )
        collector.cancel()
    }
}
