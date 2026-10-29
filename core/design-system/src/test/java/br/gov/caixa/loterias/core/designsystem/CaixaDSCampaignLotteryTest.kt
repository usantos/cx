package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.graphics.toArgb
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.resolveCaixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CaixaDSCampaignLotteryTest {
    @Test fun octoberMegaUsesThreeRolesAndWhiteOnDark() {
        val style = resolveCaixaDSLotteryStyle(2, "Mega-Sena", CaixaDSCampaign.OCTOBER_ROSE)
        assertEquals(0xFFF4ADBC.toInt(), style.light.toArgb())
        assertEquals(0xFFEE3E7F.toInt(), style.dark.toArgb())
        assertEquals(0xFF9C005C.toInt(), style.content.toArgb())
        assertEquals(0xFFFFFFFF.toInt(), style.darkContent.toArgb())
        assertEquals(style.dark, style.numeral)
        assertEquals(style.dark, style.listText)
        assertEquals(style.content, style.textOnSurface)
    }

    @Test fun otherModalitiesAndUnknownEntriesNeverReceiveOctoberOverrides() {
        for ((id, name) in listOf(
            3 to "Quina", 8 to "Lotofácil", 9 to "+Milionária", 11 to "DIA_DE_SORTE",
            16 to "Lotomania", 18 to "DUPLASENA", 19 to "Loteca", 20 to "Timemania", 0 to "Todas", 0 to "Federal"
        )) {
            assertEquals(
                name,
                resolveCaixaDSLotteryStyle(id, name, CaixaDSCampaign.DEFAULT),
                resolveCaixaDSLotteryStyle(id, name, CaixaDSCampaign.OCTOBER_ROSE)
            )
            CaixaDSLotteryArtwork.entries.forEach { role ->
                assertNull(name, resolveCaixaDSCampaignDrawable(id, name, role, CaixaDSCampaign.OCTOBER_ROSE))
            }
        }
    }

    @Test fun compactAndAccentedNamesShareCanonicalIdentity() {
        for ((a, b) in listOf(
            "DUPLASENA" to "Dupla Sena",
            "DIADESORTE" to "Dia de Sorte",
            "SUPERSETE" to "SUPER_7",
            "MAISMILIONARIA" to "+Milionária",
            "MEGASENA" to "Mega-Sena"
        )) {
            assertEquals(resolveCaixaDSLotteryStyle(0, a), resolveCaixaDSLotteryStyle(0, b))
        }
    }

    @Test fun campaignArtworkRequiresActiveMegaAndDoesNotAlterUnchangedSpecialArtwork() {
        assertNull(resolveCaixaDSCampaignDrawable(2, "", CaixaDSLotteryArtwork.TRAPEZE, CaixaDSCampaign.DEFAULT))
        assertNotNull(resolveCaixaDSCampaignDrawable(2, "", CaixaDSLotteryArtwork.TRAPEZE, CaixaDSCampaign.OCTOBER_ROSE))
        assertEquals(
            R.drawable.legacy_campaign_mega_virada_quadrado,
            resolveCaixaDSCampaignDrawable(2, "", CaixaDSLotteryArtwork.SPECIAL_SQUARE, CaixaDSCampaign.OCTOBER_ROSE)
        )
    }
}
