package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Exercita um tipo fora da faixa inicial de filtros na Activity real. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CatalogReturnNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<CatalogActivity>()

    @Test
    fun toolbarBackRestoresPreviousTypeAndItsPosition() {
        openDialogDetail()
        compose.onNodeWithContentDescription("Voltar").performClick()
        assertPreviousScreen()
    }

    @Test
    fun systemBackRestoresPreviousTypeAndItsPositionAfterRecreation() {
        openDialogDetail()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("catalog_detail").assertIsDisplayed()
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        assertPreviousScreen()
    }

    private fun openDialogDetail() {
        val index = CatalogType.entries.indexOf(CatalogType.DIALOGS) + 1
        compose.onAllNodes(hasScrollToIndexAction())[0].performScrollToIndex(index)
        compose.onNodeWithTag("simlo.catalog.type.dialogs").performClick()
        compose.onNodeWithTag("catalog_search").performTextInput("Dialog")
        compose.onNodeWithTag("catalog_list").performScrollToNode(hasTestTag(DIALOG_ENTRY))
        compose.onNodeWithTag(DIALOG_ENTRY).performClick()
        compose.onNodeWithTag("catalog_detail").assertIsDisplayed()
    }

    private fun assertPreviousScreen() {
        compose.onNodeWithTag("simlo.catalog.type.dialogs").assertIsDisplayed().assertIsSelected()
        compose.onNodeWithTag("catalog_search").assertTextContains("Dialog")
        compose.onNodeWithTag(DIALOG_ENTRY).assertIsDisplayed()
    }
}

private const val DIALOG_ENTRY =
    "br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSSelectionDialog"
