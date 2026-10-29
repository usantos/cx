package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSOctoberRoseCampaignPanel
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextFieldVariant
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSHtmlDialog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSHomeV2Theme
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val BODY_TEXT_SP = 16f
private const val LONG_MESSAGE_REPETITIONS = 100
private const val CONTRAST_LUMINANCE_OFFSET = .05f
private const val NORMAL_TEXT_MINIMUM_CONTRAST = 4.5f

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSLargeTextAccessibilityJvmTest {
    @get:Rule val compose = createComposeRule()

    @Test fun homeThemePreservesSystemFontScaleTwo() {
        var receivedScale = 0f
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                CaixaDSHomeV2Theme {
                    val scale = LocalDensity.current.fontScale
                    SideEffect { receivedScale = scale }
                }
            }
        }
        compose.runOnIdle { assertEquals(2f, receivedScale, 0f) }
    }

    @Test fun campaignParagraphKeepsScalableFontSizeAtScaleTwo() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                CaixaDSTheme { Box(Modifier.width(320.dp)) { CaixaDSOctoberRoseCampaignPanel() } }
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("Campanha anual", substring = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(BODY_TEXT_SP, layouts.single().layoutInput.style.fontSize.value, 0f)
    }

    @Test fun labelledFieldTextHasNoVerticalOverflowAtScaleTwo() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                CaixaDSTheme {
                    CaixaDSTextField("Documento preenchido", {}, "Documento", variant = CaixaDSTextFieldVariant.LABELLED)
                }
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNode(hasSetTextAction())
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertTrue("Editable text needs room for its complete line", layouts.none { it.didOverflowHeight })
    }

    @Test fun longDialogBodyOffersScrollingInsteadOfLosingActions() {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSDialog("Aviso", "Instrução acessível. ".repeat(LONG_MESSAGE_REPETITIONS), "Confirmar", {
                }, negativeLabel = "Cancelar")
            }
        }
        compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.ScrollBy)).assertCountEquals(1)
    }

    @Test fun longHtmlDialogBodyOffersScrollingInsteadOfLosingActions() {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSHtmlDialog("Ajuda", "<p>Instrução acessível.</p>".repeat(LONG_MESSAGE_REPETITIONS), {})
            }
        }
        compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.ScrollBy)).assertCountEquals(1)
    }

    @Test fun legacyHelperHasNormalTextContrastOnWhite() {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                CaixaDSTextField(
                    "123",
                    {},
                    "Documento",
                    variant = CaixaDSTextFieldVariant.LEGACY,
                    supportingText = "Somente números"
                )
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("Somente números")
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val foreground = layouts.single().layoutInput.style.color.compositeOver(Color.White)
        val ratio = (Color.White.luminance() + CONTRAST_LUMINANCE_OFFSET) / (foreground.luminance() + CONTRAST_LUMINANCE_OFFSET)
        assertTrue("Normal helper text contrast was $ratio:1", ratio >= NORMAL_TEXT_MINIMUM_CONTRAST)
    }
}
