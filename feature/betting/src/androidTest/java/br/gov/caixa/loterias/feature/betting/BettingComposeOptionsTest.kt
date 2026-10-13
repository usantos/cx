package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
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
        compose.onNodeWithTag("betting-option-item-1").performClick().assertIsSelected()
        assertEquals(0, commits)
        compose.onNodeWithTag("betting-option-cancel").performClick()
        compose.waitForIdle()
        assertFalse(visible.value)
        assertEquals(0, commits)
    }

    @Test fun recurrenceWheelStartsAtZeroAndCommitsExactlyOnce() {
        val visible = mutableStateOf(true)
        var chosen = -1
        var commits = 0
        compose.setContent {
            CaixaDSTheme {
                if (visible.value) BettingOptionDialog(
                    "TEIMOSINHA", listOf(0, 2, 4), 4, { "$it concursos" },
                    { chosen = it; commits++ }, { visible.value = false },
                    "Escolha em quantos concursos você quer participar com este mesmo jogo:",
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("betting-option-item-0").assertIsSelected()
        compose.onNodeWithTag("betting-option-wheel").assertExists()
        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.waitForIdle()
        assertEquals(0, chosen)
        assertEquals(1, commits)
        assertFalse(visible.value)
    }
}
