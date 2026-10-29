package br.gov.caixa.loterias.feature.cart

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartFooter
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val SINGLE_DIGIT = 9
private const val TWO_DIGITS = 15
private const val HIGH_TWO_DIGITS = 99
private const val THREE_DIGITS = 999
private const val OVERFLOW_COUNT = 1000
private const val PREVIEW_CHUNK = 3000
private const val PNG_QUALITY = 100

class CurrentCartBadgeCountTest {
    @get:Rule val compose = createComposeRule()

    @Test fun fifteenGamesShowBothDigitsOnOneLine() = checkCount(TWO_DIGITS)

    @Test fun ninetyNineGamesFitWithoutClipping() = checkCount(HIGH_TWO_DIGITS)

    @Test fun threeDigitCountFitsWithoutClipping() = checkCount(THREE_DIGITS)

    @Test fun aboveNativeLimitShowsCompleteOverflowLabel() = checkCount(OVERFLOW_COUNT, "...")

    @Test fun completeSnapshotCountWinsOverGroupedCards() {
        val combo = individual.copy(kind = CartItemKind.COMBO, comboBetCount = 2)
        show(ShoppingCart(1, BigDecimal.TEN, listOf(combo), totalBetCount = TWO_DIGITS))
        assertCount(TWO_DIGITS.toString())
    }

    @Test fun fallbackCountsGamesInsideComboAndIndividualBet() {
        val combo = individual.copy(id = 2, kind = CartItemKind.COMBO, comboBetCount = 2)
        show(ShoppingCart(1, BigDecimal.TEN, listOf(combo, individual)))
        assertCount("3")
    }

    @Test fun removingGamesUpdatesTheVisibleCounterAcrossDigitBoundary() {
        val cart = mutableStateOf(cart(TWO_DIGITS))
        compose.setContent { CaixaDSTheme { CurrentCartFooter(cart.value, true, {}, {}) } }
        assertCount(TWO_DIGITS.toString())
        compose.runOnIdle { cart.value = cart(SINGLE_DIGIT) }
        assertCount("9")
    }

    private val individual = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Lotofácil", "", BigDecimal.ONE)

    private fun cart(count: Int) = ShoppingCart(1, BigDecimal.TEN, List(count) { individual.copy(id = it.toLong()) })

    private fun show(cart: ShoppingCart) {
        compose.setContent { CaixaDSTheme { CurrentCartFooter(cart, true, {}, {}) } }
    }

    private fun checkCount(count: Int, expected: String = count.toString()) {
        show(cart(count))
        assertCount(expected)
        if (count == TWO_DIGITS) capturePreview()
    }

    private fun assertCount(expected: String) {
        val node = compose.onNodeWithText(expected, useUnmergedTree = true).assertIsDisplayed()
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        assertEquals("Every digit stays on the same line", 1, layout.lineCount)
        assertTrue("The complete counter fits its badge", layout.getLineRight(0) <= layout.size.width + 1f)
    }

    private fun capturePreview() {
        val bytes = ByteArrayOutputStream()
        compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, bytes)
        val chunks = Base64.encodeToString(bytes.toByteArray(), Base64.NO_WRAP).chunked(PREVIEW_CHUNK)
        chunks.forEachIndexed { index, chunk -> Log.i("CartBadgePreview", "cart-15:$index/${chunks.size}:$chunk") }
    }
}
