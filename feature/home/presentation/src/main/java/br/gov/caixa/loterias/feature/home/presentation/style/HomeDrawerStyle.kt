package br.gov.caixa.loterias.feature.home.presentation.style

import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

import androidx.compose.ui.graphics.Color
import br.gov.caixa.loterias.core.presentation.mapper.toDSCampaign

data class HomeDrawerStyle(
    val accent: Color,
    val rowLabel: Color,
    val footerLabel: Color,
    val exitBackground: Color,
    val shakeAccent: Color = accent,
    val shakeOnAccent: Color = exitBackground,
    val shakeTooltipBackground: Color = exitBackground,
    val invertShakeColors: Boolean = false,
)

fun homeDrawerStyle(campaign: br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign?): HomeDrawerStyle {
    val palette = br.gov.caixa.loterias.core.designsystem.theme.loteriasCaixaDSCampaignPalette(campaign.toDSCampaign())
    val october = campaign == br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign.OUTUBRO_ROSA
    return HomeDrawerStyle(
        accent = palette.primary,
        rowLabel = if (campaign == null) LoteriasCaixaDSColorTokens.BetAction else palette.primary,
        footerLabel = LoteriasCaixaDSColorTokens.White,
        exitBackground = palette.primary,
        shakeAccent = if (october) LoteriasCaixaDSColorTokens.OctoberRose else LoteriasCaixaDSColorTokens.CaixaBlue,
        shakeOnAccent = LoteriasCaixaDSColorTokens.White,
        shakeTooltipBackground = if (october) LoteriasCaixaDSColorTokens.OctoberRoseTooltip else LoteriasCaixaDSColorTokens.CaixaBlue,
        invertShakeColors = october,
    )
}
