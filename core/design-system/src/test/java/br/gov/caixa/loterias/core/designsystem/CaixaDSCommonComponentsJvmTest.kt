// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAccountCopy
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSFingerprintIcon
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSIconButton
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoading
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoteriasLogo
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSNumberBall
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSSectionTitle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSFooterButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSOutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSCommonComponentsJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun textFieldShowsValidationAndReflectsEditsUntilParentDisablesIt() {
        val value = mutableStateOf("")
        val support = mutableStateOf<String?>("Informe seu CPF")
        val enabled = mutableStateOf(true)
        val changes = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSTextField(
                    value.value,
                    {
                        value.value = it
                        changes.add(it)
                    },
                    "CPF",
                    enabled = enabled.value,
                    isError = support.value != null,
                    supportingText = support.value
                )
            }
        }
        compose.onNodeWithText("Informe seu CPF").assertIsDisplayed()
        compose.onNode(hasSetTextAction()).performTextReplacement("12345678900")
        compose.onNode(hasSetTextAction()).assertTextEquals("CPF", "Informe seu CPF", "12345678900")
        compose.runOnIdle {
            support.value = null
            enabled.value = false
        }
        compose.onNodeWithText("Informe seu CPF").assertDoesNotExist()
        compose.onNodeWithText("12345678900").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(listOf("12345678900"), changes) }
    }

    @Test
    fun actionButtonsAndIconKeepCallbacksAndDisabledSemantics() {
        val enabled = mutableStateOf(true)
        val actions = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSPrimaryButton("Continuar", { actions.add("primary") }, enabled = enabled.value)
                    CaixaDSFooterButton("Pagar", { actions.add("footer") }, enabled = enabled.value)
                    CaixaDSOutlinedButton("Voltar", { actions.add("outlined") })
                    CaixaDSIconButton(CaixaDSIcons.Back, "Ação anterior", { actions.add("icon") }, enabled = enabled.value)
                    CaixaDSButton("Com ícone", { actions.add("leading") }, leadingIcon = CaixaDSIcons.Forward)
                }
            }
        }
        listOf("Continuar", "Pagar", "Voltar", "Com ícone").forEach { compose.onNodeWithText(it).performClick() }
        compose.onNodeWithContentDescription("Ação anterior").performClick()
        compose.runOnIdle {
            assertEquals(listOf("primary", "footer", "outlined", "leading", "icon"), actions)
            enabled.value = false
        }
        compose.onNodeWithText("Continuar").assertIsNotEnabled()
        compose.onNodeWithText("Pagar").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Ação anterior").assertIsNotEnabled()
    }

    @Test
    fun bothAlertMessageOverloadsKeepTheirConsumerConfirmation() {
        val annotated = mutableStateOf(false)
        val actions = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                if (annotated.value) {
                    CaixaDSAlertDialog("Aviso", AnnotatedString("Mensagem anotada"), "Entendi", { actions.add("annotated") })
                } else {
                    CaixaDSAlertDialog("Aviso", "Mensagem simples", "Entendi", { actions.add("plain") })
                }
            }
        }
        compose.onNodeWithText("Mensagem simples").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { annotated.value = true }
        compose.onNodeWithText("Mensagem anotada").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(listOf("plain", "annotated"), actions) }
    }

    @Test
    fun sharedTextLoadingLogoAndUnselectedBallExposeTheirContent() {
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSSectionTitle("Resultados")
                    CaixaDSAccountCopy("Seu cadastro", 18f, true)
                    CaixaDSAccountCopy("Mantenha seus dados atualizados", 14f, false)
                    CaixaDSLoading("Consultando resultados")
                    CaixaDSLoteriasLogo("Loterias CAIXA", Modifier.size(100.dp, 40.dp))
                    CaixaDSFingerprintIcon(Color.Blue, Modifier.size(24.dp).testTag("fingerprint"))
                    CaixaDSNumberBall(9, selected = false)
                }
            }
        }
        listOf("Resultados", "Seu cadastro", "Mantenha seus dados atualizados", "Consultando resultados", "09")
            .forEach { compose.onNodeWithText(it).assertIsDisplayed() }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertIsDisplayed()
        compose.onNodeWithContentDescription("Loterias CAIXA").assertIsDisplayed()
    }
}
