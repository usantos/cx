package br.gov.caixa.loterias.feature.responsiblegaming

import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingStateAction
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.ResponsibleGamingUiState
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.state.reduceResponsibleGamingState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponsibleGamingReducerTest {
    @Test
    fun `question navigation clears destination answer and preserves mandatory assessment`() {
        val initial = ResponsibleGamingUiState(
            assessmentRequired = true,
            questions = listOf(SelfAssessmentQuestion(1, "One", 1), SelfAssessmentQuestion(2, "Two", 2)),
            answers = mapOf(1 to true, 2 to false)
        )
        val next = reduceResponsibleGamingState(initial, ResponsibleGamingStateAction.NextQuestion)
        assertEquals(1, next.currentQuestionIndex)
        assertFalse(next.answers.containsKey(2))
        val previous = reduceResponsibleGamingState(next, ResponsibleGamingStateAction.PreviousQuestion)
        assertEquals(0, previous.currentQuestionIndex)
        assertFalse(previous.answers.containsKey(1))
        assertTrue(previous.assessmentRequired)
        assertEquals(mapOf(1 to true, 2 to false), initial.answers)
    }
}
