package br.gov.caixa.loterias.feature.responsiblegaming

import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.canAdvanceAssessment
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.canSubmitSuspension
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponsibleGamingRulesTest {
    @Test fun `assessment accepts only known answers`() {
        assertTrue(canAdvanceAssessment("Sim"))
        assertTrue(canAdvanceAssessment("Não"))
        assertFalse(canAdvanceAssessment(null))
        assertFalse(canAdvanceAssessment("Talvez"))
    }

    @Test fun `suspension requires a valid period and acknowledgement`() {
        listOf("24 horas", "48 horas", "7 dias", "15 dias", "30 dias").forEach {
            assertTrue(canSubmitSuspension(it, true))
        }
        assertFalse(canSubmitSuspension("24 horas", false))
        assertFalse(canSubmitSuspension(null, true))
        assertFalse(canSubmitSuspension("1 ano", true))
    }
}
