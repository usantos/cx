// Literais numéricos são valores sintéticos de perfil e dimensões de content_dados_pessoais.xml.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.account

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.presentation.screen.PersonalDataActions
import br.gov.caixa.loterias.feature.account.presentation.screen.PersonalDataScreen
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Os contratos da tela permanecem iguais quando seus campos mudam entre implementações do sistema de design. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h1600dp")
class PersonalDataFieldContractUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `identity and resolved address stay disabled while CEP and currency emit separate callbacks`() {
        val state = mutableStateOf(profile())
        val zipChanges = mutableListOf<String>()
        val limitChanges = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                PersonalDataScreen(
                    state.value,
                    false,
                    {},
                    actions().copy(
                        changeZipCode = {
                            zipChanges += it
                            state.value = state.value.copy(zipCode = it.filter(Char::isDigit))
                        },
                        changeDailyLimit = {
                            limitChanges += it
                            state.value = state.value.copy(dailyLimit = it)
                        }
                    )
                )
            }
        }
        listOf("123.456.789-01", "Synthetic Person", "DF", "Brasília", "fixture@example.test").forEach {
            compose.onNodeWithText(it).assertIsNotEnabled()
        }
        compose.onAllNodes(hasSetTextAction()).assertCountEquals(2)
        compose.onNode(hasSetTextAction() and hasText("70000-000"))
            .performScrollTo().assertIsEnabled().performTextReplacement("71000999")
        compose.onNode(hasSetTextAction() and hasText("71000-999")).assertExists()
        compose.onNode(hasSetTextAction() and hasText("R$ 100,00"))
            .performScrollTo().assertIsEnabled().performTextReplacement("12345")
        compose.onNode(hasSetTextAction() and hasText("R$ 123,45")).assertTextEquals("R$ 123,45")
        compose.runOnIdle {
            assertEquals(listOf("71000999"), zipChanges)
            assertEquals(listOf("R$ 123,45"), limitChanges)
        }
    }

    @Test fun `CEP warning and daily bound warning replace helper without removing editable fields`() {
        val state = mutableStateOf(profile())
        compose.setContent { CaixaDSTheme { PersonalDataScreen(state.value, false, {}, actions()) } }
        compose.onNodeWithText("R$ 10,00 até R$ 200,00").performScrollTo().assertExists()
        compose.runOnIdle {
            state.value = state.value.copy(zipCodeErrorMessage = "CEP não encontrado", dailyLimit = "R$ 9,99")
        }
        compose.onNodeWithText("CEP não encontrado").performScrollTo().assertExists()
        compose.onNodeWithText("O valor mínimo é R$ 10,00").performScrollTo().assertExists()
        compose.onNodeWithText("R$ 10,00 até R$ 200,00").assertDoesNotExist()
        compose.onAllNodes(hasSetTextAction()).assertCountEquals(2)
        compose.runOnIdle { state.value = state.value.copy(dailyLimit = "R$ 200,01") }
        compose.onNodeWithText("O valor mínimo é R$ 10,00").assertDoesNotExist()
        compose.onNodeWithText("O valor máximo é R$ 200,00").performScrollTo().assertExists()
        compose.runOnIdle { state.value = state.value.copy(dailyLimit = "R$ 200,00", zipCodeErrorMessage = null) }
        compose.onNodeWithText("O valor máximo é R$ 200,00").assertDoesNotExist()
        compose.onNodeWithText("CEP não encontrado").assertDoesNotExist()
        compose.onNodeWithText("R$ 10,00 até R$ 200,00").performScrollTo().assertExists()
    }

    @Test fun `footer follows profile readiness and saving and disappears while IME is visible`() {
        val state = mutableStateOf(profile())
        val ime = mutableStateOf(false)
        var saves = 0
        compose.setContent {
            CaixaDSTheme { PersonalDataScreen(state.value, ime.value, {}, actions().copy(submit = { saves++ })) }
        }
        compose.onNodeWithText("Atualizar").assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(1, saves) }
        val invalidStates = listOf(
            profile().copy(loading = true),
            profile().copy(saving = true),
            profile().copy(zipCode = "7000000"),
            profile().copy(stateCode = ""),
            profile().copy(city = ""),
            profile().copy(municipalityId = MunicipalityId(0, 0, 0)),
            profile().copy(dailyLimit = "")
        )
        invalidStates.forEach { invalid ->
            compose.runOnIdle { state.value = invalid }
            compose.onNodeWithText("Atualizar").assertIsNotEnabled()
        }
        compose.runOnIdle {
            state.value = profile()
            ime.value = true
        }
        compose.onNodeWithText("Atualizar").assertDoesNotExist()
        compose.runOnIdle { ime.value = false }
        compose.onNodeWithText("Atualizar").assertIsEnabled()
        compose.runOnIdle { assertEquals(1, saves) }
    }

    @Test fun `limit request and gender invoke their own actions and request is disabled while loading`() {
        val state = mutableStateOf(profile())
        val genders = mutableListOf<Gender>()
        var requests = 0
        compose.setContent {
            CaixaDSTheme {
                PersonalDataScreen(
                    state.value,
                    false,
                    {},
                    actions().copy(changeGender = { genders += it }, recalculateLimit = { requests++ })
                )
            }
        }
        compose.onNodeWithText("Feminino").performScrollTo().performClick()
        val request = compose.onNodeWithText("Solicitar limite acima de R$ 200,00")
        request.performScrollTo().assertIsEnabled().assertHeightIsEqualTo(64.dp).performClick()
        assertEquals(
            15.sp,
            compose.onNodeWithText("Solicitar limite acima de R$ 200,00", useUnmergedTree = true)
                .textLayout().layoutInput.style.fontSize
        )
        compose.runOnIdle {
            assertEquals(listOf(Gender.FEMALE), genders)
            assertEquals(1, requests)
            state.value = state.value.copy(loading = true)
        }
        request.assertIsNotEnabled()
    }

    @Test fun `screen keeps Caixa legacy typography and field alignment`() {
        compose.setContent { CaixaDSTheme { PersonalDataScreen(profile(), false, {}, actions()) } }
        val label = compose.onNodeWithText("CPF", useUnmergedTree = true)
        val labelStyle = label.textLayout().layoutInput.style
        assertEquals(16.sp, labelStyle.fontSize)
        assertEquals(FontWeight.Bold, labelStyle.fontWeight)
        assertEquals(CaixaDSFontFamily, labelStyle.fontFamily)
        val field = compose.onNodeWithText("123.456.789-01")
        val inputStyle = field.textLayout().layoutInput.style
        assertEquals(16.sp, inputStyle.fontSize)
        assertEquals(CaixaDSFontFamily, inputStyle.fontFamily)
        val labelBounds = label.fetchSemanticsNode().boundsInRoot
        val nameBounds = compose.onNodeWithText("Nome", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals(labelBounds.left, nameBounds.left, 0.5f)
        val fieldBounds = field.fetchSemanticsNode().boundsInRoot
        val expectedFieldTop = with(compose.density) {
            labelBounds.bottom + 4.dp.toPx() + (52.dp.toPx() - fieldBounds.height) / 2
        }
        assertEquals("Legacy label gap and 52dp input center", expectedFieldTop, fieldBounds.top, 1f)
        compose.onNodeWithText("Não sei o meu CEP").performScrollTo()
        assertEquals(14.sp, compose.onNodeWithText("Não sei o meu CEP").textLayout().layoutInput.style.fontSize)
        compose.onNodeWithText("R$ 10,00 até R$ 200,00").performScrollTo()
        assertEquals(16.sp, compose.onNodeWithText("R$ 10,00 até R$ 200,00").textLayout().layoutInput.style.fontSize)
    }

    private fun SemanticsNodeInteraction.textLayout(): TextLayoutResult {
        val layouts = mutableListOf<TextLayoutResult>()
        performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        return layouts.single()
    }

    private fun profile() = PersonalDataUiState(
        loading = false, cpf = "12345678901", name = "Synthetic Person", email = "fixture@example.test",
        zipCode = "70000000", stateCode = "DF", city = "Brasília", municipalityId = MunicipalityId(7, 1, 0),
        dailyLimit = "R$ 100,00", minimumDailyLimit = BigDecimal("10"), authorizedDailyLimit = BigDecimal("200")
    )

    private fun actions() = PersonalDataActions({}, {}, {}, {}, {}, {}, {})
}
