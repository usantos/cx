package br.gov.caixa.loterias.sample.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CatalogNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun searchOpensDetailsAndBackRetainsQuery() {
        compose.setContent { DesignSystemCatalog() }
        compose.onNodeWithTag("catalog_search").performTextInput("CaixaDSNumberBall")
        compose.onNodeWithTag("br.gov.caixa.loterias.core.designsystem.components.CaixaDSNumberBall")
            .performScrollTo().performClick()
        compose.onNodeWithText("Exemplo interativo").assertIsDisplayed()
        compose.onNodeWithText("07").assertIsDisplayed()
        compose.onNodeWithText("Como usar").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("catalog_detail").performScrollToNode(hasText("Parâmetros e sobrecargas"))
        compose.onNodeWithText("Parâmetros e sobrecargas").assertIsDisplayed()
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithTag("catalog_search").assertTextContains("CaixaDSNumberBall")
        compose.onNodeWithTag("catalog_search").performTextClearance()
        compose.onNodeWithTag("catalog_search").performTextInput("unfindablecomponent")
        compose.onNodeWithText("Nenhum componente encontrado.").assertIsDisplayed()
    }

    @Test
    fun actionPreviewReportsInteraction() {
        compose.setContent { DesignSystemCatalog() }
        compose.onNodeWithTag("catalog_search").performTextInput("CaixaDSPrimaryButton")
        compose.onNodeWithTag("br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSPrimaryButton")
            .performScrollTo().performClick()
        compose.onNodeWithText("Experimentar ação").performClick()
        compose.onNodeWithText("Interações: 1").assertIsDisplayed()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithTag("catalog_search").assertTextContains("CaixaDSPrimaryButton")
    }
}
