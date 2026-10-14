package br.gov.caixa.loterias.feature.responsiblegaming

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.GetSocialTransfersUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.SelfAssessmentAnswer
import br.gov.caixa.loterias.core.domain.responsiblegaming.SelfAssessmentQuestion
import br.gov.caixa.loterias.core.domain.responsiblegaming.SocialTransfers
import br.gov.caixa.loterias.core.domain.responsiblegaming.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.core.domain.responsiblegaming.AccountSuspension
import br.gov.caixa.loterias.core.domain.responsiblegaming.SuspendAccountUseCase
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResponsibleGamingViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test
    fun `assessment loads navigates and submits every answer`() = runTest(dispatcherRule.dispatcher) {
        var submitted = emptyList<SelfAssessmentAnswer>()
        val viewModel = viewModel(
            route = "responsible/form",
            questions = AppResult.Success(listOf(question(1), question(2))),
            submit = { answers -> submitted = answers; AppResult.Success(Unit) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.state.value.currentQuestion?.id)
        assertNull(viewModel.state.value.currentAnswer)
        viewModel.onAction(ResponsibleGamingAction.Answer(false))
        viewModel.onAction(ResponsibleGamingAction.NextQuestion)
        assertEquals(2, viewModel.state.value.currentQuestion?.id)
        viewModel.onAction(ResponsibleGamingAction.PreviousQuestion)
        assertEquals(1, viewModel.state.value.currentQuestion?.id)
        viewModel.onAction(ResponsibleGamingAction.NextQuestion)
        viewModel.onAction(ResponsibleGamingAction.Answer(true))
        viewModel.onAction(ResponsibleGamingAction.NextQuestion)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            listOf(
                SelfAssessmentAnswer(1, "Não", false),
                SelfAssessmentAnswer(2, "Sim", true),
            ),
            submitted,
        )
        assertTrue(viewModel.state.value.assessmentSubmitted)
        assertTrue(viewModel.state.value.hasAffirmativeAnswer)
        assertFalse(viewModel.state.value.loading)
    }

    @Test
    fun `assessment ignores actions without data and reports submission failure`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel(
            route = "responsible/form",
            questions = AppResult.Success(listOf(question(1))),
            submit = { AppResult.Failure(AppError.Unexpected) },
        )
        viewModel.onAction(ResponsibleGamingAction.Answer(true))
        viewModel.onAction(ResponsibleGamingAction.NextQuestion)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.onAction(ResponsibleGamingAction.NextQuestion)
        viewModel.onAction(ResponsibleGamingAction.Answer(false))
        viewModel.onAction(ResponsibleGamingAction.NextQuestion)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Ocorreu um erro inesperado. Tente novamente.", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.assessmentSubmitted)
    }

    @Test
    fun `retry maps question failures and reloads`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val viewModel = viewModel(
            route = "responsible/form",
            getQuestions = {
                calls++
                when (calls) {
                    1 -> AppResult.Failure(AppError.Network)
                    2 -> AppResult.Failure(AppError.Validation("Configuração inválida"))
                    else -> AppResult.Success(listOf(question(9)))
                }
            },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.startsWith("Não foi possível conectar"))
        viewModel.onAction(ResponsibleGamingAction.Retry)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Configuração inválida", viewModel.state.value.errorMessage)
        viewModel.onAction(ResponsibleGamingAction.Retry)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(9, viewModel.state.value.currentQuestion?.id)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `social route loads data and retries validation failure`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val viewModel = viewModel(
            route = "responsible/social",
            social = {
                calls++
                if (calls == 1) AppResult.Failure(AppError.Validation(""))
                else AppResult.Success(socialTransfers())
            },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Não foi possível carregar os dados.", viewModel.state.value.errorMessage)
        viewModel.onAction(ResponsibleGamingAction.Retry)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(socialTransfers(), viewModel.state.value.socialTransfers)
    }

    @Test
    fun `start back completion and suspension state emit deterministic outcomes`() = runTest(dispatcherRule.dispatcher) {
        val viewModel = viewModel("responsible/suspension")
        viewModel.onAction(ResponsibleGamingAction.SelectSuspensionPeriod("24 horas"))
        viewModel.onAction(ResponsibleGamingAction.ToggleSuspensionAcknowledgement)
        viewModel.onAction(ResponsibleGamingAction.ConfirmSuspension)
        assertTrue(viewModel.state.value.suspensionConfirmation)

        val completed = async { viewModel.effects.first() }
        viewModel.onAction(ResponsibleGamingAction.SubmitSuspension)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(AccountSuspension(true, "24 horas"), viewModel.state.value.suspension)
        viewModel.onAction(ResponsibleGamingAction.Complete)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ResponsibleGamingEffect.Completed, completed.await())

        val back = async { viewModel.effects.first() }
        viewModel.onAction(ResponsibleGamingAction.Back)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ResponsibleGamingEffect.NavigateBack, back.await())

        val startViewModel = viewModel("responsible/start")
        assertFalse(startViewModel.state.value.assessmentRequired)
        val start = async { startViewModel.effects.first() }
        startViewModel.onAction(ResponsibleGamingAction.Start)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ResponsibleGamingEffect.Completed, start.await())

        val mandatoryStart = viewModel("responsible/start", assessmentRequired = true)
        assertTrue(mandatoryStart.state.value.assessmentRequired)
    }

    @Test
    fun `first previous goes back and invalid suspension cannot confirm`() = runTest(dispatcherRule.dispatcher) {
        val form = viewModel("responsible/form", questions = AppResult.Success(listOf(question(1))))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        val back = async { form.effects.first() }
        form.onAction(ResponsibleGamingAction.PreviousQuestion)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(ResponsibleGamingEffect.NavigateBack, back.await())

        val suspension = viewModel("responsible/suspension")
        suspension.onAction(ResponsibleGamingAction.ConfirmSuspension)
        assertFalse(suspension.state.value.suspensionConfirmation)
        suspension.onAction(ResponsibleGamingAction.ToggleSuspensionAcknowledgement)
        assertTrue(suspension.state.value.suspensionAcknowledged)
        suspension.onAction(ResponsibleGamingAction.SubmitSuspension)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(suspension.state.value.suspension)
    }

    @Test
    fun `suspension failure can retry`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val viewModel = viewModel(
            "responsible/suspension",
            suspend = {
                calls++
                if (calls == 1) AppResult.Failure(AppError.Network)
                else AppResult.Success(AccountSuspension(true, "7 dias"))
            },
        )
        viewModel.onAction(ResponsibleGamingAction.SelectSuspensionPeriod("7 dias"))
        viewModel.onAction(ResponsibleGamingAction.ToggleSuspensionAcknowledgement)
        viewModel.onAction(ResponsibleGamingAction.ConfirmSuspension)
        viewModel.onAction(ResponsibleGamingAction.SubmitSuspension)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage!!.startsWith("Não foi possível conectar"))
        viewModel.onAction(ResponsibleGamingAction.Retry)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(AccountSuspension(true, "7 dias"), viewModel.state.value.suspension)
        assertEquals(2, calls)
    }

    @Test fun `deferred assessment form reads no questions before login`() {
        var reads = 0
        val vm = viewModel("responsible/form", autoLoad = false,
            getQuestions = { reads++; AppResult.Success(listOf(question(1))) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, reads)
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.questions.isEmpty())
        vm.loadRouteData()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, reads)
        assertEquals(question(1), vm.state.value.currentQuestion)
    }

    @Test fun `expired lease prevents queued assessment reads`() {
        var reads = 0
        val vm = viewModel("responsible/form", isSessionCurrent = { false }, getQuestions = { reads++; AppResult.Success(listOf(question(1))) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, reads)
        assertTrue(vm.state.value.questions.isEmpty())
    }

    @Test fun `expired lease ignores pending assessment response`() {
        var current = true
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<SelfAssessmentQuestion>>>()
        val vm = viewModel("responsible/form", isSessionCurrent = { current }, getQuestions = { pending.await() })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        current = false
        pending.complete(AppResult.Success(listOf(question(1))))
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.questions.isEmpty())
    }

    private fun viewModel(
        route: String,
        questions: AppResult<List<SelfAssessmentQuestion>> = AppResult.Success(emptyList()),
        getQuestions: suspend () -> AppResult<List<SelfAssessmentQuestion>> = { questions },
        submit: suspend (List<SelfAssessmentAnswer>) -> AppResult<Unit> = { AppResult.Success(Unit) },
        social: suspend () -> AppResult<SocialTransfers> = { AppResult.Success(socialTransfers()) },
        suspend: suspend (Int) -> AppResult<AccountSuspension> = {
            AppResult.Success(AccountSuspension(true, when (it) { 24 -> "24 horas"; else -> "$it horas" }))
        },
        assessmentRequired: Boolean = false,
        autoLoad: Boolean = true,
        isSessionCurrent: () -> Boolean = { true },
    ) = ResponsibleGamingViewModel(
        route = route,
        getQuestions = GetSelfAssessmentQuestionsUseCase { getQuestions() },
        submitAssessment = SubmitSelfAssessmentUseCase { submit(it) },
        getSocialTransfers = GetSocialTransfersUseCase { social() },
        suspendAccount = SuspendAccountUseCase { suspend(it) },
        dispatchers = FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        assessmentRequired = assessmentRequired,
        autoLoad = autoLoad,
        isSessionCurrent = isSessionCurrent,
    )

    private fun question(id: Int) = SelfAssessmentQuestion(id, "Pergunta $id", id)

    private fun socialTransfers() = SocialTransfers(
        BigDecimal("1000"), BigDecimal("100"), "04/09/2026",
        BigDecimal("48"), BigDecimal("20"), BigDecimal("10"), BigDecimal("5"),
        BigDecimal("4"), BigDecimal("3"), BigDecimal("6"),
    )

    @Test
    fun `suspension labels map to backend hours`() {
        assertEquals(24, suspensionHours("24 horas"))
        assertEquals(48, suspensionHours("48 horas"))
        assertEquals(168, suspensionHours("7 dias"))
        assertEquals(360, suspensionHours("15 dias"))
        assertEquals(720, suspensionHours("30 dias"))
        assertNull(suspensionHours(null))
        assertNull(suspensionHours("outro"))
    }
}
