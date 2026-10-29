package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartFooter
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import kotlin.math.roundToInt

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
        compose.setContent { CaixaDSTheme { CurrentCartFooter(state.value, true, {}, {}) } }
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

    @Test fun emptyMessageUsesNativeCaixaSemiboldTwelveSpAndIconSize() {
        compose.setContent { CaixaDSTheme {
            br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartContent(
                br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState(),
                androidx.compose.foundation.layout.PaddingValues(), emptyList(), 0L, {}, {}, {},
            )
        } }
        val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        compose.onNodeWithText("Seu carrinho está vazio").performSemanticsAction(
            androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult,
        ) { it(layouts) }
        val style = layouts.single().layoutInput.style
        assertEquals(br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily, style.fontFamily)
        val nativeTextPixels = with(compose.density) { 12.sp.toPx().roundToInt().toFloat() }
        assertEquals(nativeTextPixels, with(compose.density) { style.fontSize.toPx() }, 0.01f)
        assertEquals(androidx.compose.ui.text.font.FontWeight.SemiBold, style.fontWeight)
        val icon = compose.onNodeWithTag("cart-empty-icon").fetchSemanticsNode().boundsInRoot
        assertEquals(140f * compose.density.density, icon.width, 1f)
        assertEquals(140f * compose.density.density, icon.height, 1f)
    }

    private fun show(cart: ShoppingCart?) {
        compose.setContent { CaixaDSTheme { CurrentCartFooter(cart, true, {}, { error("Empty checkout must not run") }) } }
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
