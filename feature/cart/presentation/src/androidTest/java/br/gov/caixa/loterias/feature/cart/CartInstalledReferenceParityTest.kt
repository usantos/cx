package br.gov.caixa.loterias.feature.cart

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartActions
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartScaffold
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartContent
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartRow
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

private const val SCREEN_WIDTH_DP = 336
private const val DELETE_SIZE_DP = 25
private const val PIXEL_TOLERANCE = 2f
private const val FIFTEEN_GAMES = 15
private const val SUPER_COLUMNS = 7
private const val SUPER_NUMBER = 9
private const val TEXT_PIXEL_TOLERANCE = 0.01f
private const val ALPHA_THRESHOLD = 0.5f
private const val BLUE_THRESHOLD = 0.2f
private const val GREEN_THRESHOLD = 0.1f
private const val PNG_QUALITY = 100

/** Verificações de regressão usam a referência instalada, sem serviços, sessão de usuário ou mutação de carrinho. */
class CartInstalledReferenceParityTest {
    @get:Rule val compose = createComposeRule()
    private val individual = CartItem(
        1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "", BigDecimal("6.00"),
        modalityCode = "MEGA_SENA", contestNumber = 3070, numberQuantity = 6,
        selectedNumbers = listOf(listOf(2, 10, 18, 27, 32, 43))
    )
    private val lottery = individual.copy(
        id = 2,
        title = "Lotofácil",
        modalityCode = "LOTOFACIL",
        contestNumber = 3814,
        amount = BigDecimal("3.50"),
        numberQuantity = FIFTEEN_GAMES,
        selectedNumbers = listOf(listOf(1, 2, 5, 7, 8, 13, 14, 15, 16, 18, 20, 22, 23, 24, 25))
    )
    private val combo = CartItem(3, CartItemKind.COMBO, "Combo da Sorte", "", BigDecimal("6.00"), comboBetCount = 1)
    private val cart = ShoppingCart(1, BigDecimal("15.50"), listOf(individual, lottery, combo))
    private fun reference() = CartNativeReference(InstrumentationRegistry.getInstrumentation().targetContext)

    @org.junit.Before fun requireInstalledReference() {
        reference()
    }

    @Test fun fullCartCapturesInstalledReferenceAndCorrectedBody() {
        val native = mutableStateOf(false)
        compose.setContent { CaixaDSTheme { if (native.value) CartReferencePreview(cart) else Current(cart) } }
        capture("cart-after")
        compose.runOnIdle { native.value = true }
        compose.waitForIdle()
        capture("cart-reference")
    }

    @Test fun individualDeleteArtworkMatchesInstalledImageView() {
        compose.setContent { CaixaDSTheme { CurrentCartRow(individual, Instant.EPOCH, {}, null) } }
        val actual = compose.onNodeWithContentDescription("Excluir", useUnmergedTree = true).captureToImage().toPixelMap()
        var expectedWidth = 0
        compose.runOnIdle {
            val native = reference()
            val row = native.inflate("item_aposta_carrinho")
            native.measure(row, (SCREEN_WIDTH_DP * compose.density.density).toInt())
            val delete = row.findViewById<View>(native.id("btnExcluir"))
            val size = (DELETE_SIZE_DP * compose.density.density).toInt()
            native.measure(delete, size, size)
            val image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            delete.draw(Canvas(image))
            expectedWidth = coloredWidth(image.asImageBitmap().toPixelMap())
            Log.i("CartReferenceMetrics", "deletePadding=${delete.paddingLeft}; drawableWidth=$expectedWidth")
        }
        assertEquals(
            "Delete artwork uses the installed reference scale",
            expectedWidth.toFloat(),
            coloredWidth(actual).toFloat(),
            PIXEL_TOLERANCE
        )
    }

    @Test fun footerTotalUsesInstalledRoundedTextSize() {
        compose.setContent { CaixaDSTheme { Current(cart) } }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("R$ 15,50").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        var nativeSize = 0f
        compose.runOnIdle {
            val native = reference()
            nativeSize =
                native.text(native.inflate("activity_carrinho"), "tvValorTotalCarrinho").textSize
        }
        assertEquals(nativeSize, with(compose.density) { layouts.single().layoutInput.style.fontSize.toPx() }, TEXT_PIXEL_TOLERANCE)
    }

    @Test fun superSevenHeadersMatchInstalledSquareCellsAndInset() {
        val superSeven = individual.copy(
            modalityCode = "SUPER_7",
            title = "Super Sete",
            selectedNumbers = List(SUPER_COLUMNS) {
                listOf(SUPER_NUMBER)
            }
        )
        compose.setContent { CaixaDSTheme { CurrentCartRow(superSeven, Instant.EPOCH, {}, null) } }
        val actual = compose.onNodeWithTag("cart-super-seven-header-0").fetchSemanticsNode().boundsInRoot
        var expectedHeight = 0
        var expectedStart = 0
        var expectedTitleColor = 0
        var expectedLastEnd = 0
        var expectedContestTop = 0
        var expectedPriceTop = 0
        compose.runOnIdle {
            val native = reference()
            val row = native.betRow(superSeven)
            native.measure(row, (SCREEN_WIDTH_DP * compose.density.density).toInt())
            val header = row.findViewById<android.view.ViewGroup>(native.id("containerColunasSuperSete")).getChildAt(0)
            expectedTitleColor = native.text(row, "tvModalidade").currentTextColor
            val headers = header.parent as android.view.ViewGroup
            expectedLastEnd = headers.getChildAt(SUPER_COLUMNS - 1).right
            expectedContestTop = native.text(row, "tvConcurso").top
            expectedPriceTop = native.text(row, "tvValor").top
            expectedHeight = header.height
            expectedStart = header.left
            Log.i(
                "CartReferenceMetrics",
                "superSevenHeader=${header.width}x${header.height}; start=${header.left}; " +
                    "textPx=${(header as android.widget.TextView).textSize}"
            )
        }
        assertEquals("Super Sete native square height", expectedHeight.toFloat(), actual.height, PIXEL_TOLERANCE)
        assertEquals("Super Sete first native column inset", expectedStart.toFloat(), actual.left, PIXEL_TOLERANCE)
        assertEquals(
            "All seven native column widths and gaps",
            expectedLastEnd.toFloat(),
            compose.onNodeWithTag("cart-super-seven-header-${SUPER_COLUMNS - 1}").fetchSemanticsNode().boundsInRoot.right,
            PIXEL_TOLERANCE
        )
        assertEquals(
            "Native contest anchor above the Super Sete grid",
            expectedContestTop.toFloat(),
            compose.onNodeWithText("Conc. 3070").fetchSemanticsNode().boundsInRoot.top,
            PIXEL_TOLERANCE
        )
        assertEquals(
            "Native price anchor below the contest",
            expectedPriceTop.toFloat(),
            compose.onNodeWithText("R$ 6,00").fetchSemanticsNode().boundsInRoot.top,
            PIXEL_TOLERANCE
        )
        val titleLayouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("super sete").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(titleLayouts) }
        assertEquals("Super Sete native title color", expectedTitleColor, titleLayouts.single().layoutInput.style.color.toArgb())
    }

    @Test fun dynamicNumberRowsRetainInstalledFractionalTextSize() {
        compose.setContent { CaixaDSTheme { CurrentCartRow(individual, Instant.EPOCH, {}, null) } }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("02 - 10 - 18 - 27 - 32 - 43").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        var expectedSize = 0f
        compose.runOnIdle {
            val native = reference()
            val row = native.betRow(individual)
            val numbers = row.findViewById<android.view.ViewGroup>(native.id("containerNumeros"))
            expectedSize = (numbers.getChildAt(0) as android.widget.TextView).textSize
        }
        assertEquals(expectedSize, with(compose.density) { layouts.single().layoutInput.style.fontSize.toPx() }, TEXT_PIXEL_TOLERANCE)
    }

    @Test fun superSevenCartCapturesNativeReference() {
        val superSeven = individual.copy(
            modalityCode = "SUPER_7",
            title = "Super Sete",
            selectedNumbers = List(SUPER_COLUMNS) {
                listOf(SUPER_NUMBER)
            }
        )
        val snapshot = cart.copy(total = superSeven.amount, items = listOf(superSeven))
        val native = mutableStateOf(false)
        compose.setContent { CaixaDSTheme { if (native.value) CartReferencePreview(snapshot) else Current(snapshot) } }
        capture("cart-super-seven-after")
        compose.runOnIdle { native.value = true }
        compose.waitForIdle()
        capture("cart-super-seven-reference")
    }

    @Test fun modalityTitlesUseInstalledCartPalette() {
        val expected = reference().modalityColors()
        Log.i("CartReferenceMetrics", "palette=$expected")
        compose.setContent {
            CaixaDSTheme {
                androidx.compose.foundation.layout.Column {
                    expected.keys.forEach { code ->
                        br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText(
                            code,
                            color = br.gov.caixa.loterias.feature.cart.presentation.screen.currentCartModalityColor(code)
                        )
                    }
                }
            }
        }
        expected.forEach { (code, color) ->
            val layouts = mutableListOf<TextLayoutResult>()
            compose.onNodeWithText(code).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertEquals(code, color, layouts.single().layoutInput.style.color.toArgb())
        }
    }

    @Test fun fifteenGamesRemainCorrectAfterVisualEqualization() {
        val snapshot = cart.copy(
            total = BigDecimal("80.00"),
            items = listOf(individual, lottery, combo.copy(comboBetCount = FIFTEEN_GAMES - 2, amount = BigDecimal("70.50"))),
            totalBetCount = FIFTEEN_GAMES
        )
        compose.setContent { CaixaDSTheme { Current(snapshot) } }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(FIFTEEN_GAMES.toString()).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(1, layouts.single().lineCount)
        capture("cart-fifteen-after")
    }

    private fun coloredWidth(image: androidx.compose.ui.graphics.PixelMap): Int {
        val xs = mutableListOf<Int>()
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val c = image[x, y]
                if (c.alpha > ALPHA_THRESHOLD && c.blue > c.red + BLUE_THRESHOLD && c.green > c.red + GREEN_THRESHOLD) xs += x
            }
        }
        return if (xs.isEmpty()) 0 else xs.max() - xs.min() + 1
    }

    private fun capture(name: String) {
        val bytes = java.io.ByteArrayOutputStream()
        compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, bytes)
        val descriptors = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommandRw("dd of=/data/local/tmp/$name.png")
        android.os.ParcelFileDescriptor.AutoCloseOutputStream(descriptors[1]).use { it.write(bytes.toByteArray()) }
        android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptors[0]).use { it.readBytes() }
    }

    @androidx.compose.runtime.Composable
    private fun Current(snapshot: ShoppingCart) {
        val state = CartUiState(loading = false, cart = snapshot, authenticated = true)
        CurrentCartScaffold(state, snapshot, {}, {}, CurrentCartActions({}, {}, {}, {})) { padding ->
            CurrentCartContent(state, padding, snapshot.items, 0L, {}, {}, {})
        }
    }
}
