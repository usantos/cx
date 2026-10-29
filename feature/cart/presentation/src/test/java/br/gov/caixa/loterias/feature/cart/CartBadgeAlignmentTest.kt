package br.gov.caixa.loterias.feature.cart

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartFooter
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CartBadgeAlignmentTest {
    @get:Rule val compose = createComposeRule()

    @Test fun countIsCenteredInsideItsBadge() {
        val item = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "", BigDecimal.ONE)
        val cart = ShoppingCart(1, BigDecimal.ONE, listOf(item), totalBetCount = 4)
        compose.setContent { CaixaDSTheme { CurrentCartFooter(cart, true, {}, {}) } }
        val badge = compose.onNodeWithTag("cart-summary-badge", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val number = compose.onNodeWithText("4", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals("Horizontal center", badge.center.x, number.center.x, CENTER_TOLERANCE_PIXELS)
        assertEquals("Vertical center", badge.center.y, number.center.y, CENTER_TOLERANCE_PIXELS)
    }
}

private const val CENTER_TOLERANCE_PIXELS = 0.5f
