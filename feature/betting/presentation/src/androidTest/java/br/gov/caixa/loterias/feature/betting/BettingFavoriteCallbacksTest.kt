package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextReplacement
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingFavoriteDialog
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BettingFavoriteCallbacksTest {
    @get:Rule val compose = createComposeRule()

    @Test fun editingImeAndActionsUseReplacedCallbacks() {
        val revision = mutableStateOf(0)
        val name = mutableStateOf("")
        val events = mutableListOf<String>()
        compose.setContent {
            val current = revision.value
            CaixaDSTheme {
                BettingFavoriteDialog(name.value,
                    onNameChange = { name.value = it; events.add("change-$current-$it") },
                    onConfirm = { events.add("confirm-$current") },
                    onCancel = { events.add("cancel-$current") })
            }
        }
        compose.runOnIdle { revision.value = 1 }
        compose.onNodeWithTag("favorite-name-input").performTextReplacement("Minha aposta")
        compose.onNodeWithTag("favorite-name-input").performImeAction()
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertEquals(listOf("change-1-Minha aposta", "confirm-1", "confirm-1", "cancel-1"), events)
        }
    }
}
