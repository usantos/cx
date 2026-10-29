package br.gov.caixa.loterias.core.designsystem.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

/** Catálogo de renderização. Ativação e datas pertencem à configuração de domínio e servidor. */
enum class CaixaDSCampaign { DEFAULT, SEPTEMBER_BLUE, OCTOBER_ROSE }

data class CaixaDSCampaignPalette(
    val name: String?,
    val primary: Color,
    val strong: Color,
    val drawerGradient: List<Color>,
)

fun caixaDSCampaignPalette(campaign: CaixaDSCampaign): CaixaDSCampaignPalette = when (campaign) {
    CaixaDSCampaign.DEFAULT -> CaixaDSCampaignPalette(null, CaixaDSColorTokens.Blue700,
        CaixaDSColorTokens.Blue900, listOf(CaixaDSColorTokens.Blue500, CaixaDSColorTokens.Blue700, CaixaDSColorTokens.Blue900))
    CaixaDSCampaign.SEPTEMBER_BLUE -> CaixaDSCampaignPalette("Setembro Azul", CaixaDSColorTokens.Blue500,
        CaixaDSColorTokens.Blue900, listOf(CaixaDSColorTokens.Blue500, CaixaDSColorTokens.Blue700, CaixaDSColorTokens.Blue900))
    CaixaDSCampaign.OCTOBER_ROSE -> CaixaDSCampaignPalette("Outubro Rosa", CaixaDSColorTokens.OctoberRose,
        CaixaDSColorTokens.OctoberRoseDark, listOf(
            lerp(CaixaDSColorTokens.OctoberRose, CaixaDSColorTokens.OctoberRoseDark, .55f),
            CaixaDSColorTokens.OctoberRoseDark, CaixaDSColorTokens.OctoberRoseDark))
}

val LocalCaixaDSCampaign = staticCompositionLocalOf { CaixaDSCampaign.DEFAULT }
val LocalCaixaDSMegaSenaCampaign = staticCompositionLocalOf { CaixaDSCampaign.DEFAULT }

@Composable
fun CaixaDSCampaignTheme(
    campaign: CaixaDSCampaign,
    megaSenaCampaign: CaixaDSCampaign = CaixaDSCampaign.DEFAULT,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalCaixaDSCampaign provides campaign,
        LocalCaixaDSMegaSenaCampaign provides megaSenaCampaign, content = content)
}

