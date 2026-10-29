package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CatalogActivityLaunchTest {
    @get:Rule val compose = createAndroidComposeRule<CatalogActivity>()

    @Test
    fun launcherOpensCatalogAndRestoresSelectedDetailAndListScrollAfterRecreation() {
        // A Activity real controla setContent; este teste não substitui seu conteúdo de produção.
        compose.onNodeWithText("DS Loterias Caixa").assertIsDisplayed()
        compose.onNodeWithTag("catalog_search").assertIsDisplayed()
        val key = "br.gov.caixa.loterias.core.designsystem.web.CaixaDSWebPage"
        compose.onNodeWithTag("catalog_list").performScrollToNode(hasTestTag(key))
        compose.onNodeWithTag(key).assertIsDisplayed().performClick()
        compose.waitUntil { compose.onNodeWithTag("catalog_detail").isDisplayed() }
        compose.onNodeWithTag("catalog_detail").assertIsDisplayed()
        compose.activityRule.scenario.recreate()
        compose.waitUntil { compose.onNodeWithTag("catalog_detail").isDisplayed() }
        compose.onNodeWithTag("catalog_detail").assertIsDisplayed()
        compose.onNodeWithText("CaixaDSWebPage").assertIsDisplayed()
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithTag(key).assertIsDisplayed()
    }
}
