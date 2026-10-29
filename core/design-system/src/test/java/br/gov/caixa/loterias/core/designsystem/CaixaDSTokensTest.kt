package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.graphics.Color
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSDarkColors
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLightColors
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSMotion
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSSizes
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSSpacing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CaixaDSTokensTest {
    @Test
    fun `spacing scale is strictly increasing`() {
        val spacing = CaixaDSSpacing()
        val scale = listOf(spacing.none, spacing.xxs, spacing.xs, spacing.sm, spacing.md, spacing.lg, spacing.xl, spacing.xxl)

        assertTrue(scale.zipWithNext().all { (current, next) -> current < next })
    }

    @Test
    fun `touch target respects accessibility minimum`() {
        assertTrue(CaixaDSSizes().touchTarget.value >= 48f)
    }

    @Test
    fun `light semantic palette maps to brand primitives`() {
        assertEquals(CaixaDSColorTokens.Blue700, CaixaDSLightColors.brand)
        assertEquals(CaixaDSColorTokens.Orange500, CaixaDSLightColors.accent)
        assertEquals(CaixaDSColorTokens.Orange600, CaixaDSLightColors.actionHighlight)
        assertEquals(CaixaDSColorTokens.Blue700, CaixaDSLightColors.authenticationBackground)
        assertEquals(CaixaDSColorTokens.BlueOverlay90, CaixaDSLightColors.loadingOverlay)
        assertEquals(CaixaDSColorTokens.Error700, CaixaDSLightColors.error)
    }

    @Test
    fun `legacy material card keeps the auto assessment card color`() {
        assertEquals(Color(0xFFECECEC), CaixaDSColorTokens.MaterialCard)
    }

    @Test
    fun `tutorial primitives preserve the legacy palette`() {
        assertEquals(Color(0xFF008254), CaixaDSColorTokens.TutorialGreen)
        assertEquals(Color(0xFF00784E), CaixaDSColorTokens.TutorialGreenButton)
        assertEquals(Color(0xFF47586C), CaixaDSColorTokens.TutorialGray)
        assertEquals(Color(0xFF007BFF), CaixaDSColorTokens.TutorialCheckbox)
        assertEquals(Color(0xFFCED4DA), CaixaDSColorTokens.TutorialCheckboxOutline)
        assertEquals(Color(0xFFEFF5F6), CaixaDSColorTokens.TicketDisabledBackground)
        assertEquals(Color(0xFFD7E0E2), CaixaDSColorTokens.TicketDisabledContent)
        assertEquals(Color(0xFF4E596B), CaixaDSColorTokens.NotificationText)
    }

    @Test
    fun `dark palette preserves readable semantic contrast pairs`() {
        assertTrue(CaixaDSDarkColors.background != CaixaDSDarkColors.textPrimary)
        assertTrue(CaixaDSDarkColors.brand != CaixaDSDarkColors.onBrand)
        assertTrue(CaixaDSDarkColors.error != CaixaDSDarkColors.onError)
        assertEquals(CaixaDSLightColors.authenticationBackground, CaixaDSDarkColors.authenticationBackground)
        assertEquals(CaixaDSLightColors.loadingOverlay, CaixaDSDarkColors.loadingOverlay)
    }

    @Test
    fun `motion durations progress from fast to emphasized`() {
        assertTrue(CaixaDSMotion.Fast < CaixaDSMotion.Standard)
        assertTrue(CaixaDSMotion.Standard < CaixaDSMotion.Emphasized)
    }

    @Test
    fun `legacy back icon preserves original intrinsic size`() {
        assertEquals(12f, CaixaDSIcons.Back.defaultWidth.value)
        assertEquals(18f, CaixaDSIcons.Back.defaultHeight.value)
        assertEquals(12f, CaixaDSIcons.Back.viewportWidth)
        assertEquals(18f, CaixaDSIcons.Back.viewportHeight)
    }
}
