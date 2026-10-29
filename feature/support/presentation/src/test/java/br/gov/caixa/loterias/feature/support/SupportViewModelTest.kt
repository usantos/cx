package br.gov.caixa.loterias.feature.support

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection
import br.gov.caixa.loterias.feature.support.domain.repository.SupportRepository
import br.gov.caixa.loterias.feature.support.domain.usecase.AcceptTerms
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportQuestions
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportSections
import br.gov.caixa.loterias.feature.support.domain.usecase.GetTerms
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMode
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import br.gov.caixa.loterias.feature.support.presentation.viewmodel.SupportViewModel
import java.math.BigDecimal
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SupportViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private val dispatcher = UnconfinedTestDispatcher()

    @Test fun `screen actions update state directly and clear transient content on navigation`() = runTest {
        val repository = FakeSupportRepository()
        val viewModel = viewModel(
            repository,
            SupportUiState(
                htmlContent = "old html",
                selectedSectionId = 99,
                selectedSectionTitle = "Old section",
                questions = listOf(SupportQuestion(8, "Old question", "Old answer")),
                expandedQuestionIds = setOf(8),
                errorMessage = "old error"
            )
        )

        viewModel.onAction(SupportAction.SelectTab(SupportTab.TERMS))
        assertEquals(SupportTab.TERMS, viewModel.state.value.selectedTab)
        assertEquals("terms", viewModel.state.value.htmlContent)
        assertNull(viewModel.state.value.selectedSectionId)
        assertEquals("", viewModel.state.value.selectedSectionTitle)
        assertTrue(viewModel.state.value.questions.isEmpty())
        assertEquals(setOf(8L), viewModel.state.value.expandedQuestionIds)
        assertNull(viewModel.state.value.errorMessage)

        viewModel.onAction(SupportAction.ToggleQuestion(2))
        assertEquals(setOf(8L, 2L), viewModel.state.value.expandedQuestionIds)
        viewModel.onAction(SupportAction.ToggleQuestion(2))
        assertEquals(setOf(8L), viewModel.state.value.expandedQuestionIds)

        viewModel.onAction(SupportAction.OpenSection(1, "Seção"))
        assertEquals(1L, viewModel.state.value.selectedSectionId)
        assertEquals(listOf(SupportQuestion(2, "Pergunta", "Resposta")), viewModel.state.value.questions)
        viewModel.onAction(SupportAction.CloseSection)
        assertNull(viewModel.state.value.selectedSectionId)
        assertEquals("", viewModel.state.value.selectedSectionTitle)
        assertTrue(viewModel.state.value.questions.isEmpty())
    }

    @Test fun `content reducer actions apply loaded values and loading failures`() = runTest {
        val viewModel = viewModel(FakeSupportRepository())
        val section = SupportSection(4, "Central", emptyList())
        val question = SupportQuestion(8, "Pergunta", "Resposta")

        viewModel.onAction(SupportAction.StartLoading)
        assertTrue(viewModel.state.value.isLoading)
        viewModel.onAction(SupportAction.LoadFailed("indisponível"))
        assertFalse(viewModel.state.value.isLoading)
        assertEquals("indisponível", viewModel.state.value.errorMessage)

        viewModel.onAction(SupportAction.ContentLoaded("resposta html"))
        assertEquals("resposta html", viewModel.state.value.htmlContent)
        assertNull(viewModel.state.value.errorMessage)
        viewModel.onAction(SupportAction.SectionsLoaded(listOf(section)))
        assertEquals(listOf(section), viewModel.state.value.sections)
        viewModel.onAction(SupportAction.QuestionsLoaded(listOf(question)))
        assertEquals(listOf(question), viewModel.state.value.questions)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test fun `terms acceptance actions preserve guarded behavior`() = runTest {
        val repository = FakeSupportRepository()
        val viewModel = viewModel(
            repository,
            SupportUiState(
                selectedTab = SupportTab.TERMS,
                acceptanceRequired = true
            )
        )

        viewModel.onAction(SupportAction.AcceptTerms)
        assertFalse(repository.accepted)
        viewModel.onAction(SupportAction.ToggleTermsAcceptance)
        assertTrue(viewModel.state.value.termsAccepted)
        val accepted = async(start = CoroutineStart.UNDISPATCHED) { viewModel.termsAccepted.first() }
        viewModel.onAction(SupportAction.AcceptTerms)
        accepted.await()
        assertTrue(repository.accepted)
    }

    @Test fun `loads faq terms and privacy and accepts terms`() = runTest {
        val repository = FakeSupportRepository()
        val viewModel = viewModel(repository)
        assertEquals("Seção", viewModel.state.value.sections.single().title)

        viewModel.onAction(SupportAction.OpenSection(1, "SEÇÃO"))
        assertEquals(1L, viewModel.state.value.selectedSectionId)
        assertEquals("Pergunta", viewModel.state.value.questions.single().title)
        viewModel.onAction(SupportAction.CloseSection)
        assertEquals(null, viewModel.state.value.selectedSectionId)

        viewModel.onAction(SupportAction.SelectTab(SupportTab.TERMS))
        assertEquals("terms", viewModel.state.value.htmlContent)
        viewModel.onAction(SupportAction.SelectTab(SupportTab.PRIVACY))
        assertEquals("privacy", viewModel.state.value.htmlContent)

        viewModel.onAction(SupportAction.AcceptTerms)
        assertFalse(repository.accepted)
        viewModel.onAction(SupportAction.ToggleTermsAcceptance)
        val accepted = async(start = CoroutineStart.UNDISPATCHED) { viewModel.termsAccepted.first() }
        viewModel.onAction(SupportAction.AcceptTerms)
        accepted.await()
        assertTrue(repository.accepted)
        viewModel.onAction(SupportAction.Back)
    }

    @Test fun `game help loads the selected lottery rules without requesting FAQ`() = runTest {
        val repository = FakeSupportRepository()
        val viewModel = viewModel(
            repository,
            SupportUiState(
                mode = SupportMode.GAME_HELP,
                gameHelpModalityId = 8,
                gameHelpModalityName = "Lotofácil"
            )
        )

        assertTrue(viewModel.state.value.htmlContent.startsWith("Escolha de 15 a 20 números entre os 25"))
        assertFalse(viewModel.state.value.htmlContent.contains("Lorem ipsum", ignoreCase = true))
        assertEquals(0, repository.sectionRequests)
        assertTrue(viewModel.state.value.sections.isEmpty())
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test fun `game help selects live contest metadata for the requested modality`() = runTest {
        val repository = FakeSupportRepository()
        val parameters = BettingParameters(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            10,
            listOf(
                game(2, 3100, "17/10/2026 21:00:00", "17/10/2026 20:30:00"),
                game(8, 3700, "16/10/2026 21:00:00", "16/10/2026 20:30:00")
            )
        )
        val viewModel = viewModel(
            repository,
            SupportUiState(
                mode = SupportMode.GAME_HELP,
                gameHelpModalityId = 8
            ),
            GetBettingParametersUseCase { AppResult.Success(parameters) }
        )

        val state = viewModel.state.value
        assertEquals(3700, state.gameHelpContestNumber)
        assertEquals("16/10/2026 21:00:00", state.gameHelpDrawDate)
        assertEquals("16/10/2026 20:30:00", state.gameHelpClosingDateTime)
        assertEquals(0, repository.sectionRequests)
        assertTrue(state.htmlContent.contains("11, 12, 13, 14 ou 15"))
    }

    @Test fun `game help matches native and runtime lottery IDs for live metadata`() = runTest {
        val cases = listOf(
            Triple(3, 1, "Quina"),
            Triple(1, 3, "Quina"),
            Triple(16, 5, "Lotomania"),
            Triple(5, 16, "Lotomania"),
            Triple(13, 19, "Loteca"),
            Triple(19, 13, "Loteca")
        )
        cases.forEach { (helpId, parameterId, nativeName) ->
            val repository = FakeSupportRepository()
            val parameters = BettingParameters(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                10,
                listOf(
                    game(2, 3100, "17/10/2026 21:00:00", "17/10/2026 20:30:00"),
                    game(parameterId, 7200, "16/10/2026 21:00:00", "16/10/2026 20:30:00")
                )
            )
            val viewModel = viewModel(
                repository,
                SupportUiState(
                    mode = SupportMode.GAME_HELP,
                    gameHelpModalityId = helpId,
                    gameHelpModalityName = nativeName
                ),
                GetBettingParametersUseCase { AppResult.Success(parameters) }
            )

            val state = viewModel.state.value
            assertEquals("Contest for help ID $helpId", 7200, state.gameHelpContestNumber)
            assertEquals("16/10/2026 21:00:00", state.gameHelpDrawDate)
            assertEquals("16/10/2026 20:30:00", state.gameHelpClosingDateTime)
            assertEquals(helpId, state.gameHelpModalityId)
            assertEquals(nativeName, state.gameHelpModalityName)
            assertEquals(0, repository.sectionRequests)
        }
    }

    @Test fun `unavailable contest parameters keep the selected lottery rules`() = runTest {
        val repository = FakeSupportRepository()
        val viewModel = viewModel(
            repository,
            SupportUiState(
                mode = SupportMode.GAME_HELP,
                gameHelpModalityId = 20
            ),
            GetBettingParametersUseCase { AppResult.Failure(AppError.Network) }
        )

        val state = viewModel.state.value
        assertTrue(state.htmlContent.contains("time do coração"))
        assertEquals(null, state.gameHelpContestNumber)
        assertEquals("", state.gameHelpDrawDate)
        assertEquals("", state.gameHelpClosingDateTime)
        assertEquals(0, repository.sectionRequests)
        assertEquals(null, state.errorMessage)
        assertFalse(state.isLoading)
    }

    private fun game(id: Int, contest: Int, draw: String, closing: String) = LotteryGameParameters(
        modality = LotteryModality(id, "Modalidade $id", "CODE$id"),
        contestNumber = contest,
        drawDate = draw,
        closingDateTime = closing,
        maximumNumber = 60,
        minimumSelection = 6,
        maximumSelection = 20,
        maximumSurprises = 10,
        recurringOptions = emptyList(),
        prices = emptyList()
    )

    @Test fun `read only mode loads terms without tabs or acceptance`() = runTest {
        val repository = FakeSupportRepository()
        val viewModel = viewModel(
            repository,
            SupportUiState(
                mode = SupportMode.TERMS_READ_ONLY,
                showTermsTabs = true,
                acceptanceRequired = true,
                termsAccepted = true
            )
        )
        val state = viewModel.state.value
        assertEquals(SupportTab.TERMS, state.selectedTab)
        assertEquals("terms", state.htmlContent)
        assertFalse(state.showTermsTabs)
        assertFalse(state.acceptanceRequired)
        assertFalse(state.termsAccepted)
        viewModel.onAction(SupportAction.ToggleTermsAcceptance)
        viewModel.onAction(SupportAction.AcceptTerms)
        assertFalse(repository.accepted)
    }

    @Test fun `surfaces load and acceptance failures`() = runTest {
        val repository = FailingSupportRepository()
        val viewModel = viewModel(repository)
        assertEquals("Não foi possível carregar o conteúdo", viewModel.state.value.errorMessage)
        viewModel.loadSelectedContent()
        assertEquals("Não foi possível carregar o conteúdo", viewModel.state.value.errorMessage)

        val acceptanceState = SupportUiState(
            selectedTab = SupportTab.TERMS,
            acceptanceRequired = true,
            termsAccepted = true
        )
        val acceptanceViewModel = viewModel(repository, acceptanceState)
        acceptanceViewModel.onAction(SupportAction.AcceptTerms)
        assertEquals("Não foi possível aceitar os termos", acceptanceViewModel.state.value.errorMessage)

        val questionsViewModel = viewModel(repository, SupportUiState(selectedTab = SupportTab.FAQ))
        questionsViewModel.onAction(SupportAction.OpenSection(1, "Seção"))
        assertEquals("Não foi possível carregar o conteúdo", questionsViewModel.state.value.errorMessage)
    }

    private class FakeSupportRepository : SupportRepository {
        var accepted = false
        var sectionRequests = 0
        override suspend fun sections(): List<SupportSection> {
            sectionRequests++
            return listOf(SupportSection(1, "Seção", listOf(SupportQuestion(2, "Pergunta", "Resposta"))))
        }
        override suspend fun questions(sectionId: Long) = sections().single().questions
        override suspend fun terms() = "terms"
        override suspend fun acceptTerms() {
            accepted = true
        }
    }

    private class FailingSupportRepository : SupportRepository {
        override suspend fun sections(): List<SupportSection> = error("load failed")
        override suspend fun questions(sectionId: Long): List<SupportQuestion> = error("load failed")
        override suspend fun terms(): String = error("load failed")
        override suspend fun acceptTerms(): Unit = error("accept failed")
    }

    private fun viewModel(
        repository: SupportRepository,
        state: SupportUiState = SupportUiState(),
        parameters: GetBettingParametersUseCase? = null,
    ) = SupportViewModel(
        GetSupportSections(repository),
        GetSupportQuestions(repository),
        GetTerms(repository),
        AcceptTerms(repository),
        "privacy",
        FixedCoroutineDispatchers(dispatcher),
        state,
        parameters
    )
}
