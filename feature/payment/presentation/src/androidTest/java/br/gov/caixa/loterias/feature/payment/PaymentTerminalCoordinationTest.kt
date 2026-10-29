package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.presentation.state.WorkflowSpec
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentTerminalRoute
import br.gov.caixa.loterias.feature.payment.presentation.screen.paymentScreens
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentTerminalViewModel
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PaymentTerminalCoordinationTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<PaymentTerminalViewModel>()

    @After fun cleanup() {
        models.forEach { it.viewModelScope.cancel() }
    }

    @Test fun guidanceCheckboxAndRowToggleTermsBeforeCompletionAndBackEffects() {
        var complete = 0
        var back = 0
        val vm = show("payment/pix-guidance", onBack = { back++ }, onComplete = { complete++ })
        compose.onNodeWithText("Pagar agora!").performScrollTo().assertIsNotEnabled()
        compose.onNode(isToggleable()).performScrollTo().performClick()
        compose.onNodeWithText("Pagar agora!").assertIsEnabled()
        compose.onNodeWithText(TERMS).performClick()
        compose.onNodeWithText("Pagar agora!").assertIsNotEnabled()
        compose.onNode(isToggleable()).performClick()
        compose.onNodeWithText("Pagar agora!").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertTrue(vm.state.value.pixTermsAccepted)
            assertEquals(1, complete)
            assertEquals(1, back)
        }
    }

    @Test fun replacingViewModelUsesCurrentPixActionsAndCancelsTheOldEffectCollector() {
        val replace = mutableStateOf(false)
        lateinit var first: PaymentTerminalViewModel
        lateinit var second: PaymentTerminalViewModel
        var complete = 0
        var back = 0
        compose.setContent {
            first = remember { model(spec("payment/pix-guidance")) }
            second = remember { model(spec("payment/pix-guidance")) }
            CaixaDSTheme { PaymentTerminalRoute(if (replace.value) second else first, { back++ }, { complete++ }) }
        }
        compose.onNode(isToggleable()).performScrollTo().performClick()
        compose.runOnIdle { replace.value = true }
        compose.waitForIdle()
        compose.onNodeWithText("Pagar agora!").performScrollTo().assertIsNotEnabled()
        compose.onNode(isToggleable()).performScrollTo().performClick()
        compose.onNodeWithText("Pagar agora!").performClick()
        compose.runOnIdle {
            assertTrue(first.state.value.pixTermsAccepted)
            assertTrue(second.state.value.pixTermsAccepted)
            first.submit()
            first.back()
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, complete)
            assertEquals(0, back)
        }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, back) }
    }

    @Test fun failureActionsUseMethodAndHomeDestinationsWithoutCompleting() {
        val destinations = mutableListOf<String>()
        var back = 0
        var complete = 0
        show("payment/failure", { back++ }, { complete++ }, destinations::add)
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Voltar ao início").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(listOf("payment/method", "home", "home"), destinations)
            assertEquals(0, back)
            assertEquals(0, complete)
        }
    }

    @Test fun processingKeepsHelpPurchaseAndHomeNavigationWithoutCompleting() {
        val destinations = mutableListOf<String>()
        var complete = 0
        show("payment/confirmation", onComplete = { complete++ }, onNavigate = destinations::add)
        compose.onNodeWithText("Estamos processando sua compra.").assertIsDisplayed()
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.onNodeWithText("Acompanhe suas compras").performClick()
        compose.onNodeWithText("Voltar ao início").performClick()
        compose.runOnIdle {
            assertEquals(listOf("support/terms", "tickets/purchases", "home"), destinations)
            assertEquals(0, complete)
        }
    }

    @Test fun successUsesTheSameProcessingViewAndCanOmitItsSecondaryAction() {
        val destinations = mutableListOf<String>()
        compose.setContent {
            val vm = remember { model(spec("payment/success").copy(secondaryAction = null)) }
            CaixaDSTheme { PaymentTerminalRoute(vm, {}, {}, destinations::add) }
        }
        compose.onNodeWithText("Estamos processando sua compra.").assertIsDisplayed()
        compose.onNodeWithText("Voltar ao início").assertDoesNotExist()
        compose.onNodeWithText("Acompanhe suas compras").performClick()
        compose.runOnIdle { assertEquals(listOf("tickets/purchases"), destinations) }
    }

    @Test fun processingReceivesUpdatedNavigationCallbackWithoutReplacingViewModel() {
        val replace = mutableStateOf(false)
        val first = mutableListOf<String>()
        val second = mutableListOf<String>()
        compose.setContent {
            val vm = remember { model(spec("payment/confirmation")) }
            val onNavigate: (String) -> Unit = if (replace.value) {
                { destination -> second.add(destination) }
            } else {
                { destination -> first.add(destination) }
            }
            CaixaDSTheme { PaymentTerminalRoute(vm, {}, {}, onNavigate) }
        }
        compose.runOnIdle { replace.value = true }
        compose.onNodeWithText("Acompanhe suas compras").performClick()
        compose.runOnIdle {
            assertTrue(first.isEmpty())
            assertEquals(listOf("tickets/purchases"), second)
        }
    }

    private fun show(
        route: String,
        onBack: () -> Unit = {
        },
        onComplete: () -> Unit = {},
        onNavigate: (String) -> Unit = {},
    ): PaymentTerminalViewModel {
        lateinit var vm: PaymentTerminalViewModel
        compose.setContent {
            vm = remember { model(spec(route)) }
            CaixaDSTheme { PaymentTerminalRoute(vm, onBack, onComplete, onNavigate) }
        }
        compose.waitForIdle()
        return vm
    }
    private fun model(spec: WorkflowSpec) = PaymentTerminalViewModel(spec).also { models.add(it) }
    private fun spec(route: String) =
        paymentScreens(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext::getString).single {
            it.route ==
                route
        }
    companion object {
        private const val TERMS = "Estou ciente que somente será aceito o pagamento realizado em conta de titularidade do apostador."
    }
}
