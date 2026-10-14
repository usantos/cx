package br.gov.caixa.loterias.apostas

import androidx.compose.ui.graphics.Color
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens

internal data class HomeDrawerStyle(
    val accent: Color,
    val rowLabel: Color,
    val footerLabel: Color,
    val exitBackground: Color,
)

internal fun homeDrawerStyle(octoberRose: Boolean): HomeDrawerStyle = HomeDrawerStyle(
    accent = if (octoberRose) CaixaDSColorTokens.LegacyOctoberRose else CaixaDSColorTokens.Blue700,
    rowLabel = if (octoberRose) CaixaDSColorTokens.LegacyOctoberRose else CaixaDSColorTokens.LegacySocialGraphite,
    footerLabel = if (octoberRose) CaixaDSColorTokens.LegacyOctoberRose else CaixaDSColorTokens.LegacyDrawerInstitutionHeading,
    exitBackground = if (octoberRose) CaixaDSColorTokens.LegacyOctoberRose else CaixaDSColorTokens.Blue700,
)
