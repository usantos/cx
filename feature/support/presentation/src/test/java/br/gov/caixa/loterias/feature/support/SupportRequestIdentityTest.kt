package br.gov.caixa.loterias.feature.support

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.domain.usecase.AcceptTermsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportQuestionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetSupportSectionsUseCase
import br.gov.caixa.loterias.feature.support.domain.usecase.GetTermsUseCase
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import br.gov.caixa.loterias.feature.support.presentation.viewmodel.SupportViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SupportRequestIdentityTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()
    private val dispatcher = UnconfinedTestDispatcher()

    @Test fun delayedTermsCannotReplacePrivacyEvenWhenProviderIgnoresCancellation() = runTest {
        val terms = CompletableDeferred<AppResult<String>>()
        val viewModel = viewModel(terms = { withContext(NonCancellable) { terms.await() } })
        viewModel.onAction(SupportAction.SelectTab(SupportTab.TERMS))
        viewModel.onAction(SupportAction.SelectTab(SupportTab.PRIVACY))
        assertEquals("privacy", viewModel.state.value.htmlContent)
        terms.complete(AppResult.Success("obsolete terms"))
        assertEquals(SupportTab.PRIVACY, viewModel.state.value.selectedTab)
        assertEquals("privacy", viewModel.state.value.htmlContent)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test fun sectionAResponseCannotReplaceSectionBQuestions() = runTest {
        val a = CompletableDeferred<AppResult<List<SupportQuestion>>>()
        val b = CompletableDeferred<AppResult<List<SupportQuestion>>>()
        val viewModel = viewModel(questions = { id -> withContext(NonCancellable) { if (id == 1L) a.await() else b.await() } })
        viewModel.onAction(SupportAction.OpenSection(1, "A"))
        viewModel.onAction(SupportAction.OpenSection(2, "B"))
        val current = listOf(SupportQuestion(2, "B question", "B answer"))
        b.complete(AppResult.Success(current))
        a.complete(AppResult.Success(listOf(SupportQuestion(1, "A question", "A answer"))))
        assertEquals(2L, viewModel.state.value.selectedSectionId)
        assertEquals(current, viewModel.state.value.questions)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test fun closingSectionInvalidatesNonCooperativeResponse() = runTest {
        val response = CompletableDeferred<AppResult<List<SupportQuestion>>>()
        val viewModel = viewModel(questions = { withContext(NonCancellable) { response.await() } })
        viewModel.onAction(SupportAction.OpenSection(1, "A"))
        viewModel.onAction(SupportAction.CloseSection)
        response.complete(AppResult.Success(listOf(SupportQuestion(1, "obsolete", "obsolete"))))
        assertNull(viewModel.state.value.selectedSectionId)
        assertTrue(viewModel.state.value.questions.isEmpty())
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test fun returningToTermsRejectsEarlierGenerationOfSameTab() = runTest {
        val old = CompletableDeferred<AppResult<String>>()
        val current = CompletableDeferred<AppResult<String>>()
        var calls = 0
        val viewModel = viewModel(terms = {
            calls++
            withContext(NonCancellable) { if (calls == 1) old.await() else current.await() }
        })
        viewModel.onAction(SupportAction.SelectTab(SupportTab.TERMS))
        viewModel.onAction(SupportAction.SelectTab(SupportTab.PRIVACY))
        viewModel.onAction(SupportAction.SelectTab(SupportTab.TERMS))
        current.complete(AppResult.Success("current terms"))
        old.complete(AppResult.Success("obsolete terms"))
        assertEquals("current terms", viewModel.state.value.htmlContent)
        assertFalse(viewModel.state.value.isLoading)
    }

    private fun viewModel(
        terms: suspend () -> AppResult<String> = { AppResult.Success("terms") },
        questions: suspend (Long) -> AppResult<List<SupportQuestion>> = { AppResult.Success(emptyList()) },
    ) = SupportViewModel(
        GetSupportSectionsUseCase { AppResult.Success(emptyList()) },
        GetSupportQuestionsUseCase { questions(it) },
        GetTermsUseCase { terms() },
        AcceptTermsUseCase { AppResult.Success(Unit) },
        "privacy",
        FixedCoroutineDispatchers(dispatcher),
        SupportUiState()
    )
}
