package br.gov.caixa.loterias.apostas

import androidx.compose.ui.graphics.toArgb
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign
import br.gov.caixa.loterias.feature.home.presentation.style.homeDrawerStyle
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeDrawerStyleTest {
    @Test fun octoberCampaignUsesLegacyStrongPinkAndReadableLabels() {
        val style = homeDrawerStyle(CommemorativeCampaign.OUTUBRO_ROSA)
        assertEquals(CaixaDSColorTokens.OctoberRose.toArgb(), style.exitBackground.toArgb())
        assertEquals(CaixaDSColorTokens.OctoberRose.toArgb(), style.accent.toArgb())
        assertEquals(CaixaDSColorTokens.OctoberRose.toArgb(), style.rowLabel.toArgb())
        assertEquals(CaixaDSColorTokens.White.toArgb(), style.footerLabel.toArgb())
    }

    @Test fun noCampaignUsesCaixaBlue() {
        val style = homeDrawerStyle(null)
        assertEquals(CaixaDSColorTokens.Blue700.toArgb(), style.exitBackground.toArgb())
    }
}
