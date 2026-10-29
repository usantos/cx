package br.gov.caixa.loterias.core.domain.support

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection

class SupportModelsTest {
    @Test
    fun `question exposes immutable value semantics`() {
        val question = SupportQuestion(1, "Pergunta", "Resposta")
        val equal = SupportQuestion(1, "Pergunta", "Resposta")

        assertEquals(1, question.id)
        assertEquals("Pergunta", question.title)
        assertEquals("Resposta", question.answer)
        assertEquals(question, equal)
        assertEquals(question.hashCode(), equal.hashCode())
        assertNotEquals(question, question.copy(answer = "Outra"))
        assertEquals("Pergunta", question.component2())
        assertEquals("Resposta", question.component3())
        assertEquals("SupportQuestion(id=1, title=Pergunta, answer=Resposta)", question.toString())
    }

    @Test
    fun `section owns its questions without platform dependencies`() {
        val question = SupportQuestion(2, "P", "R")
        val section = SupportSection(3, "Geral", listOf(question))

        assertEquals(3, section.id)
        assertEquals("Geral", section.title)
        assertEquals(listOf(question), section.questions)
        assertEquals(section, section.copy())
        assertNotEquals(section, section.copy(title = "Outro"))
        assertEquals(section.hashCode(), section.copy().hashCode())
        assertEquals(3, section.component1())
        assertEquals("Geral", section.component2())
        assertEquals(listOf(question), section.component3())
        assertEquals("SupportSection(id=3, title=Geral, questions=[SupportQuestion(id=2, title=P, answer=R)])", section.toString())
    }
}
