package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.designsystem.preview.CaixaDSCatalog
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSCatalogJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun catalogPresentsAccessibleExamplesAndFormattedNumbersInBothThemes() {
        val dark = mutableStateOf(false)
        compose.setContent {
            CaixaDSTheme(darkTheme = dark.value) { CaixaDSCatalog(Modifier.verticalScroll(rememberScrollState())) }
        }
        listOf(false, true).forEach { darkTheme ->
            compose.runOnIdle { dark.value = darkTheme }
            compose.onNodeWithText("CaixaDS").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Texto de apoio e conteúdo.").performScrollTo().assertIsDisplayed()
            compose.onNodeWithContentDescription("Ilustração de notificação").performScrollTo().assertIsDisplayed()
            compose.onNodeWithContentDescription("Leitura de mensagem").performScrollTo().assertIsDisplayed()
            compose.onNodeWithContentDescription("Exclusão de mensagem").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Ação principal").performScrollTo().assertIsEnabled()
            compose.onNodeWithText("Ação secundária").performScrollTo().assertIsEnabled()
            compose.onNodeWithText("Campo").performScrollTo().assertIsDisplayed()
            listOf("03", "12", "27", "41", "56").forEach { compose.onNodeWithText(it).performScrollTo().assertIsDisplayed() }
        }
    }
}
