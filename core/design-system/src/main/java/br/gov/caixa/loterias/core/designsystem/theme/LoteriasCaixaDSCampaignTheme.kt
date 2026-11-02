package br.gov.caixa.loterias.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

/** Catálogo de renderização. Ativação e datas pertencem à configuração de domínio e servidor. */
enum class LoteriasCaixaDSCampaign { DEFAULT, SEPTEMBER_BLUE, OCTOBER_ROSE }

data class LoteriasCaixaDSCampaignPalette(val name: String?, val primary: Color, val strong: Color, val drawerGradient: List<Color>)

fun loteriasCaixaDSCampaignPalette(campaign: LoteriasCaixaDSCampaign): LoteriasCaixaDSCampaignPalette = when (campaign) {
    LoteriasCaixaDSCampaign.DEFAULT -> LoteriasCaixaDSCampaignPalette(
        null,
        LoteriasCaixaDSColorTokens.Blue700,
        LoteriasCaixaDSColorTokens.Blue900,
        listOf(LoteriasCaixaDSColorTokens.Blue500, LoteriasCaixaDSColorTokens.Blue700, LoteriasCaixaDSColorTokens.Blue900)
    )

    LoteriasCaixaDSCampaign.SEPTEMBER_BLUE -> LoteriasCaixaDSCampaignPalette(
        "Setembro Azul",
        LoteriasCaixaDSColorTokens.Blue500,
        LoteriasCaixaDSColorTokens.Blue900,
        listOf(LoteriasCaixaDSColorTokens.Blue500, LoteriasCaixaDSColorTokens.Blue700, LoteriasCaixaDSColorTokens.Blue900)
    )

    LoteriasCaixaDSCampaign.OCTOBER_ROSE -> LoteriasCaixaDSCampaignPalette(
        "Outubro Rosa",
        LoteriasCaixaDSColorTokens.OctoberRose,
        LoteriasCaixaDSColorTokens.OctoberRoseDark,
        listOf(
            lerp(
                LoteriasCaixaDSColorTokens.OctoberRose,
                LoteriasCaixaDSColorTokens.OctoberRoseDark,
                CampaignThemeMetrics.CAMPAIGN_GRADIENT_BLEND_FRACTION
            ),
            LoteriasCaixaDSColorTokens.OctoberRoseDark,
            LoteriasCaixaDSColorTokens.OctoberRoseDark
        )
    )
}

val LocalLoteriasCaixaDSCampaign = staticCompositionLocalOf { LoteriasCaixaDSCampaign.DEFAULT }
val LocalLoteriasCaixaDSMegaSenaCampaign = staticCompositionLocalOf { LoteriasCaixaDSCampaign.DEFAULT }

@Composable
fun LoteriasCaixaDSCampaignTheme(
    campaign: LoteriasCaixaDSCampaign,
    megaSenaCampaign: LoteriasCaixaDSCampaign = LoteriasCaixaDSCampaign.DEFAULT,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalLoteriasCaixaDSCampaign provides campaign,
        LocalLoteriasCaixaDSMegaSenaCampaign provides megaSenaCampaign,
        content = content
    )
}

private object CampaignThemeMetrics {
    const val CAMPAIGN_GRADIENT_BLEND_FRACTION = .55f
}
