package br.gov.caixa.loterias.feature.responsiblegaming

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.AccountSuspension
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentAnswer
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSelfAssessmentQuestionsUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.GetSocialTransfersUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SubmitSelfAssessmentUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspendAccountUseCase
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingEffect
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.viewmodel.ResponsibleGamingViewModel
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.viewmodel.suspensionHours
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
    fun `assessment submits empty answer description as native contract`() = runTest(dispatcherRule.dispatcher) {
        var submitted = emptyList<SelfAssessmentAnswer>()
        val viewModel = viewModel(
            route = "responsible/form",
            questions = AppResult.Success(listOf(question(1))),
            submit = {
                submitted = it
                AppResult.Success(Unit)
            }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        viewModel.onAction(ResponsibleGamingAction.Answer(true))
        viewModel.onAction(ResponsibleGamingAction.NextQuestion)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(SelfAssessmentAnswer(1, "", true)), submitted)
    }

    @Test
    fun `assessment loads navigates and submits every answer`() = runTest(dispatcherRule.dispatcher) {
        var submitted = emptyList<SelfAssessmentAnswer>()
        val viewModel = viewModel(
            route = "responsible/form",
            questions = AppResult.Success(listOf(question(1), question(2))),
            submit = { answers ->
                submitted = answers
                AppResult.Success(Unit)
            }
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.state.value.currentQuestion?.id)
        assertNull(viewModel.state.value.currentAnswer)
        viewModel.onAction(ResponsibleGamingAction.Answer(false))
        viewModel.onAction(ResponsibleGamingAction.NextQuestion)
        assertEquals(2, viewModel.state.value.currentQuestion?.id)
        viewModel.onAction(ResponsibleGamingAction.PreviousQuestion)
        assertEquals(1, viewModel.state.value.currentQuestion?.id)
        assertNull(viewModel.state.value.currentAnswer)
        viewModel.onAction(ResponsibleGamingAction.Answer(false))
        viewModel.onAction(ResponsibleGamingAction.NextQuestion)
        viewModel.onAction(ResponsibleGamingAction.Answer(true))
        viewModel.onAction(ResponsibleGamingAction.NextQuestion)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            listOf(
                SelfAssessmentAnswer(1, "", false),
                SelfAssessmentAnswer(2, "", true)
            ),
            submitted
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
            submit = { AppResult.Failure(AppError.Unexpected) }
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
            }
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
                if (calls == 1) {
                    AppResult.Failure(AppError.Validation(""))
                } else {
                    AppResult.Success(socialTransfers())
                }
            }
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
                if (calls == 1) {
                    AppResult.Failure(AppError.Network)
                } else {
                    AppResult.Success(AccountSuspension(true, "7 dias"))
                }
            }
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

    @Test
    fun `suspension end uses Sao Paulo server zone even when device is UTC`() = runTest(dispatcherRule.dispatcher) {
        val previousZone = java.util.TimeZone.getDefault()
        try {
            java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"))
            val model = viewModel(
                "responsible/suspension",
                serverTimeSource = br.gov.caixa.loterias.core.common.time.ServerTimeSource {
                    java.time.Instant.parse("2026-10-21T00:30:00Z")
                }
            )
            model.onAction(ResponsibleGamingAction.SelectSuspensionPeriod("24 horas"))
            model.onAction(ResponsibleGamingAction.ToggleSuspensionAcknowledgement)
            model.onAction(ResponsibleGamingAction.ConfirmSuspension)
            model.onAction(ResponsibleGamingAction.SubmitSuspension)
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

            assertEquals("21/10/2026, às 21:30.", model.state.value.suspensionEndsAt)
        } finally {
            java.util.TimeZone.setDefault(previousZone)
        }
    }

    @Test fun `deferred assessment form reads no questions before login`() {
        var reads = 0
        val vm = viewModel(
            "responsible/form",
            autoLoad = false,
            getQuestions = {
                reads++
                AppResult.Success(listOf(question(1)))
            }
        )
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
        val vm =
            viewModel("responsible/form", isSessionCurrent = { false }, getQuestions = {
                reads++
                AppResult.Success(listOf(question(1)))
            })
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

    @Test fun `two suspension submissions before IO starts send one request`() = runTest {
        val io = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        var calls = 0
        val model = viewModel("responsible/suspension", ioDispatcher = io, suspend = {
            calls++
            if (calls == 1) AppResult.Success(AccountSuspension(true, "24 horas")) else AppResult.Failure(AppError.Network)
        })
        model.onAction(ResponsibleGamingAction.SelectSuspensionPeriod("24 horas"))
        model.onAction(ResponsibleGamingAction.ToggleSuspensionAcknowledgement)
        model.onAction(ResponsibleGamingAction.ConfirmSuspension)
        model.onAction(ResponsibleGamingAction.SubmitSuspension)
        model.onAction(ResponsibleGamingAction.SubmitSuspension)
        io.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        assertEquals(AccountSuspension(true, "24 horas"), model.state.value.suspension)
        assertNull(model.state.value.errorMessage)
        val terminal = model.state.value
        io.scheduler.advanceUntilIdle()
        assertEquals(terminal, model.state.value)
    }

    @Test fun `two final answers before IO starts submit one assessment`() = runTest {
        val io = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        var calls = 0
        val model = viewModel("responsible/form", ioDispatcher = io, questions = AppResult.Success(listOf(question(1))), submit = {
            calls++
            if (calls == 1) AppResult.Success(Unit) else AppResult.Failure(AppError.Network)
        })
        io.scheduler.advanceUntilIdle()
        model.onAction(ResponsibleGamingAction.Answer(true))
        model.onAction(ResponsibleGamingAction.NextQuestion)
        model.onAction(ResponsibleGamingAction.NextQuestion)
        io.scheduler.advanceUntilIdle()
        assertEquals(1, calls)
        assertTrue(model.state.value.assessmentSubmitted)
        assertNull(model.state.value.errorMessage)
        val terminal = model.state.value
        io.scheduler.advanceUntilIdle()
        assertEquals(terminal, model.state.value)
    }

    @Test fun `a late second question load cannot release an unfinished assessment submission`() = runTest {
        val io = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        val first = kotlinx.coroutines.CompletableDeferred<AppResult<List<SelfAssessmentQuestion>>>()
        val second = kotlinx.coroutines.CompletableDeferred<AppResult<List<SelfAssessmentQuestion>>>()
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<Unit>>()
        var reads = 0
        var submissions = 0
        val model = viewModel(
            "responsible/form",
            ioDispatcher = io,
            autoLoad = false,
            getQuestions = {
                reads++
                if (reads == 1) first.await() else second.await()
            },
            submit = {
                submissions++
                pending.await()
            }
        )
        model.loadRouteData()
        model.loadRouteData()
        io.scheduler.runCurrent()
        first.complete(AppResult.Success(listOf(question(1))))
        io.scheduler.runCurrent()
        model.onAction(ResponsibleGamingAction.Answer(true))
        model.onAction(ResponsibleGamingAction.NextQuestion)
        io.scheduler.runCurrent()
        // A segunda leitura pode concluir após o POST; sua projeção não deve liberar outro envio.
        second.complete(AppResult.Success(listOf(question(1))))
        io.scheduler.runCurrent()
        model.onAction(ResponsibleGamingAction.Answer(true))
        model.onAction(ResponsibleGamingAction.NextQuestion)
        io.scheduler.runCurrent()
        pending.complete(AppResult.Success(Unit))
        io.scheduler.advanceUntilIdle()
        assertEquals(1, submissions)
        assertTrue(model.state.value.assessmentSubmitted)
        assertNull(model.state.value.errorMessage)
    }

    private fun viewModel(
        route: String,
        ioDispatcher: kotlinx.coroutines.CoroutineDispatcher = dispatcherRule.dispatcher,
        questions: AppResult<List<SelfAssessmentQuestion>> = AppResult.Success(emptyList()),
        getQuestions: suspend () -> AppResult<List<SelfAssessmentQuestion>> = { questions },
        submit: suspend (List<SelfAssessmentAnswer>) -> AppResult<Unit> = { AppResult.Success(Unit) },
        social: suspend () -> AppResult<SocialTransfers> = { AppResult.Success(socialTransfers()) },
        suspend: suspend (Int) -> AppResult<AccountSuspension> = {
            AppResult.Success(
                AccountSuspension(
                    true,
                    when (it) {
                        24 -> "24 horas"
                        else -> "$it horas"
                    }
                )
            )
        },
        assessmentRequired: Boolean = false,
        autoLoad: Boolean = true,
        isSessionCurrent: () -> Boolean = { true },
        serverTimeSource: ServerTimeSource = ServerTimeSource {
            java.time.Instant.now()
        },
    ) = ResponsibleGamingViewModel(
        route = route,
        getQuestions = GetSelfAssessmentQuestionsUseCase { getQuestions() },
        submitAssessment = SubmitSelfAssessmentUseCase { submit(it) },
        getSocialTransfers = GetSocialTransfersUseCase { social() },
        suspendAccount = SuspendAccountUseCase { suspend(it) },
        dispatchers = FixedCoroutineDispatchers(ioDispatcher),
        assessmentRequired = assessmentRequired,
        autoLoad = autoLoad,
        isSessionCurrent = isSessionCurrent,
        serverTimeSource = serverTimeSource
    )

    private fun question(id: Int) = SelfAssessmentQuestion(id, "Pergunta $id", id)

    private fun socialTransfers() = SocialTransfers(
        BigDecimal("1000"), BigDecimal("100"), "04/09/2026",
        BigDecimal("48"), BigDecimal("20"), BigDecimal("10"), BigDecimal("5"),
        BigDecimal("4"), BigDecimal("3"), BigDecimal("6")
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
