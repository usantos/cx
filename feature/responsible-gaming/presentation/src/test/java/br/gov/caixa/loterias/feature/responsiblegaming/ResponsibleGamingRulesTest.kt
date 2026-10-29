package br.gov.caixa.loterias.feature.responsiblegaming

import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.SocialAmount
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.canAdvanceAssessment
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.canSubmitSuspension
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.formatCurrency
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.formatSocialAmount
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.socialAmount
import java.math.BigDecimal
import org.junit.Assert.assertEquals
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

    @Test fun `social values preserve legacy Brazilian formatting`() {
        assertEquals("R$ 0,00", formatCurrency(null))
        assertEquals("R$ 1.234,56", formatCurrency(BigDecimal("1234.56")))
        assertEquals("R$ 999,0", formatSocialAmount(BigDecimal("999")))
        assertEquals("R$ 1,2 mil", formatSocialAmount(BigDecimal("1234")))
        assertEquals("R$ 2,5 mi", formatSocialAmount(BigDecimal("2500000")))
        assertEquals("R$ 3,4 bi", formatSocialAmount(BigDecimal("3400000000")))
        assertEquals(SocialAmount("0,0", "reais"), socialAmount(null))
        assertEquals(SocialAmount("999,0", "reais"), socialAmount(BigDecimal("999")))
        assertEquals(SocialAmount("1,2", "mil reais"), socialAmount(BigDecimal("1234")))
        assertEquals(SocialAmount("1,5", "milhão de reais"), socialAmount(BigDecimal("1500000")))
        assertEquals(SocialAmount("2,5", "milhões de reais"), socialAmount(BigDecimal("2500000")))
        assertEquals(SocialAmount("1,5", "bilhão de reais"), socialAmount(BigDecimal("1500000000")))
        assertEquals(SocialAmount("3,4", "bilhões de reais"), socialAmount(BigDecimal("3400000000")))
    }
}
