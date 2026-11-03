package br.gov.caixa.loterias.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.components.cart.isCartSummaryVisible
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.createFeatureComposeRuleV2
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.home.presentation.components.HomeCartSummary
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** SomadorCarrinhoFragment: total positivo controla a visibilidade; contagem do retrato completo controla o indicador. */
class HomeCartSummaryUiTest {
    @get:Rule val compose = createFeatureComposeRuleV2()

    @Test fun homeCartTracksEmptyPopulatedAndOverflowStatesAndOpensTheCart() {
        val state = mutableStateOf(HomeUiState(cartItemCount = 8))
        var opens = 0
        val actions = HomeActions(
            retry = {}, openMenu = {}, openNotifications = {}, toggleCampaign = {}, toggleQuickAccess = {},
            readTicket = {}, responsibleGaming = {}, socialTransfers = {}, womenSupport = {}, toggleResult = {},
            pool = {}, bet = {}, openCart = { opens++ }
        )
        compose.setFeatureContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                Box { HomeCartSummary(state.value, isCartSummaryVisible(state.value.cartTotal), actions) }
            }
        }
        compose.onNodeWithText("Valor atual do carrinho", useUnmergedTree = true).assertDoesNotExist()
        compose.runOnIdle { state.value = state.value.copy(cartTotal = BigDecimal("25.50"), cartItemCount = 999) }
        compose.onNodeWithText("R$ 25,50", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("999", useUnmergedTree = true).assertIsDisplayed()
        compose.onNode(hasClickAction()).performClick()
        compose.runOnIdle {
            assertEquals(1, opens)
            state.value = state.value.copy(cartItemCount = 1000)
        }
        compose.onNodeWithText("...", useUnmergedTree = true).assertIsDisplayed()
        compose.runOnIdle { state.value = state.value.copy(cartTotal = BigDecimal("-1")) }
        compose.onNodeWithText("Valor atual do carrinho", useUnmergedTree = true).assertDoesNotExist()
    }
}
