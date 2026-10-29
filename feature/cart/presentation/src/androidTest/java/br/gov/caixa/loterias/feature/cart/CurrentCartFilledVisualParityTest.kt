package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartFooter
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartRow
import java.math.BigDecimal
import java.time.Instant
import kotlin.math.roundToInt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CurrentCartFilledVisualParityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun lotomaniaKeepsInstalledCartRawNumberFormatting() {
        show(
            CartItem(
                1,
                CartItemKind.INDIVIDUAL_BET,
                "Lotomania",
                "",
                BigDecimal("3.00"),
                modalityCode = "LOTOMANIA",
                numberQuantity = 6,
                selectedNumbers = listOf(listOf(1, 20, 45, 60, 99, 100))
            )
        )

        // ApostaViewHolder novo instalado preenche o inteiro bruto; não chama o VolanteUtils antigo.
        compose.onNodeWithText("01 - 20 - 45 - 60 - 99 - 100").assertIsDisplayed()
    }

    @Test fun singleBetComboUsesSingularCount() {
        show(CartItem(2, CartItemKind.COMBO, "Combo especial", "", BigDecimal("6.00"), comboBetCount = 1))

        // ComboViewHolder instalado usa quantidade_apostas com um recurso distinto para o singular.
        compose.onNodeWithText("1 aposta").assertIsDisplayed()
        compose.onNodeWithText("1 apostas").assertDoesNotExist()
    }

    @Test fun filledFooterCountKeepsNativeVerticalTranslationInsideBadge() {
        val items = listOf(
            CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "", BigDecimal("6.00")),
            CartItem(2, CartItemKind.COMBO, "Combo", "", BigDecimal("12.00"), comboBetCount = 2)
        )
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                CurrentCartFooter(ShoppingCart(1, BigDecimal("18.00"), items), true, {}, {})
            }
        }
        val icon = compose.onNodeWithContentDescription("Carrinho").fetchSemanticsNode().boundsInRoot
        val count = compose.onNodeWithText("3").fetchSemanticsNode().boundsInRoot
        // activity_carrinho instalado: indicador de 15dp, texto centralizado, translationY=1.5dp.
        assertEquals(9f * compose.density.density, count.center.y - icon.top, 1f)
    }

    @Test fun poolReservationFollowsTitleWithoutDeleteIconInflatingHeader() {
        show(pool())
        val title = compose.onNodeWithText("mega-sena").fetchSemanticsNode().boundsInRoot
        val reservation = compose.onNodeWithText("cota reservada nº 1 de 10").fetchSemanticsNode().boundsInRoot
        assertEquals(8f * compose.density.density, reservation.top - title.bottom, 1f)
    }

    @Test fun comboCountFollowsTitleWithoutDeleteIconInflatingHeader() {
        show(CartItem(2, CartItemKind.COMBO, "Combo especial", "", BigDecimal("6.00"), comboBetCount = 2))
        val title = compose.onNodeWithText("Combo especial").fetchSemanticsNode().boundsInRoot
        val count = compose.onNodeWithText("2 apostas").fetchSemanticsNode().boundsInRoot
        var nativeGap = 0
        compose.runOnIdle {
            val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
            val density = context.resources.displayMetrics.density
            fun dp(value: Int) = (value * density + 0.5f).toInt()
            fun text(label: String, font: Int) = android.widget.TextView(context).apply {
                id = android.view.View.generateViewId()
                text = label
                typeface = androidx.core.content.res.ResourcesCompat.getFont(context, font)
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f)
            }
            val regular = br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular
            val semiBold = br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold
            val nativeTitle = text("Combo especial", semiBold)
            val nativeCount = text("2 apostas", regular)
            val nativePrice = text("R$ 6,00", regular)
            val delete = android.view.View(context).apply { id = android.view.View.generateViewId() }
            val native = androidx.constraintlayout.widget.ConstraintLayout(context)
            native.addView(
                nativeTitle,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(0, -2).apply {
                    startToStart = 0
                    endToStart = delete.id
                    topToTop = 0
                    marginEnd = dp(12)
                }
            )
            native.addView(
                delete,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(dp(32), dp(32)).apply {
                    endToEnd = 0
                    topToTop = nativeTitle.id
                    bottomToBottom = nativeTitle.id
                }
            )
            native.addView(
                nativePrice,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(-2, -2).apply {
                    endToEnd = 0
                    topToBottom = delete.id
                }
            )
            native.addView(
                nativeCount,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(-2, -2).apply {
                    startToStart = 0
                    topToBottom = nativeTitle.id
                    topMargin = dp(6)
                    bottomToBottom = nativePrice.id
                }
            )
            native.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED)
            )
            native.layout(0, 0, native.measuredWidth, native.measuredHeight)
            nativeGap = nativeCount.top - nativeTitle.bottom
        }
        assertEquals("Quantity follows both native title and amount constraints", nativeGap.toFloat(), count.top - title.bottom, 0f)
    }

    @Test fun poolReservationUsesInstalledGreyTextColor() {
        show(pool())
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("cota reservada nº 1 de 10")
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(Color(0xFF404B52), layouts.single().layoutInput.style.color)
    }

    @Test fun poolDetailUsesInstalledBoldFont() {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) { CurrentCartRow(pool(), Instant.EPOCH, {}, {}) }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("mais detalhes", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(FontWeight.Bold, layouts.single().layoutInput.style.fontWeight)
    }

    @Test fun sportsCartDoesNotAppendMatchRowsMissingFromInstalledCart() {
        show(
            CartItem(
                4,
                CartItemKind.INDIVIDUAL_BET,
                "Loteca",
                "",
                BigDecimal("6.00"),
                modalityCode = "LOTECA",
                contestNumber = 1230,
                gameRows = listOf("1. Clube A x Clube B · 1", "2. Clube C x Clube D · X")
            )
        )
        compose.onNodeWithText("loteca").assertIsDisplayed()
        compose.onNodeWithText("Conc. 1230").assertIsDisplayed()
        compose.onNodeWithText("R$ 6,00").assertIsDisplayed()
        compose.onNodeWithText("1. Clube A x Clube B · 1").assertDoesNotExist()
        compose.onNodeWithText("2. Clube C x Clube D · X").assertDoesNotExist()
    }

    @Test fun individualDeleteArtworkKeepsNativeIntrinsicSizeWithoutShrinkingToPaddingRemainder() {
        show(CartItem(5, CartItemKind.INDIVIDUAL_BET, "Dia de Sorte", "", BigDecimal.ONE, modalityCode = "DIA_DE_SORTE"))
        assertDeleteArtwork(20)
    }

    @Test fun comboDeleteArtworkUsesNativeSevenDpPadding() {
        show(CartItem(6, CartItemKind.COMBO, "Combo", "", BigDecimal.ONE, comboBetCount = 2))
        assertDeleteArtwork(18, 0x0066B3)
    }

    @Test fun poolDeleteArtworkFillsNativeTwentyFourDpImage() {
        show(pool())
        assertDeleteArtwork(24, 0x0066B3)
    }

    @Test fun detailChevronUsesInstalledNarrowArtwork() {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Box(Modifier.background(Color.White)) { CurrentCartRow(pool(), Instant.EPOCH, {}, {}) }
            }
        }
        val image = compose.onNodeWithText("mais detalhes").captureToImage().toPixelMap()
        val iconPx = (16f * compose.density.density).roundToInt()
        val actual = blueBounds(image, image.width - iconPx)
        val native = nativeBounds(
            "M6.51,4.024L5.564,4.97L8.636,8.049L5.564,11.128L6.51,12.073L10.534,8.049L6.51,4.024Z",
            17f,
            iconPx,
            0x005CA9
        )
        assertEquals("Chevron width matches native antialiased path", native.first.toFloat(), actual.first.toFloat(), 1f)
        assertEquals("Chevron height matches native antialiased path", native.second.toFloat(), actual.second.toFloat(), 1f)
    }

    private fun assertDeleteArtwork(drawableDp: Int, tint: Int = 0x006BB6) {
        // Captura a arte interna; os limites mesclados de IconButton incluem sua área de toque expandida.
        val image = compose.onNodeWithContentDescription("Excluir", useUnmergedTree = true).captureToImage().toPixelMap()
        val sizePx = (drawableDp * compose.density.density).roundToInt()
        assertEquals("Native drawable box width", sizePx.toFloat(), image.width.toFloat(), 1f)
        val actual = blueBounds(image)
        val native = nativeBounds(
            "M13.333,7.5V15.833H6.667V7.5H13.333ZM12.083,2.5H7.917L7.083,3.333H4.167V5H15.833V3.333H12.917L12.083,2.5ZM15,5.833H5V15.833C5,16.75 5.75,17.5 6.667,17.5H13.333C14.25,17.5 15,16.75 15,15.833V5.833Z",
            20f,
            sizePx,
            tint
        )
        assertEquals("Trash width matches native antialiased path", native.first.toFloat(), actual.first.toFloat(), 1f)
        assertEquals("Trash height matches native antialiased path", native.second.toFloat(), actual.second.toFloat(), 1f)
    }

    private fun nativeBounds(path: String, viewport: Float, sizePx: Int, tint: Int): Pair<Int, Int> {
        val bitmap = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
        android.graphics.Canvas(bitmap).apply {
            drawColor(android.graphics.Color.WHITE)
            scale(sizePx / viewport, sizePx / viewport)
            drawPath(
                androidx.core.graphics.PathParser.createPathFromPathData(path)!!,
                android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF000000.toInt() or tint }
            )
        }
        return blueBounds(bitmap.asImageBitmap().toPixelMap())
    }

    private fun blueBounds(image: PixelMap, startX: Int = 0): Pair<Int, Int> {
        val pixels = buildList {
            for (x in startX.coerceAtLeast(0) until image.width) {
                for (y in 0 until image.height) {
                    val pixel = image[x, y]
                    // Inclui bordas azuis suavizadas sobre branco sem contar pixels totalmente transparentes.
                    if (pixel.alpha > 0.1f && pixel.blue - pixel.red > 0.1f && pixel.green - pixel.red > 0.04f) add(x to y)
                }
            }
        }
        assertTrue("Native blue artwork must be visible", pixels.isNotEmpty())
        return (pixels.maxOf { it.first } - pixels.minOf { it.first } + 1) to
            (pixels.maxOf { it.second } - pixels.minOf { it.second } + 1)
    }

    private fun pool() = CartItem(
        3,
        CartItemKind.POOL_SHARE,
        "Mega-Sena • Bolão 25",
        "",
        BigDecimal("5.00"),
        modalityCode = "MEGA_SENA",
        contestNumber = 3070,
        poolShare = CartPoolShare("25", 1, 10, "1970-01-01T00:05:00Z")
    )

    private fun show(item: CartItem) {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Box(Modifier.background(Color.White)) { CurrentCartRow(item, Instant.EPOCH, {}, null) }
            }
        }
    }
}
