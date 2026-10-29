package br.gov.caixa.loterias.feature.home.presentation.style

import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

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
    val palette = br.gov.caixa.loterias.core.designsystem.theme.caixaDSCampaignPalette(campaign.toDSCampaign())
    val october = campaign == br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign.OUTUBRO_ROSA
    return HomeDrawerStyle(
        accent = palette.primary,
        rowLabel = if (campaign == null) CaixaDSColorTokens.BetAction else palette.primary,
        footerLabel = CaixaDSColorTokens.White,
        exitBackground = palette.primary,
        shakeAccent = if (october) CaixaDSColorTokens.OctoberRose else CaixaDSColorTokens.CaixaBlue,
        shakeOnAccent = CaixaDSColorTokens.White,
        shakeTooltipBackground = if (october) CaixaDSColorTokens.OctoberRoseTooltip else CaixaDSColorTokens.CaixaBlue,
        invertShakeColors = october,
    )
}
