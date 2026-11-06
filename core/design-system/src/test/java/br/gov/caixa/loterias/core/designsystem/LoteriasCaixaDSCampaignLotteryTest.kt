package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.graphics.toArgb
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.resolveLoteriasCaixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.resolveLoteriasCaixaDSLotteryStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class LoteriasCaixaDSCampaignLotteryTest {
    @Test fun octoberMegaUsesThreeRolesAndWhiteOnDark() {
        val style = resolveLoteriasCaixaDSLotteryStyle(2, "Mega-Sena", LoteriasCaixaDSCampaign.OCTOBER_ROSE)
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
            CampaignLotteryTestExpectations.QUINA_ID to "Quina", CampaignLotteryTestExpectations.LOTOFACIL_ID to "Lotofácil",
            CampaignLotteryTestExpectations.MILIONARIA_ID to "+Milionária",
            CampaignLotteryTestExpectations.DIA_DE_SORTE_ID to "DIA_DE_SORTE",
            CampaignLotteryTestExpectations.LOTOMANIA_ID to "Lotomania", CampaignLotteryTestExpectations.DUPLA_SENA_ID to "DUPLASENA",
            CampaignLotteryTestExpectations.LOTECA_ID to "Loteca", CampaignLotteryTestExpectations.TIMEMANIA_ID to "Timemania",
            0 to "Todas", 0 to "Federal"
        )) {
            assertEquals(
                name,
                resolveLoteriasCaixaDSLotteryStyle(id, name, LoteriasCaixaDSCampaign.DEFAULT),
                resolveLoteriasCaixaDSLotteryStyle(id, name, LoteriasCaixaDSCampaign.OCTOBER_ROSE)
            )
            LoteriasCaixaDSLotteryArtwork.entries.forEach { role ->
                assertNull(name, resolveLoteriasCaixaDSCampaignDrawable(id, name, role, LoteriasCaixaDSCampaign.OCTOBER_ROSE))
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
            assertEquals(resolveLoteriasCaixaDSLotteryStyle(0, a), resolveLoteriasCaixaDSLotteryStyle(0, b))
        }
    }

    @Test fun campaignArtworkRequiresActiveMegaAndDoesNotAlterUnchangedSpecialArtwork() {
        assertNull(resolveLoteriasCaixaDSCampaignDrawable(2, "", LoteriasCaixaDSLotteryArtwork.TRAPEZE, LoteriasCaixaDSCampaign.DEFAULT))
        assertNotNull(
            resolveLoteriasCaixaDSCampaignDrawable(2, "", LoteriasCaixaDSLotteryArtwork.TRAPEZE, LoteriasCaixaDSCampaign.OCTOBER_ROSE)
        )
        assertEquals(
            R.drawable.legacy_campaign_mega_virada_quadrado,
            resolveLoteriasCaixaDSCampaignDrawable(
                2,
                "",
                LoteriasCaixaDSLotteryArtwork.SPECIAL_SQUARE,
                LoteriasCaixaDSCampaign.OCTOBER_ROSE
            )
        )
    }
}

private object CampaignLotteryTestExpectations {
    const val QUINA_ID = 3
    const val LOTOFACIL_ID = 8
    const val MILIONARIA_ID = 9
    const val DIA_DE_SORTE_ID = 11
    const val LOTOMANIA_ID = 16
    const val DUPLA_SENA_ID = 18
    const val LOTECA_ID = 19
    const val TIMEMANIA_ID = 20
}
