package br.gov.caixa.loterias.core.designsystem

import androidx.compose.ui.graphics.Color
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSDarkColors
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLightColors
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSMotion
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSSizes
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSSpacing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoteriasCaixaDSTokensTest {
    @Test
    fun `spacing scale is strictly increasing`() {
        val spacing = LoteriasCaixaDSSpacing()
        val scale = listOf(spacing.none, spacing.xxs, spacing.xs, spacing.sm, spacing.md, spacing.lg, spacing.xl, spacing.xxl)

        assertTrue(scale.zipWithNext().all { (current, next) -> current < next })
    }

    @Test
    fun `touch target respects accessibility minimum`() {
        assertTrue(LoteriasCaixaDSSizes().touchTarget.value >= TokensTestExpectations.MINIMUM_TOUCH_TARGET_DP)
    }

    @Test
    fun `light semantic palette maps to brand primitives`() {
        assertEquals(LoteriasCaixaDSColorTokens.Blue700, LoteriasCaixaDSLightColors.brand)
        assertEquals(LoteriasCaixaDSColorTokens.Orange500, LoteriasCaixaDSLightColors.accent)
        assertEquals(LoteriasCaixaDSColorTokens.Orange600, LoteriasCaixaDSLightColors.actionHighlight)
        assertEquals(LoteriasCaixaDSColorTokens.Blue700, LoteriasCaixaDSLightColors.authenticationBackground)
        assertEquals(LoteriasCaixaDSColorTokens.BlueOverlay90, LoteriasCaixaDSLightColors.loadingOverlay)
        assertEquals(LoteriasCaixaDSColorTokens.Error700, LoteriasCaixaDSLightColors.error)
    }

    @Test
    fun `legacy material card keeps the auto assessment card color`() {
        assertEquals(Color(TokensTestExpectations.EXPECTED_MATERIAL_CARD_ARGB), LoteriasCaixaDSColorTokens.MaterialCard)
    }

    @Test
    fun `tutorial primitives preserve the legacy palette`() {
        assertEquals(Color(TokensTestExpectations.EXPECTED_TUTORIAL_GREEN_ARGB), LoteriasCaixaDSColorTokens.TutorialGreen)
        assertEquals(Color(TokensTestExpectations.EXPECTED_TUTORIAL_GREEN_BUTTON_ARGB), LoteriasCaixaDSColorTokens.TutorialGreenButton)
        assertEquals(Color(TokensTestExpectations.EXPECTED_TUTORIAL_GRAY_ARGB), LoteriasCaixaDSColorTokens.TutorialGray)
        assertEquals(Color(TokensTestExpectations.EXPECTED_TUTORIAL_CHECKBOX_ARGB), LoteriasCaixaDSColorTokens.TutorialCheckbox)
        assertEquals(
            Color(TokensTestExpectations.EXPECTED_TUTORIAL_CHECKBOX_OUTLINE_ARGB),
            LoteriasCaixaDSColorTokens.TutorialCheckboxOutline
        )
        assertEquals(
            Color(TokensTestExpectations.EXPECTED_TICKET_DISABLED_BACKGROUND_ARGB),
            LoteriasCaixaDSColorTokens.TicketDisabledBackground
        )
        assertEquals(Color(TokensTestExpectations.EXPECTED_TICKET_DISABLED_CONTENT_ARGB), LoteriasCaixaDSColorTokens.TicketDisabledContent)
        assertEquals(Color(TokensTestExpectations.EXPECTED_NOTIFICATION_TEXT_ARGB), LoteriasCaixaDSColorTokens.NotificationText)
    }

    @Test
    fun `dark palette preserves readable semantic contrast pairs`() {
        assertTrue(LoteriasCaixaDSDarkColors.background != LoteriasCaixaDSDarkColors.textPrimary)
        assertTrue(LoteriasCaixaDSDarkColors.brand != LoteriasCaixaDSDarkColors.onBrand)
        assertTrue(LoteriasCaixaDSDarkColors.error != LoteriasCaixaDSDarkColors.onError)
        assertEquals(LoteriasCaixaDSLightColors.authenticationBackground, LoteriasCaixaDSDarkColors.authenticationBackground)
        assertEquals(LoteriasCaixaDSLightColors.loadingOverlay, LoteriasCaixaDSDarkColors.loadingOverlay)
    }

    @Test
    fun `motion durations progress from fast to emphasized`() {
        assertTrue(LoteriasCaixaDSMotion.FAST < LoteriasCaixaDSMotion.STANDARD)
        assertTrue(LoteriasCaixaDSMotion.STANDARD < LoteriasCaixaDSMotion.EMPHASIZED)
    }

    @Test
    fun `legacy back icon preserves original intrinsic size`() {
        assertEquals(TokensTestExpectations.EXPECTED_BACK_WIDTH, LoteriasCaixaDSIcons.Back.defaultWidth.value)
        assertEquals(TokensTestExpectations.EXPECTED_BACK_HEIGHT, LoteriasCaixaDSIcons.Back.defaultHeight.value)
        assertEquals(TokensTestExpectations.EXPECTED_BACK_WIDTH, LoteriasCaixaDSIcons.Back.viewportWidth)
        assertEquals(TokensTestExpectations.EXPECTED_BACK_HEIGHT, LoteriasCaixaDSIcons.Back.viewportHeight)
    }
}

private object TokensTestExpectations {
    const val MINIMUM_TOUCH_TARGET_DP = 48f
    const val EXPECTED_MATERIAL_CARD_ARGB = 0xFFECECEC
    const val EXPECTED_TUTORIAL_GREEN_ARGB = 0xFF008254
    const val EXPECTED_TUTORIAL_GREEN_BUTTON_ARGB = 0xFF00784E
    const val EXPECTED_TUTORIAL_GRAY_ARGB = 0xFF47586C
    const val EXPECTED_TUTORIAL_CHECKBOX_ARGB = 0xFF007BFF
    const val EXPECTED_TUTORIAL_CHECKBOX_OUTLINE_ARGB = 0xFFCED4DA
    const val EXPECTED_TICKET_DISABLED_BACKGROUND_ARGB = 0xFFEFF5F6
    const val EXPECTED_TICKET_DISABLED_CONTENT_ARGB = 0xFFD7E0E2
    const val EXPECTED_NOTIFICATION_TEXT_ARGB = 0xFF4E596B
    const val EXPECTED_BACK_WIDTH = 12f
    const val EXPECTED_BACK_HEIGHT = 18f
}
