package br.gov.caixa.loterias.feature.account

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.text.TextRange
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Installed DadosPessoaisActivity$4 formats each edit as cents; no real profile or writes. */
class PersonalDailyLimitInputParityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun unpunctuatedDigitsAreCentsBeforeValidationAndCallback() {
        val state = mutableStateOf(PersonalDataUiState(loading = false, dailyLimit = "R$ 100,00",
            minimumDailyLimit = BigDecimal("10"), authorizedDailyLimit = BigDecimal("200")))
        val changes = mutableListOf<String>()
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            LegacyPersonalDailyLimit(state.value) { value ->
                changes += value
                state.value = state.value.copy(dailyLimit = value)
            }
        } }
        compose.onNode(hasSetTextAction()).performClick().performTextReplacement("1234")
        compose.onNode(hasSetTextAction()).assertTextEquals("R$ 12,34")
        compose.onNode(hasSetTextAction()).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange("R$ 12,34".length)),
        )
        compose.onNodeWithText("R$ 10,00 até R$ 200,00").assertIsDisplayed()
        compose.onNodeWithText("O valor máximo é R$ 200,00").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(listOf("R$ 12,34"), changes)
            assertEquals(BigDecimal("12.34"), parseBrazilianCurrency(state.value.dailyLimit))
        }
    }

    @Test fun appendingAndRemovingLastDigitUsesTheInstalledCurrencyMask() {
        val state = mutableStateOf(PersonalDataUiState(loading = false, dailyLimit = "R$ 100,00",
            minimumDailyLimit = BigDecimal("10"), authorizedDailyLimit = BigDecimal("2000")))
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            LegacyPersonalDailyLimit(state.value) { value -> state.value = state.value.copy(dailyLimit = value) }
        } }
        compose.onNode(hasSetTextAction()).performClick().performTextReplacement("10000")
        compose.onNode(hasSetTextAction()).performTextInput("5")
        compose.onNode(hasSetTextAction()).assertTextEquals("R$ 1.000,05")
        compose.onNode(hasSetTextAction()).performTextReplacement("R$ 1.000,0")
        compose.onNode(hasSetTextAction()).assertTextEquals("R$ 100,00")
        compose.runOnIdle { assertEquals(BigDecimal("100.00"), parseBrazilianCurrency(state.value.dailyLimit)) }
    }

    @Test fun selectingCurrencyTextPreservesSelectionWithoutSendingAnEdit() {
        val state = mutableStateOf(PersonalDataUiState(loading = false, dailyLimit = "R$ 100,00",
            minimumDailyLimit = BigDecimal("10"), authorizedDailyLimit = BigDecimal("200")))
        val changes = mutableListOf<String>()
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            LegacyPersonalDailyLimit(state.value) { value ->
                changes += value
                state.value = state.value.copy(dailyLimit = value)
            }
        } }
        compose.onNode(hasSetTextAction()).performClick()
            .performTextInputSelection(TextRange(0, "R$ 100,00".length))
        compose.onNode(hasSetTextAction()).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(0, "R$ 100,00".length)),
        )
        compose.runOnIdle { assertEquals(emptyList<String>(), changes) }
    }
}
