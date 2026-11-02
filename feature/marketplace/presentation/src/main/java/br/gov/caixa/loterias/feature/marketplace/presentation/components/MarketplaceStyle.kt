package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceStyle
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveLoteriasCaixaDSLotteryStyle

internal fun marketplaceText(size: Int, weight: FontWeight = FontWeight.Normal) = LoteriasCaixaDSMarketplaceStyle.text(size, weight)

internal data class MarketplaceColors(
    val light: Color,
    val dark: Color,
    val content: Color = LoteriasCaixaDSColorTokens.White,
    val darkContent: Color = LoteriasCaixaDSColorTokens.White,
    val listText: Color = light,
    val textOnSurface: Color = listText,
    val carouselHeader: Color = light,
)

@Composable
internal fun marketplaceColors(modality: String): MarketplaceColors {
    val style = loteriasCaixaDSLotteryStyle(0, modality)
    val campaignStyle = style != resolveLoteriasCaixaDSLotteryStyle(0, modality, LoteriasCaixaDSCampaign.DEFAULT)
    val dark = if (modality.contains("LOTECA", true)) style.light else style.dark
    return MarketplaceColors(
        style.light,
        dark,
        style.content,
        style.darkContent,
        style.listText,
        if (campaignStyle) style.textOnSurface else style.listText,
        if (campaignStyle) style.dark else style.light
    )
}
