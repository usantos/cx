package br.gov.caixa.loterias.feature.support.faq

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.support.faq.screen.FaqAnswer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.ResourcesMode

@RunWith(DynamicFeatureRobolectricTestRunner::class)
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@Config(sdk = [36], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class SupportAccessibilityTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun faqLinksHaveSemanticAnnotationsAndInvokeOwnedConfirmation() {
        compose.setContent { LoteriasCaixaDSTheme { FaqAnswer("Consulte https://caixa.gov.br e https://loterias.caixa.gov.br", Modifier) } }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNode(hasText("Consulte", substring = true)).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        compose.runOnIdle {
            val text = layouts.single().layoutInput.text
            assertEquals(2, text.getLinkAnnotations(0, text.length).size)
        }
    }
}
