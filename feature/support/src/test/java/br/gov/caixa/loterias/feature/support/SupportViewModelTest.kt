package br.gov.caixa.loterias.feature.support

import br.gov.caixa.loterias.core.domain.support.SupportQuestion
import br.gov.caixa.loterias.core.domain.support.SupportRepository
import br.gov.caixa.loterias.core.domain.support.SupportSection
import br.gov.caixa.loterias.core.domain.support.AcceptTerms
import br.gov.caixa.loterias.core.domain.support.GetSupportSections
import br.gov.caixa.loterias.core.domain.support.GetSupportQuestions
import br.gov.caixa.loterias.core.domain.support.GetTerms
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SupportViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private val dispatcher = UnconfinedTestDispatcher()

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

    @Test fun `read only mode loads terms without tabs or acceptance`() = runTest {
        val repository = FakeSupportRepository()
        val viewModel = viewModel(repository, SupportUiState(
            mode = SupportMode.TERMS_READ_ONLY,
            showTermsTabs = true,
            acceptanceRequired = true,
            termsAccepted = true,
        ))
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
            termsAccepted = true,
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
        override suspend fun sections() = listOf(
            SupportSection(1, "Seção", listOf(SupportQuestion(2, "Pergunta", "Resposta"))),
        )
        override suspend fun questions(sectionId: Long) = sections().single().questions
        override suspend fun terms() = "terms"
        override suspend fun acceptTerms() { accepted = true }
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
    ) = SupportViewModel(
        GetSupportSections(repository),
        GetSupportQuestions(repository),
        GetTerms(repository),
        AcceptTerms(repository),
        "privacy",
        FixedCoroutineDispatchers(dispatcher),
        state,
    )
}
