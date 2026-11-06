package br.gov.caixa.loterias.core.domain.configuration

import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignActivation
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaignSettings

class CommemorativeCampaignTest {
    private val october = LocalDate.of(2026, 10, 15)

    @Test fun calendarAloneDoesNotActivateAnyArea() {
        CampaignArea.entries.forEach { assertNull(CommemorativeCampaignSettings().active(it, october)) }
    }

    @Test fun backendCanEnableCampaignOutsideItsNamedMonth() {
        val settings = CommemorativeCampaignSettings(listOf(CampaignActivation(
            CommemorativeCampaign.OUTUBRO_ROSA, setOf(CampaignArea.DRAWER))))
        assertEquals(CommemorativeCampaign.OUTUBRO_ROSA, settings.active(CampaignArea.DRAWER, october.minusMonths(3)))
        assertNull(settings.active(CampaignArea.MEGA_SENA, october))
    }

    @Test fun datesAreInclusiveAndDisabledRulesNeverApply() {
        val rule = CampaignActivation(CommemorativeCampaign.SETEMBRO_AZUL, setOf(CampaignArea.HOME),
            startsOn = october.minusDays(1), endsOn = october.plusDays(1))
        assertTrue(rule.isActive(october.minusDays(1)))
        assertTrue(rule.isActive(october.plusDays(1)))
        assertFalse(rule.isActive(october.minusDays(2)))
        assertFalse(rule.isActive(october.plusDays(2)))
        assertFalse(rule.copy(enabled = false).isActive(october))
        assertFalse(rule.copy(startsOn = october.plusDays(3)).isActive(october))
    }

    @Test fun highestPriorityWinsPerAreaAndTiesPreserveBackendOrder() {
        val first = CampaignActivation(CommemorativeCampaign.OUTUBRO_ROSA, setOf(CampaignArea.DRAWER), priority = 1)
        val second = CampaignActivation(CommemorativeCampaign.SETEMBRO_AZUL, setOf(CampaignArea.HOME, CampaignArea.DRAWER), priority = 2)
        assertEquals(CommemorativeCampaign.SETEMBRO_AZUL,
            CommemorativeCampaignSettings(listOf(first, second)).active(CampaignArea.DRAWER, october))
        assertEquals(CommemorativeCampaign.OUTUBRO_ROSA,
            CommemorativeCampaignSettings(listOf(first, second.copy(priority = 1))).active(CampaignArea.DRAWER, october))
    }
}
