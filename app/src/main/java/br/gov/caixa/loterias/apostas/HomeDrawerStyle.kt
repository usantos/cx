package br.gov.caixa.loterias.apostas

import androidx.compose.ui.graphics.Color

internal data class HomeDrawerStyle(
    val accent: Color,
    val rowLabel: Color,
    val footerLabel: Color,
    val exitBackground: Color,
)

internal fun homeDrawerStyle(octoberRose: Boolean): HomeDrawerStyle = HomeDrawerStyle(
    accent = if (octoberRose) Color(0xFFEE3E7F) else Color(0xFF005CA9),
    rowLabel = if (octoberRose) Color(0xFFEE3E7F) else Color(0xFF404B52),
    footerLabel = if (octoberRose) Color(0xFFEE3E7F) else Color(0xFF3864A3),
    exitBackground = if (octoberRose) Color(0xFFEE3E7F) else Color(0xFF005CA9),
)
