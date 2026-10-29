package br.gov.caixa.loterias.core.designsystem

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerHeader
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerInstitution
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerItem
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerSheet
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSDrawerJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun drawerHeaderAndItemExposeGreetingExpandedStateAndIndependentActions() {
        val expanded = mutableStateOf(false)
        val calls = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSDrawerSheet {
                    CaixaDSDrawerHeader("Olá, Maria", { calls.add("close") })
                    CaixaDSDrawerItem("Minha conta", {
                        calls.add("account")
                        expanded.value = !expanded.value
                    }, expanded = expanded.value)
                    if (expanded.value) CaixaDSDrawerItem("Meus dados", { calls.add("data") }, child = true)
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
            CaixaDSTheme {
                CaixaDSDrawerSheet(CaixaDSCampaign.OCTOBER_ROSE) {
                    CaixaDSDrawerHeader("Bem-vinda", {})
                    CaixaDSDrawerItem("Prêmios", { childClicks++ }, child = true)
                    CaixaDSDrawerInstitution(expanded.value, { expanded.value = !expanded.value }, "Informações institucionais")
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
