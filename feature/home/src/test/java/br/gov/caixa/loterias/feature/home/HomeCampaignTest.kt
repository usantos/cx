package br.gov.caixa.loterias.feature.home

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeCampaignTest {
    @Test
    fun outubroRosaIsActiveDuringOctoberOnly() {
        assertEquals(HomeCampaign.OUTUBRO_ROSA, homeCampaignAt(LocalDate.of(2026, 10, 1)))
        assertEquals(HomeCampaign.OUTUBRO_ROSA, homeCampaignAt(LocalDate.of(2026, 10, 31)))
        assertEquals(HomeCampaign.DEFAULT, homeCampaignAt(LocalDate.of(2026, 9, 30)))
        assertEquals(HomeCampaign.DEFAULT, homeCampaignAt(LocalDate.of(2026, 11, 1)))
    }

    @Test
    fun outubroRosaUsesInstalledMegaSenaPalette() {
        val campaign = homeCampaignAt(LocalDate.of(2026, 10, 7))

        assertEquals(0xFFEE3E7FL, campaign.megaSenaPrimaryArgb)
        assertEquals(0xFF9C005CL, campaign.megaSenaDarkArgb)
    }
}
