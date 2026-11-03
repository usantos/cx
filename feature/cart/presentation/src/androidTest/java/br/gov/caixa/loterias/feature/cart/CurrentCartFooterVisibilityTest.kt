package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartFooter
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CurrentCartFooterVisibilityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun emptyCartHidesCheckoutIconAndDividerAndCentersTotal() {
        show(ShoppingCart(0, BigDecimal.ZERO, emptyList()))
        assertEmptyFooter()
    }

    @Test fun absentCartAlsoHidesCheckoutAndCentersTotal() {
        show(null)
        assertEmptyFooter()
    }

    @Test fun removingLastItemHidesActionsAndAddingItemRestoresThemEvenAtZeroPrice() {
        val empty = ShoppingCart(0, BigDecimal.ZERO, emptyList())
        val filled = empty.copy(items = listOf(CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "01 02 03 04 05 06", BigDecimal.ZERO)))
        val state = mutableStateOf<ShoppingCart?>(filled)
        compose.setFeatureContent { LoteriasCaixaDSTheme { CurrentCartFooter(state.value, true, {}, {}) } }
        compose.onNodeWithText("Ir para pagamento").assertIsDisplayed()
        compose.onNodeWithContentDescription("Carrinho").assertExists()
        compose.onNodeWithTag("cart-summary-divider").assertExists()
        compose.runOnIdle { state.value = empty }
        assertEmptyFooter()
        compose.runOnIdle { state.value = filled }
        compose.onNodeWithText("Ir para pagamento").assertIsDisplayed()
        compose.onNodeWithContentDescription("Carrinho").assertExists()
        compose.onNodeWithTag("cart-summary-divider").assertExists()
    }

    @Test fun filledFooterCountStaysCenteredInsideNativeBadge() {
        val items = listOf(
            CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "", BigDecimal("6.00")),
            CartItem(2, CartItemKind.COMBO, "Combo", "", BigDecimal("12.00"), comboBetCount = 2)
        )
        compose.setFeatureContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                CurrentCartFooter(ShoppingCart(1, BigDecimal("18.00"), items), true, {}, {})
            }
        }
        val icon = compose.onNodeWithContentDescription("Carrinho").fetchSemanticsNode().boundsInRoot
        val badge = compose.onNodeWithTag("cart-summary-badge", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val count = compose.onNodeWithText("3").fetchSemanticsNode().boundsInRoot
        // Preserva o indicador de 15dp do legado e a correção de centralização do contador.
        assertEquals(NATIVE_BADGE_SIZE_DP * compose.density.density, badge.height, 1f)
        assertEquals(NATIVE_BADGE_SIZE_DP * compose.density.density, badge.width, 1f)
        assertEquals(icon.top, badge.top, 1f)
        assertEquals(badge.center.y, count.center.y, CENTER_TOLERANCE_PIXELS)
        assertEquals(badge.center.x, count.center.x, CENTER_TOLERANCE_PIXELS)
    }

    private fun show(cart: ShoppingCart?) {
        compose.setFeatureContent { LoteriasCaixaDSTheme { CurrentCartFooter(cart, true, {}, { error("Empty checkout must not run") }) } }
    }

    private fun assertEmptyFooter() {
        compose.onNodeWithText("Ir para pagamento").assertDoesNotExist()
        compose.onNodeWithContentDescription("Carrinho").assertDoesNotExist()
        compose.onNodeWithTag("cart-summary-divider").assertDoesNotExist()
        compose.onNodeWithText("Adicionar nova aposta").assertIsDisplayed()
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val title = compose.onNodeWithText("Total do carrinho").fetchSemanticsNode().boundsInRoot
        val amount = compose.onNodeWithText("R$ 0,00").fetchSemanticsNode().boundsInRoot
        assertEquals(root.center.x, title.center.x, 1f)
        assertEquals(root.center.x, amount.center.x, 1f)
    }
}

private const val NATIVE_BADGE_SIZE_DP = 15f

private const val CENTER_TOLERANCE_PIXELS = 0.5f
