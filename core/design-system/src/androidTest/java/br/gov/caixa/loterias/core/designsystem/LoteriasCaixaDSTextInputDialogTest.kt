package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSTextInputDialog
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoteriasCaixaDSTextInputDialogTest {
    @get:Rule val compose = createComposeRule()

    @Test fun nameDialogEditsAndDispatchesSaveAndCancel() {
        val value = mutableStateOf("")
        var saved: String? = null
        var cancelled = false
        compose.setContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                LoteriasCaixaDSTextInputDialog(
                    "Carrinho Favorito",
                    value.value,
                    "Nome do carrinho favorito",
                    { value.value = it },
                    { saved = value.value },
                    { cancelled = true }
                )
            }
        }
        compose.onNodeWithText("Carrinho Favorito").assertIsDisplayed()
        compose.onNodeWithContentDescription("Nome do carrinho favorito").performTextInput("Meu carrinho")
        compose.onNodeWithText("Salvar").assertHeightIsEqualTo(55.dp).performClick()
        compose.runOnIdle { assertEquals("Meu carrinho", saved) }
        compose.onNodeWithText("Cancelar").assertHeightIsEqualTo(55.dp).performClick()
        compose.runOnIdle { assertTrue(cancelled) }
    }

    @Test fun loadingDisablesEditingSaveAndCancellation() {
        var calls = 0
        compose.setContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                LoteriasCaixaDSTextInputDialog(
                    "Carrinho Favorito",
                    "Meu carrinho",
                    "Nome do carrinho favorito",
                    { calls++ },
                    { calls++ },
                    { calls++ },
                    enabled = false
                )
            }
        }
        compose.onNodeWithContentDescription("Nome do carrinho favorito").assertIsNotEnabled()
        compose.onNodeWithText("Salvar").assertIsNotEnabled().performClick()
        compose.onNodeWithText("Cancelar").assertIsNotEnabled().performClick()
        compose.runOnIdle { assertEquals(0, calls) }
    }
}
