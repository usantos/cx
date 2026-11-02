package br.gov.caixa.loterias.sample.designsystem

import android.content.ClipboardManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CatalogOrganizationTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun typeFilterAndSearchSurviveOpeningAComponentAndReturning() {
        compose.setContent { DesignSystemCatalog() }
        compose.onNodeWithTag("simlo.catalog.type.buttons").performClick()
        compose.onNodeWithTag("catalog_search").performTextInput("PrimaryButton")
        compose.onNodeWithTag(PRIMARY_BUTTON).performScrollTo().performClick()
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithTag("simlo.catalog.type.buttons").assertIsSelected()
        compose.onNodeWithTag("catalog_search").assertTextContains("PrimaryButton")
    }

    @Test
    fun copyActionsExportTheExactDisplayedUsageAndSignature() {
        compose.setContent { DesignSystemCatalog() }
        compose.onNodeWithTag("catalog_search").performTextInput("PrimaryButton")
        compose.onNodeWithTag(PRIMARY_BUTTON).performScrollTo().performClick()
        val entry = CatalogRegistry.entries.single { it.id == PRIMARY_BUTTON }
        compose.onNodeWithTag("simlo.catalog.copy.usage").performScrollTo().performClick()
        assertClipboard(compose.activity.getString(entry.snippet))
        compose.onNodeWithTag("catalog_detail").performScrollToNode(hasTestTag("simlo.catalog.copy.signature"))
        compose.onNodeWithTag("simlo.catalog.copy.signature").performClick()
        assertClipboard(compose.activity.getString(entry.signatures))
    }

    private fun assertClipboard(expected: String) {
        compose.runOnIdle {
            val clipboard = compose.activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            assertEquals(expected, clipboard.primaryClip?.getItemAt(0)?.text?.toString())
        }
    }
}

private const val PRIMARY_BUTTON =
    "br.gov.caixa.loterias.core.designsystem.components.button.LoteriasCaixaDSPrimaryButton"
