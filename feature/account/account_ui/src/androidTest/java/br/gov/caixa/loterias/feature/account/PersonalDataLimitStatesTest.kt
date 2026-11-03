package br.gov.caixa.loterias.feature.account

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.createFeatureComposeRuleV2
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.account.presentation.screen.LegacyPersonalDailyLimit
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Apenas limites sintéticos; exercita digitação, foco e exibição sem dados de conta nem gravações na rede. */
class PersonalDataLimitStatesTest {
    @get:Rule val compose = createFeatureComposeRuleV2()

    @Test fun focusedDecimalInputSwitchesBetweenHelperAndInstalledMinimumMaximumMessages() {
        val state = mutableStateOf(
            PersonalDataUiState(
                loading = false,
                dailyLimit = "100,00",
                minimumDailyLimit = BigDecimal("10"),
                authorizedDailyLimit = BigDecimal("200")
            )
        )
        val changes = mutableListOf<String>()
        compose.setFeatureContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                LegacyPersonalDailyLimit(state.value) { value ->
                    changes.add(value)
                    state.value = state.value.copy(dailyLimit = value)
                }
            }
        }
        compose.onNodeWithText("R$ 10,00 até R$ 200,00").assertIsDisplayed()
        compose.onNode(hasSetTextAction()).performClick().assertIsFocused().performTextReplacement("9,99")
        compose.onNodeWithText("O valor mínimo é R$ 10,00").assertIsDisplayed()
        compose.onNodeWithText("R$ 10,00 até R$ 200,00").assertDoesNotExist()
        compose.onNode(hasSetTextAction()).performTextReplacement("200,01")
        compose.onNodeWithText("O valor máximo é R$ 200,00").assertIsDisplayed()
        compose.onNode(hasSetTextAction()).performTextReplacement("150,00")
        compose.onNodeWithText("R$ 10,00 até R$ 200,00").assertIsDisplayed()
        compose.onNodeWithText("O valor máximo é R$ 200,00").assertDoesNotExist()
        compose.runOnIdle { assertEquals("R$ 150,00", changes.last()) }
    }
}
