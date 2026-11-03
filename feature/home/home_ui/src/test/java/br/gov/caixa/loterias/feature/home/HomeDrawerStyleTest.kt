package br.gov.caixa.loterias.feature.home

import androidx.compose.ui.graphics.toArgb
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign
import br.gov.caixa.loterias.feature.home.presentation.style.homeDrawerStyle
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeDrawerStyleTest {
    @Test fun octoberCampaignUsesLegacyStrongPinkAndReadableLabels() {
        val style = homeDrawerStyle(CommemorativeCampaign.OUTUBRO_ROSA)
        assertEquals(LoteriasCaixaDSColorTokens.OctoberRose.toArgb(), style.exitBackground.toArgb())
        assertEquals(LoteriasCaixaDSColorTokens.OctoberRose.toArgb(), style.accent.toArgb())
        assertEquals(LoteriasCaixaDSColorTokens.OctoberRose.toArgb(), style.rowLabel.toArgb())
        assertEquals(LoteriasCaixaDSColorTokens.White.toArgb(), style.footerLabel.toArgb())
    }

    @Test fun noCampaignUsesCaixaBlue() {
        val style = homeDrawerStyle(null)
        assertEquals(LoteriasCaixaDSColorTokens.Blue700.toArgb(), style.exitBackground.toArgb())
    }
}
