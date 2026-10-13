package br.gov.caixa.loterias.feature.support

import br.gov.caixa.loterias.core.domain.support.SupportQuestion
import br.gov.caixa.loterias.core.domain.support.SupportSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class SupportReducerTest {
    private val question = SupportQuestion(1, "Como jogar?", "Escolha seus números")
    private val section = SupportSection(2, "Apostas", listOf(question))

    @Test
    fun `tab selection clears transient content and error`() {
        val state = SupportUiState(htmlContent = "old", errorMessage = "error")
            .reduce(SupportAction.SelectTab(SupportTab.TERMS))

        assertEquals(SupportTab.TERMS, state.selectedTab)
        assertEquals("", state.htmlContent)
        assertNull(state.errorMessage)
    }

    @Test
    fun `question toggles expanded state`() {
        val expanded = SupportUiState().reduce(SupportAction.ToggleQuestion(1))
        assertTrue(1L in expanded.expandedQuestionIds)
        val collapsed = expanded.reduce(SupportAction.ToggleQuestion(1))
        assertFalse(1L in collapsed.expandedQuestionIds)
    }

    @Test
    fun `opening and closing section mirrors legacy two level navigation`() {
        val opened = SupportUiState(expandedQuestionIds = setOf(99))
            .reduce(SupportAction.OpenSection(2, "Apostas"))
        assertEquals(2L, opened.selectedSectionId)
        assertEquals("Apostas", opened.selectedSectionTitle)
        assertTrue(opened.expandedQuestionIds.isEmpty())

        val loaded = opened.reduce(SupportAction.QuestionsLoaded(listOf(question)))
        assertEquals(listOf(question), loaded.questions)
        assertFalse(loaded.isLoading)

        val closed = loaded.reduce(SupportAction.CloseSection)
        assertNull(closed.selectedSectionId)
        assertTrue(closed.questions.isEmpty())
    }

    @Test
    fun `loading content sections and errors replace transient state`() {
        val loading = SupportUiState(errorMessage = "old").reduce(SupportAction.StartLoading)
        assertTrue(loading.isLoading)
        assertNull(loading.errorMessage)

        val content = loading.reduce(SupportAction.ContentLoaded("<p>Termos</p>"))
        assertFalse(content.isLoading)
        assertEquals("<p>Termos</p>", content.htmlContent)

        val sections = loading.reduce(SupportAction.SectionsLoaded(listOf(section)))
        assertEquals(listOf(section), sections.sections)
        assertFalse(sections.isLoading)

        val error = loading.reduce(SupportAction.LoadFailed("Falha"))
        assertFalse(error.isLoading)
        assertEquals("Falha", error.errorMessage)
    }

    @Test
    fun `acceptance toggles and terminal actions preserve state`() {
        val state = SupportUiState()
        val accepted = state.reduce(SupportAction.ToggleTermsAcceptance)
        assertTrue(accepted.termsAccepted)
        assertFalse(accepted.reduce(SupportAction.ToggleTermsAcceptance).termsAccepted)
        assertSame(state, state.reduce(SupportAction.AcceptTerms))
        assertSame(state, state.reduce(SupportAction.Back))
    }

    @Test
    fun `game help modality remains stable across reducer actions`() {
        val state = SupportUiState(
            mode = SupportMode.GAME_HELP,
            gameHelpModalityId = 20,
            gameHelpModalityName = "Timemania",
        )

        val loading = state.reduce(SupportAction.StartLoading)

        assertEquals(SupportMode.GAME_HELP, loading.mode)
        assertEquals(20, loading.gameHelpModalityId)
        assertEquals("Timemania", loading.gameHelpModalityName)
    }
}
