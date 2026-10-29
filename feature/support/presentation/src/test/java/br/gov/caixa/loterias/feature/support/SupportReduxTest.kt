package br.gov.caixa.loterias.feature.support

import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMode
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMutation
import br.gov.caixa.loterias.feature.support.presentation.state.SupportStateStore
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import br.gov.caixa.loterias.feature.support.presentation.state.reduceSupportState
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class SupportReduxTest {
    @Test fun togglingQuestionsProducesNewStateWithoutMutatingTheInput() {
        val initial = SupportUiState(expandedQuestionIds = setOf(OLD_ID))
        val mutation = SupportMutation.Action(SupportAction.ToggleQuestion(NEW_ID))
        val first = reduceSupportState(initial, mutation)
        assertEquals(setOf(OLD_ID, NEW_ID), first.expandedQuestionIds)
        assertEquals(setOf(OLD_ID), initial.expandedQuestionIds)
        assertEquals(first, reduceSupportState(initial, mutation))
        assertNotSame(initial, first)
        assertEquals(initial, reduceSupportState(first, mutation))
    }

    @Test fun selectingTabsClearsTransientContentAndPreservesExistingExpansionPolicy() {
        val initial = SupportUiState(
            htmlContent = "old",
            selectedSectionId = OLD_ID,
            selectedSectionTitle = "old",
            questions = listOf(SupportQuestion(OLD_ID, "Q", "A")),
            expandedQuestionIds = setOf(OLD_ID),
            errorMessage = "error"
        )
        val next = reduceSupportState(initial, SupportMutation.Action(SupportAction.SelectTab(SupportTab.TERMS)))
        assertEquals(SupportTab.TERMS, next.selectedTab)
        assertEquals("", next.htmlContent)
        assertNull(next.selectedSectionId)
        assertTrue(next.questions.isEmpty())
        assertEquals(initial.expandedQuestionIds, next.expandedQuestionIds)
        assertNull(next.errorMessage)
    }

    @Test fun loadedAndFailureActionsReplaceOnlyTheirOwnState() {
        val loading = SupportUiState(isLoading = true, errorMessage = "old")
        val section = SupportSection(OLD_ID, "FAQ", emptyList())
        val question = SupportQuestion(NEW_ID, "Q", "A")
        val sections = reduceSupportState(loading, SupportMutation.Action(SupportAction.SectionsLoaded(listOf(section))))
        assertEquals(listOf(section), sections.sections)
        assertFalse(sections.isLoading)
        assertNull(sections.errorMessage)
        val questions = reduceSupportState(loading, SupportMutation.Action(SupportAction.QuestionsLoaded(listOf(question))))
        assertEquals(listOf(question), questions.questions)
        assertFalse(questions.isLoading)
        val html = reduceSupportState(loading, SupportMutation.Action(SupportAction.ContentLoaded("html")))
        assertEquals("html", html.htmlContent)
        assertFalse(html.isLoading)
        val failed = reduceSupportState(loading, SupportMutation.Action(SupportAction.LoadFailed("offline")))
        assertEquals("offline", failed.errorMessage)
        assertFalse(failed.isLoading)
    }

    @Test fun sectionNavigationAndLoadingPreserveTheOtherFields() {
        val initial = SupportUiState(htmlContent = "keep", expandedQuestionIds = setOf(OLD_ID))
        val opened = reduceSupportState(initial, SupportMutation.Action(SupportAction.OpenSection(NEW_ID, "New")))
        assertEquals(NEW_ID, opened.selectedSectionId)
        assertEquals("New", opened.selectedSectionTitle)
        assertTrue(opened.expandedQuestionIds.isEmpty())
        assertEquals("keep", opened.htmlContent)
        val closed = reduceSupportState(opened, SupportMutation.Action(SupportAction.CloseSection))
        assertNull(closed.selectedSectionId)
        val loading = reduceSupportState(closed, SupportMutation.Action(SupportAction.StartLoading))
        assertTrue(loading.isLoading)
    }

    @Test fun gameHelpClearsOldDatesAndThenReceivesTheMatchingContestMetadata() {
        val initial = SupportUiState(
            isLoading = true,
            errorMessage = "old",
            gameHelpContestNumber = OLD_CONTEST,
            gameHelpDrawDate = "old",
            gameHelpClosingDateTime = "old"
        )
        val rules = reduceSupportState(initial, SupportMutation.GameHelpLoaded("rules"))
        assertEquals("rules", rules.htmlContent)
        assertNull(rules.gameHelpContestNumber)
        assertEquals("", rules.gameHelpDrawDate)
        assertFalse(rules.isLoading)
        val dated = reduceSupportState(rules, SupportMutation.GameParametersLoaded(NEW_CONTEST, "draw", "close"))
        assertEquals(NEW_CONTEST, dated.gameHelpContestNumber)
        assertEquals("draw", dated.gameHelpDrawDate)
        assertEquals("close", dated.gameHelpClosingDateTime)
    }

    @Test fun acceptanceEffectsAreNotExecutedByTheReducer() {
        val initial = SupportUiState(isLoading = true)
        val checked = reduceSupportState(initial, SupportMutation.Action(SupportAction.ToggleTermsAcceptance))
        assertTrue(checked.termsAccepted)
        assertSame(checked, reduceSupportState(checked, SupportMutation.Action(SupportAction.AcceptTerms)))
        assertSame(checked, reduceSupportState(checked, SupportMutation.Action(SupportAction.Back)))
        val failed = reduceSupportState(checked, SupportMutation.AcceptanceFailed("failed"))
        assertEquals("failed", failed.errorMessage)
        assertTrue(failed.isLoading)
    }

    @Test fun storeNormalizesReadOnlyTermsAndExposesOnlyAReadOnlyProjection() {
        SupportStateStore(
            SupportUiState(
                mode = SupportMode.TERMS_READ_ONLY,
                termsAccepted = true,
                showTermsTabs = true,
                acceptanceRequired = true,
                selectedSectionId = OLD_ID
            )
        ).use { store ->
            val state = store.state.value
            assertEquals(SupportTab.TERMS, state.selectedTab)
            assertFalse(state.termsAccepted)
            assertFalse(state.showTermsTabs)
            assertFalse(state.acceptanceRequired)
            assertNull(state.selectedSectionId)
            assertFalse(store.state is MutableStateFlow<*>)
        }
    }

    @Test fun stateProjectionTracksDispatchAndStopsObservingAfterClose() {
        val store = SupportStateStore(SupportUiState())
        store.dispatch(SupportMutation.Action(SupportAction.ToggleTermsAcceptance))
        assertTrue(store.state.value.termsAccepted)
        store.close()
        store.dispatch(SupportMutation.Action(SupportAction.ToggleTermsAcceptance))
        assertTrue(store.state.value.termsAccepted)
        store.close()
    }

    @Test fun concurrentDispatchDoesNotLoseUpdates() {
        SupportStateStore(SupportUiState()).use { store ->
            val executor = Executors.newFixedThreadPool(WORKERS)
            try {
                val tasks = (1..ACTION_COUNT).map { id ->
                    executor.submit {
                        store.dispatch(SupportMutation.Action(SupportAction.ToggleQuestion(id.toLong())))
                    }
                }
                tasks.forEach { it.get(TIMEOUT_SECONDS, TimeUnit.SECONDS) }
                assertEquals((1..ACTION_COUNT).map(Int::toLong).toSet(), store.state.value.expandedQuestionIds)
            } finally {
                executor.shutdownNow()
            }
        }
    }
}

private const val OLD_ID = 8L
private const val NEW_ID = 9L
private const val OLD_CONTEST = 100
private const val NEW_CONTEST = 101
private const val WORKERS = 4
private const val ACTION_COUNT = 100
private const val TIMEOUT_SECONDS = 5L
