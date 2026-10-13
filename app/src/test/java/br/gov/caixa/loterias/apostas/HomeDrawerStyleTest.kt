package br.gov.caixa.loterias.apostas

import androidx.compose.ui.graphics.toArgb
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeDrawerStyleTest {
    @Test fun octoberRoseDrawerUsesLegacyPinkForAccentLabelsAndExit() {
        val style = homeDrawerStyle(octoberRose = true)
        val legacyPink = 0xFFEE3E7Fu.toInt()

        assertEquals(legacyPink, style.accent.toArgb())
        assertEquals(legacyPink, style.rowLabel.toArgb())
        assertEquals(legacyPink, style.footerLabel.toArgb())
        assertEquals(legacyPink, style.exitBackground.toArgb())
    }

    @Test fun defaultDrawerKeepsCaixaBlueAccentAndExit() {
        val style = homeDrawerStyle(octoberRose = false)

        assertEquals(0xFF005CA9u.toInt(), style.accent.toArgb())
        assertEquals(0xFF005CA9u.toInt(), style.exitBackground.toArgb())
    }
}
