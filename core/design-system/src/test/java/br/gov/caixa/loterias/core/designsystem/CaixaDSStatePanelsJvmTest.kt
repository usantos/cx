package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSBetTypeItem
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSBetTypeStrip
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMercadoPagoPrizePanel
import br.gov.caixa.loterias.core.designsystem.components.state.CaixaDSGateMessage
import br.gov.caixa.loterias.core.designsystem.components.state.CaixaDSStatusAction
import br.gov.caixa.loterias.core.designsystem.components.state.showCaixaDSMessage
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSBetTypeIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSStatePanelsJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun statusActionShowsReasonAndDispatchesRetry() {
        var retries = 0
        compose.setContent {
            CaixaDSTheme { CaixaDSStatusAction("Sem conexão", "Confira sua internet", "Tentar novamente", { retries++ }) }
        }
        compose.onNodeWithText("Sem conexão").assertIsDisplayed()
        compose.onNodeWithText("Confira sua internet").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test
    fun gateMessageExposesProgressAndUpdatedAccessibleText() {
        val message = mutableStateOf("Validando acesso")
        compose.setContent { CaixaDSTheme { CaixaDSGateMessage(message.value) } }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertIsDisplayed()
        compose.onNodeWithText("Validando acesso").assertIsDisplayed()
        compose.runOnIdle { message.value = "Preparando sua conta" }
        compose.onNodeWithText("Validando acesso").assertDoesNotExist()
        compose.onNodeWithText("Preparando sua conta").assertIsDisplayed()
    }

    @Test
    fun betTypeStripPreservesCallerOrderAndHeadingSemanticsAndDisappearsWhenEmpty() {
        val items =
            mutableStateOf(
                listOf(CaixaDSBetTypeItem("Bolão", CaixaDSBetTypeIcons.Pool), CaixaDSBetTypeItem("Troca", CaixaDSBetTypeIcons.Exchange))
            )
        compose.setContent { CaixaDSTheme { CaixaDSBetTypeStrip(items.value, Color.White, Color.Blue, Modifier.testTag("types")) } }
        val pool = compose.onNodeWithText("Bolão").assertIsDisplayed().fetchSemanticsNode()
        val exchange = compose.onNodeWithText("Troca").assertIsDisplayed().fetchSemanticsNode()
        assertTrue(pool.config.contains(SemanticsProperties.Heading))
        assertTrue(exchange.config.contains(SemanticsProperties.Heading))
        assertTrue(pool.boundsInRoot.left < exchange.boundsInRoot.left)
        compose.runOnIdle { items.value = emptyList() }
        compose.onNodeWithTag("types").assertDoesNotExist()
        compose.onNodeWithText("Bolão").assertDoesNotExist()
    }

    @Test
    fun prizePanelRendersUpdatedAmountAndNativeFeedbackUsesShortToast() {
        val amount = mutableStateOf("R$ 25,00")
        lateinit var context: android.content.Context
        compose.setContent {
            context = LocalContext.current
            CaixaDSTheme { Column { CaixaDSMercadoPagoPrizePanel(amount.value) } }
        }
        compose.onNodeWithText("Prêmio").assertIsDisplayed()
        compose.onNodeWithText("R$ 25,00").assertIsDisplayed()
        compose.runOnIdle {
            amount.value = "R$ 50,00"
            showCaixaDSMessage(context, "Dados atualizados")
        }
        compose.onNodeWithText("R$ 50,00").assertIsDisplayed()
        compose.onNodeWithText("R$ 25,00").assertDoesNotExist()
        assertEquals("Dados atualizados", ShadowToast.getTextOfLatestToast())
        assertEquals(android.widget.Toast.LENGTH_SHORT, ShadowToast.getLatestToast().duration)
    }
}
