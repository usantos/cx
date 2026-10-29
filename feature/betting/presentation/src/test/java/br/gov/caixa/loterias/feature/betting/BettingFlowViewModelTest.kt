package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowEffect
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingFlowViewModel
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BettingFlowViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Test fun `pool checkbox controls subsequent entry and skip does not open marketplace`() = runTest {
        listOf(false, true).forEach { checked ->
            var storedDismissal = false
            val repository = object : br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository {
                override suspend fun isIntroductionCompleted() = false
                override suspend fun completeIntroduction() = Unit
                override suspend fun isTutorialDismissed(tutorial: BettingTutorial) = storedDismissal
                override suspend fun setTutorialDismissed(tutorial: BettingTutorial, dismissed: Boolean) {
                    assertEquals(BettingTutorial.POOL, tutorial)
                    storedDismissal = dismissed
                }
            }
            val prepare = br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntry(
                br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase {
                    br.gov.caixa.loterias.core.common.result.AppResult.Success(
                        br.gov.caixa.loterias.feature.account.domain.model.AccountAccess(
                            br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination.HOME,
                            false,
                            java.math.BigDecimal.ZERO
                        )
                    )
                },
                repository
            )
            val request = br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryRequest(2, "ESPECIAL")
            val vm = BettingFlowViewModel(
                WorkflowSpec("betting/tutorial", "Tutorial", "", primaryAction = ""),
                br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissed(repository)
            )
            val effects = mutableListOf<BettingFlowEffect>()
            val collector = backgroundScope.launch(dispatcher) { vm.effects.collect(effects::add) }
            if (checked) vm.toggleDoNotShowAgain()
            vm.secondaryAction() // Pula a apresentação até a última página, como no paginador legado.
            assertEquals(4, vm.state.value.page)
            assertEquals(false, storedDismissal)
            vm.primaryAction()
            assertEquals(listOf(BettingFlowEffect.Completed), effects)
            assertEquals(
                br.gov.caixa.loterias.core.common.result.AppResult.Success(
                    br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision.Ready(request, !checked)
                ),
                prepare(request)
            )
            vm.secondaryAction() // O fechamento final retorna; não deve emitir outra conclusão.
            assertEquals(listOf(BettingFlowEffect.Completed, BettingFlowEffect.NavigateBack), effects)
            collector.cancel()
        }
    }

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `keeps route state and follows legacy pool tutorial navigation`() = runTest {
        val spec = WorkflowSpec("betting/tutorial", "Tutorial", "", primaryAction = "")
        val dismissals = mutableListOf<Pair<BettingTutorial, Boolean>>()
        val viewModel = BettingFlowViewModel(
            spec,
            SetBettingTutorialDismissedUseCase { tutorial, dismissed ->
                dismissals += tutorial to dismissed
            }
        )
        val effects = mutableListOf<BettingFlowEffect>()
        val collector = backgroundScope.launch(dispatcher) { viewModel.effects.collect(effects::add) }

        assertEquals(spec, viewModel.state.value.spec)
        viewModel.primaryAction()
        assertEquals(1, viewModel.state.value.page)
        viewModel.secondaryAction()
        assertEquals(4, viewModel.state.value.page)
        viewModel.toggleDoNotShowAgain()
        assertEquals(true, viewModel.state.value.doNotShowAgain)
        viewModel.primaryAction()
        viewModel.back()

        assertEquals(listOf(BettingFlowEffect.Completed, BettingFlowEffect.NavigateBack), effects)
        assertEquals(listOf(BettingTutorial.POOL to true), dismissals)
        collector.cancel()
    }

    @Test fun `rejects pages outside tutorial and super seven advances until completion`() = runTest {
        val spec = WorkflowSpec("betting/tutorial-super-seven", "Super Sete", "", primaryAction = "")
        val dismissals = mutableListOf<Pair<BettingTutorial, Boolean>>()
        val viewModel = BettingFlowViewModel(
            spec,
            SetBettingTutorialDismissedUseCase { tutorial, dismissed ->
                dismissals += tutorial to dismissed
            }
        )
        val effects = mutableListOf<BettingFlowEffect>()
        val collector = backgroundScope.launch(dispatcher) { viewModel.effects.collect(effects::add) }

        viewModel.selectPage(-1)
        viewModel.selectPage(3)
        assertEquals(0, viewModel.state.value.page)
        viewModel.primaryAction()
        viewModel.primaryAction()
        assertEquals(2, viewModel.state.value.page)
        viewModel.primaryAction()
        viewModel.secondaryAction()

        assertEquals(listOf(BettingFlowEffect.Completed, BettingFlowEffect.NavigateBack), effects)
        assertEquals(
            listOf(BettingTutorial.SUPER_SETE to true, BettingTutorial.SUPER_SETE to true),
            dismissals
        )
        collector.cancel()
    }

    @Test fun `pool final does not suppress tutorial when checkbox remains clear`() = runTest {
        val dismissals = mutableListOf<Pair<BettingTutorial, Boolean>>()
        val viewModel = BettingFlowViewModel(
            WorkflowSpec("betting/tutorial-plus", "+Milionária", "", primaryAction = ""),
            SetBettingTutorialDismissedUseCase { tutorial, dismissed -> dismissals += tutorial to dismissed }
        )

        viewModel.selectPage(6)
        viewModel.secondaryAction()

        assertEquals(emptyList<Pair<BettingTutorial, Boolean>>(), dismissals)
    }
}
