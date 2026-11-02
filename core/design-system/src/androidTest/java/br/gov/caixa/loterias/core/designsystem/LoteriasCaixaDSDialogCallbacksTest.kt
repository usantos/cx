package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSHtmlDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSOptionDialog
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoteriasCaixaDSDialogCallbacksTest {
    @get:Rule val compose = createComposeRule()

    @Test fun htmlDialogUsesReplacedPositiveAndNegativeCallbacks() {
        val revision = mutableStateOf(0)
        val events = mutableListOf<String>()
        compose.setContent {
            val current = revision.value
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSHtmlDialog(
                    "Aviso",
                    "<b>Mensagem</b>",
                    onPositive = { events.add("positive-$current") },
                    positiveLabel = "Confirmar",
                    negativeLabel = "Cancelar",
                    onNegative = { events.add("negative-$current") }
                )
            }
        }
        compose.runOnIdle { revision.value = 1 }
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle { assertEquals(listOf("negative-1", "positive-1"), events) }
    }

    @Test fun optionDialogKeepsPendingChoiceAndUsesReplacedCallbacksInOrder() {
        val revision = mutableStateOf(0)
        val events = mutableListOf<String>()
        compose.setContent {
            val current = revision.value
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSOptionDialog(
                    "Quantidade",
                    listOf(
                        DialogCallbacksTestExpectations.INITIAL_NUMBER_COUNT,
                        DialogCallbacksTestExpectations.NEXT_NUMBER_COUNT,
                        DialogCallbacksTestExpectations.MAXIMUM_NUMBER_COUNT
                    ),
                    DialogCallbacksTestExpectations.INITIAL_NUMBER_COUNT,
                    {
                        "$it números"
                    },
                    onSelect = { events.add("select-$current-$it") },
                    onDismiss = { events.add("dismiss-$current") }
                )
            }
        }
        compose.onNodeWithTag("betting-option-item-1").performClick()
        compose.runOnIdle { revision.value = 1 }
        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.onNodeWithTag("betting-option-cancel").performClick()
        compose.runOnIdle { assertEquals(listOf("select-1-7", "dismiss-1", "dismiss-1"), events) }
    }
}

private object DialogCallbacksTestExpectations {
    const val INITIAL_NUMBER_COUNT = 6
    const val NEXT_NUMBER_COUNT = 7
    const val MAXIMUM_NUMBER_COUNT = 8
}
