package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroductionUseCase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import br.gov.caixa.loterias.feature.onboarding.presentation.route.OnboardingRoute
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.OnboardingViewModel

@RunWith(AndroidJUnit4::class)
class TutorialTypographyParityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun introductionUsesThePackagedFuturaFacesAndLegacySizes() {
        val viewModel = OnboardingViewModel("onboarding/tutorial", CompleteIntroductionUseCase {})
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                OnboardingRoute(viewModel, {}, {}, {}, {}, {}, {})
            }
        }

        assertStyle("Chegou a +milionária", CaixaDSFuturaFontFamily, 28f, bold = true)
        assertStyle("Conheça o novo jogo +milionária.\nVamos explicar como funciona\nnessa nova modalidade.", CaixaDSFuturaFontFamily, 18f, bold = false)
        assertStyle("Começar", CaixaDSFuturaFontFamily, 16f, bold = true)
        assertStyle("tutorial", CaixaDSFuturaFontFamily, 16f, bold = false)
        assertStyle("Pular", CaixaDSFuturaFontFamily, 16f, bold = true)
        assertStyle("apresentação", CaixaDSFuturaFontFamily, 16f, bold = false)
    }

    @Test fun welcomePagesUseLegacyTypographyForEachTitleAndBody() {
        val viewModel = OnboardingViewModel("onboarding/introduction", CompleteIntroductionUseCase {})
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                OnboardingRoute(viewModel, {}, {}, {}, {}, {}, {})
            }
        }
        compose.onNodeWithText("Sim").performClick()

        assertStyle("Uma nova experiência", CaixaDSFontFamily, 20f, bold = true)
        assertStyle("Todas as apostas na palma da sua mão, o dia inteiro, todos os dias. ;)", CaixaDSRegularFontFamily, 20f, bold = false)
        compose.onNodeWithText("Todas as apostas na palma da sua mão, o dia inteiro, todos os dias. ;)", useUnmergedTree = true)
            .performTouchInput { swipeLeft() }
        assertStyle("Novas funcionalidades", CaixaDSFuturaFontFamily, 18f, bold = true)
        assertStyle("Suas apostas ainda mais fáceis e muito mais rápidas.", CaixaDSRegularFontFamily, 20f, bold = false)
        compose.onNodeWithText("Suas apostas ainda mais fáceis e muito mais rápidas.", useUnmergedTree = true)
            .performTouchInput { swipeLeft() }
        assertStyle("Mais praticidade", CaixaDSFuturaFontFamily, 18f, bold = true)
        assertStyle("Acompanhe e gerencie todas as suas apostas.", CaixaDSRegularFontFamily, 20f, bold = false)
        compose.onNodeWithText("Acessar o aplicativo").assertExists()
    }

    private fun assertStyle(
        text: String,
        family: androidx.compose.ui.text.font.FontFamily,
        sizeSp: Float,
        bold: Boolean,
    ) {
        val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        compose.onNodeWithText(text, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals("One text layout for '$text'", 1, layouts.size)
        val style = layouts.single().layoutInput.style
        assertEquals("Font family for '$text'", family, style.fontFamily)
        assertEquals("Legacy size for '$text'", sizeSp, style.fontSize.value)
        assertEquals("Legacy weight for '$text'", bold, style.fontWeight?.weight == 700)
    }
}
