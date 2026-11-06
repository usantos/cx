package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerHeader
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerInstitution
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerItem
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSDrawerSheet
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LoteriasCaixaDSDrawerJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun drawerHeaderAndItemExposeGreetingExpandedStateAndIndependentActions() {
        val expanded = mutableStateOf(false)
        val calls = mutableListOf<String>()
        compose.setContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSDrawerSheet {
                    LoteriasCaixaDSDrawerHeader("Olá, Maria", { calls.add("close") })
                    LoteriasCaixaDSDrawerItem("Minha conta", {
                        calls.add("account")
                        expanded.value = !expanded.value
                    }, expanded = expanded.value)
                    if (expanded.value) LoteriasCaixaDSDrawerItem("Meus dados", { calls.add("data") }, child = true)
                }
            }
        }
        val greeting = compose.onNodeWithText("Olá, Maria").assertIsDisplayed().fetchSemanticsNode()
        assertTrue(greeting.config.contains(SemanticsProperties.Heading))
        val account = compose.onNodeWithText("Minha conta")
        assertEquals("Recolhido", account.fetchSemanticsNode().config[SemanticsProperties.StateDescription])
        compose.onNodeWithText("Meus dados").assertDoesNotExist()
        account.performClick()
        assertEquals("Expandido", account.fetchSemanticsNode().config[SemanticsProperties.StateDescription])
        compose.onNodeWithText("Meus dados").performClick()
        compose.onNodeWithContentDescription("Fechar menu").performClick()
        compose.runOnIdle { assertEquals(listOf("account", "data", "close"), calls) }
    }

    @Test
    fun campaignDrawerKeepsChildrenAndInstitutionDetailsUsable() {
        val expanded = mutableStateOf(false)
        var childClicks = 0
        compose.setContent {
            LoteriasCaixaDSTheme {
                LoteriasCaixaDSDrawerSheet(LoteriasCaixaDSCampaign.OCTOBER_ROSE) {
                    LoteriasCaixaDSDrawerHeader("Bem-vinda", {})
                    LoteriasCaixaDSDrawerItem("Prêmios", { childClicks++ }, child = true)
                    LoteriasCaixaDSDrawerInstitution(expanded.value, { expanded.value = !expanded.value }, "Informações institucionais")
                }
            }
        }
        compose.onNodeWithText("Bem-vinda").assertIsDisplayed()
        compose.onNodeWithText("Prêmios").performClick()
        val institution = compose.onNodeWithText("CAIXA Loterias")
        assertEquals("Recolhido", institution.fetchSemanticsNode().config[SemanticsProperties.StateDescription])
        compose.onNodeWithText("Informações institucionais").assertDoesNotExist()
        institution.performClick()
        compose.onNodeWithText("Informações institucionais").assertIsDisplayed()
        assertEquals("Expandido", institution.fetchSemanticsNode().config[SemanticsProperties.StateDescription])
        institution.performClick()
        compose.onNodeWithText("Informações institucionais").assertDoesNotExist()
        compose.runOnIdle { assertEquals(1, childClicks) }
    }
}
