package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

/** Controlled Compose dialogs with fake callbacks: never submits favorites/cart/network requests. */
class BettingComposeOptionsTest {
    @get:Rule val compose = createComposeRule()

    @Test fun quantitySelectionStaysPendingUntilConfirmAndCancelDoesNotCommit() {
        val visible = mutableStateOf(true)
        var commits = 0
        compose.setContent {
            CaixaDSTheme {
                if (visible.value) BettingOptionDialog(
                    "Quantidade de números", listOf(6, 7, 8), 6, { "$it números" },
                    { commits++ }, { visible.value = false },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("betting-option-list").assertExists()
        compose.onNodeWithTag("betting-option-confirm").assertIsNotEnabled()
        compose.onNodeWithTag("betting-option-confirm").assertNativeDisabledConfirmPixels()
        compose.onNodeWithTag("betting-option-item-1").assertNativeUnchosenRowPixels()
        (0..2).forEach { compose.onNodeWithTag("betting-option-item-$it").assertIsNotSelected() }
        val picker = compose.onNodeWithTag("betting-option-list").fetchSemanticsNode().boundsInRoot
        assertEquals(with(compose.density) { 170.dp.toPx() }, picker.height, 1f)
        compose.onNodeWithTag("betting-option-item-1").performClick().assertIsSelected()
        assertEquals(0, commits)
        compose.onNodeWithTag("betting-option-cancel").performClick()
        compose.waitForIdle()
        assertFalse(visible.value)
        assertEquals(0, commits)
    }

    @Test fun recurrenceWheelStartsUnselectedAndCommitsTappedThirdOptionExactlyOnce() {
        val visible = mutableStateOf(true)
        var chosen = -1
        var commits = 0
        compose.setContent {
            CaixaDSTheme {
                if (visible.value) BettingOptionDialog(
                    "TEIMOSINHA", listOf(0, 2, 3), 3, { "$it concursos" },
                    { chosen = it; commits++ }, { visible.value = false },
                    "Escolha em quantos concursos você quer participar com este mesmo jogo:",
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("betting-option-confirm").assertIsNotEnabled()
        (0..2).forEach { compose.onNodeWithTag("betting-option-item-$it").assertIsNotSelected().assertIsDisplayed() }
        val picker = compose.onNodeWithTag("betting-option-wheel").fetchSemanticsNode().boundsInRoot
        val third = compose.onNodeWithTag("betting-option-item-2").fetchSemanticsNode().boundsInRoot
        org.junit.Assert.assertTrue("Third native row must be entirely visible", third.bottom <= picker.bottom)
        compose.onNodeWithTag("betting-option-item-2").performTouchInput { click() }
        compose.onNodeWithTag("betting-option-confirm").assertIsEnabled()
        compose.onNodeWithTag("betting-option-wheel").assertExists()
        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.waitForIdle()
        assertEquals(3, chosen)
        assertEquals(1, commits)
        assertFalse(visible.value)
    }
    @Test fun initialPickerShowsThreeEntireRowsWithoutCenterPadding() {
        compose.setContent { CaixaDSTheme { BettingOptionDialog("TEIMOSINHA", listOf(0, 2, 3), 3,
            { "$it concursos" }, {}, {}, "Escolha em quantos concursos você quer participar com este mesmo jogo:") } }
        compose.waitForIdle()
        val picker = compose.onNodeWithTag("betting-option-wheel").fetchSemanticsNode().boundsInRoot
        val first = compose.onNodeWithTag("betting-option-item-0").fetchSemanticsNode().boundsInRoot
        val third = compose.onNodeWithTag("betting-option-item-2").fetchSemanticsNode().boundsInRoot
        assertEquals(picker.top, first.top, 1f)
        assertEquals("Third row retains its entire native 52dp height", with(compose.density) { 52.dp.toPx() }, third.height, 1f)
        assertEquals("Third row starts after two complete native rows", with(compose.density) { 104.dp.toPx() }, third.top - first.top, 1f)
        org.junit.Assert.assertTrue("Third row must be unclipped", third.bottom <= picker.bottom)
    }

    @Test fun titleAndActionHeightMatchNativeXmlTextViewAndButton() {
        compose.setContent { CaixaDSTheme { BettingOptionDialog("TEIMOSINHA", listOf(0, 2, 3), 3,
            { "$it concursos" }, {}, {}, "Escolha em quantos concursos você quer participar com este mesmo jogo:") } }
        compose.waitForIdle()
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val themed = android.view.ContextThemeWrapper(context, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
        val title = android.widget.TextView(themed).apply {
            text = "TEIMOSINHA"; textSize = 16f
            typeface = androidx.core.content.res.ResourcesCompat.getFont(context, br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_bold)
            measure(0, 0)
        }
        val button = android.widget.Button(themed, null, android.R.attr.buttonBarButtonStyle).apply {
            text = "Confirmar"; textSize = 16f; isAllCaps = false
            typeface = androidx.core.content.res.ResourcesCompat.getFont(context, br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold)
            measure(0, 0)
        }
        assertEquals("XML explicit bold title height", title.measuredHeight.toFloat(), compose.onNodeWithText("TEIMOSINHA").fetchSemanticsNode().boundsInRoot.height, 1f)
        assertEquals("Native buttonBarButtonStyle height", button.measuredHeight.toFloat(), compose.onNodeWithTag("betting-option-confirm").fetchSemanticsNode().boundsInRoot.height, 1f)
    }

    @Test fun dragSelectsOnlyAfterUserInteractionAndConfirmCommitsSnappedOption() {
        var chosen: Int? = null
        var dismissed = 0
        compose.setContent { CaixaDSTheme { BettingOptionDialog("Quantidade", (6..20).toList(), 20,
            { "$it números" }, { chosen = it }, { dismissed++ }) } }
        compose.onNodeWithTag("betting-option-confirm").assertIsNotEnabled().performTouchInput { click() }
        compose.runOnIdle { assertEquals(null, chosen); assertEquals(0, dismissed) }
        compose.onNodeWithTag("betting-option-list").performTouchInput { swipeUp() }
        compose.waitForIdle()
        compose.onNodeWithTag("betting-option-confirm").assertIsEnabled()
        val selected = compose.onAllNodes(isSelected()).fetchSemanticsNodes().single()
        val index = selected.config[androidx.compose.ui.semantics.SemanticsProperties.TestTag].substringAfterLast('-').toInt()
        val picker = compose.onNodeWithTag("betting-option-list").fetchSemanticsNode().boundsInRoot
        assertEquals("Dragged interior option snaps to viewport center", picker.center.y, selected.boundsInRoot.center.y, 2f)
        compose.onNodeWithTag("betting-option-confirm").performTouchInput { click() }
        compose.runOnIdle { assertEquals(6 + index, chosen); assertEquals(1, dismissed) }
    }

    @Test fun descriptionUsesNativeRegularTextViewLineMetrics() {
        val description = "Escolha em quantos concursos você quer participar com este mesmo jogo:"
        compose.setContent { CaixaDSTheme { BettingOptionDialog("TEIMOSINHA", listOf(0, 2, 3), 3,
            { "$it concursos" }, {}, {}, description) } }
        compose.waitForIdle()
        val actual = compose.onNodeWithText(description).fetchSemanticsNode().boundsInRoot
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val reference = android.widget.TextView(context).apply {
            text = description; textSize = 16f
            typeface = androidx.core.content.res.ResourcesCompat.getFont(context, br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular)
            val padding = with(compose.density) { 16.dp.roundToPx() }
            setPadding(0, padding, 0, padding)
            measure(android.view.View.MeasureSpec.makeMeasureSpec(actual.width.toInt(), android.view.View.MeasureSpec.EXACTLY), 0)
        }
        assertEquals("Native body wrap and includeFontPadding", (reference.measuredHeight - reference.paddingTop - reference.paddingBottom).toFloat(), actual.height, 1f)
    }

}
