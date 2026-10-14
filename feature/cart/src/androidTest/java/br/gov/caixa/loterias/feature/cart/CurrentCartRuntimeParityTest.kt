package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Real CartViewModel and synthetic contracts, matched against CX's current activity/row XML. */
class CurrentCartRuntimeParityTest {
    @get:Rule val compose = createComposeRule()
    private val calls = mutableListOf<String>()
    private lateinit var capturedModel: CartViewModel
    private val mega = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega Sena", "", BigDecimal("6.00"),
        modalityCode = "MEGA_SENA", contestNumber = 3070, numberQuantity = 6, recurringDraws = 1,
        selectedNumbers = listOf(listOf(32, 10, 43, 51, 27, 18)))
    private val lotofacil = CartItem(2, CartItemKind.INDIVIDUAL_BET, "Lotofácil", "", BigDecimal("3.50"),
        modalityCode = "LOTOFACIL", contestNumber = 3804, numberQuantity = 15, recurringDraws = 1,
        selectedNumbers = listOf(listOf(2, 25, 11, 1, 16, 6, 15, 22, 20, 8, 24, 12, 19, 5, 13)))

    @Test fun currentCxIntroAndFixedFooterPreserveAmountCountAndCheckoutActions() {
        show(listOf(mega))
        compose.onNodeWithText("Confira suas apostas e siga para o pagamento").assertIsDisplayed()
        compose.onNodeWithText("O valor mínimo do carrinho é R$2,50").assertIsDisplayed()
        compose.runOnIdle { assertEquals(BigDecimal("30.00"), capturedModel.state.value.minimumCartAmount) }
        compose.onNodeWithText("Total do carrinho").assertIsDisplayed()
        compose.onNodeWithText("R$ 39,00").assertIsDisplayed()
        compose.onNodeWithText("1").assertIsDisplayed()
        compose.onNodeWithText("Valor atual do carrinho").assertDoesNotExist()
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val add = compose.onNodeWithText("Adicionar nova aposta").fetchSemanticsNode().boundsInRoot
        val pay = compose.onNodeWithText("Ir para pagamento").fetchSemanticsNode().boundsInRoot
        val density = compose.density.density
        assertEquals("CX add button height", 44f * density, add.height, 1f)
        assertEquals("CX payment button height", 44f * density, pay.height, 1f)
        assertEquals("CX buttons span the screen with 24dp side insets", root.width - 48f * density, add.width, 2f)
        assertEquals(add.width, pay.width, 1f)
        assertEquals("CX keeps a 12dp gap between the two 44dp buttons", 12f * density, pay.top - add.bottom, 2f)
        compose.onNodeWithText("Adicionar nova aposta").performClick()
        compose.onNodeWithText("Ir para pagamento").assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(listOf("home", "account", "location", "recent", "validate", "payment"), calls) }
    }

    @Test fun compactIndividualRowsKeepNumbersInSixNumberLinesAndContestBesideFirstLine() {
        val million = mega.copy(id = 3, title = "+Milionária", modalityCode = "MAIS_MILIONARIA", selectedClovers = listOf(2, 4))
        show(listOf(lotofacil, million))
        compose.onNodeWithText("Apostas Individuais").assertIsDisplayed()
        compose.onNodeWithText("lotofácil").assertIsDisplayed()
        val first = compose.onNodeWithText("02 - 25 - 11 - 01 - 16 - 06").fetchSemanticsNode().boundsInRoot
        val second = compose.onNodeWithText("15 - 22 - 20 - 08 - 24 - 12").fetchSemanticsNode().boundsInRoot
        compose.onNodeWithText("19 - 05 - 13").assertIsDisplayed()
        val contest = compose.onNodeWithText("Conc. 3804").fetchSemanticsNode().boundsInRoot
        val amount = compose.onNodeWithText("R$ 3,50").fetchSemanticsNode().boundsInRoot
        assertEquals("CX aligns the contest with the first number line", first.top, contest.top, 2f)
        assertTrue(contest.left >= first.right)
        assertTrue(second.top >= first.bottom - 1f)
        assertTrue(amount.top > contest.bottom)
        compose.onNodeWithText("Você apostou").assertDoesNotExist()
        compose.onNodeWithText("Para").assertDoesNotExist()
        compose.onAllNodesWithContentDescription("Excluir").onFirst().performClick()
        compose.onNodeWithText("Deseja excluir esta aposta do carrinho?").assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals(emptyList<String>(), calls) }
        compose.onNodeWithText("+milionária").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("trevos: 02 - 04").assertIsDisplayed()
    }

    @Test fun emptyCartKeepsZeroSummaryAndDisablesPaymentBeforeAnyCheckoutService() {
        show(emptyList(), total = BigDecimal.ZERO)
        compose.onNodeWithText("Carrinho vazio").assertIsDisplayed()
        compose.onNodeWithText("Total do carrinho").assertIsDisplayed()
        compose.onNodeWithText("R$ 0,00").assertIsDisplayed()
        compose.onNodeWithText("0").assertIsDisplayed()
        compose.onNodeWithText("Ir para pagamento").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Excluir todas as apostas").assertDoesNotExist()
        compose.runOnIdle { assertEquals(emptyList<String>(), calls) }
    }

    @Test fun cartToolbarUsesCurrentCxSolidBlueAndCompactDeleteIcon() {
        show(listOf(mega))
        val title = compose.onNodeWithText("Carrinho de apostas").fetchSemanticsNode().boundsInRoot
        val image = compose.onRoot().captureToImage().toPixelMap()
        val y = (title.top + 2f).toInt().coerceIn(0, image.height - 1)
        val left = image[(image.width * 0.28f).toInt(), y]
        val right = image[(image.width * 0.65f).toInt(), y]
        assertEquals("CX's new cart toolbar has a solid background", left, right)
        assertEquals(0x005CA9, left.toArgb() and 0xFFFFFF)
    }

    @Test fun superSevenSurpriseKeepsColumnStructureAndRecurringBadge() {
        show(listOf(mega.copy(modalityCode = "SUPER_7", title = "Super Sete", contestNumber = 900,
            badges = setOf(CartItemBadge.SURPRISE, CartItemBadge.RECURRING),
            selectedNumbers = emptyList())))
        compose.onNodeWithText("super sete").assertIsDisplayed()
        compose.onAllNodesWithText("XX").assertCountEquals(7)
        compose.onNodeWithText("Surpresinha").assertIsDisplayed()
        compose.onNodeWithText("Teimosinha").assertIsDisplayed()
        compose.onNodeWithText("Conc. 900").assertIsDisplayed()
    }

    @Test fun currentRowsPreserveAllBackendModalityLabelsAndColumnNumbers() {
        val codes = listOf("MEGA_SENA", "LOTOFACIL", "MAIS_MILIONARIA", "DUPLA_SENA", "DIA_DE_SORTE",
            "SUPER_7", "QUINA", "LOTOMANIA", "TIMEMANIA", "LOTECA", "CUSTOM")
        val labels = listOf("mega-sena", "lotofácil", "+milionária", "dupla sena", "dia de sorte",
            "super sete", "quina", "lotomania", "timemania", "loteca", "concurso especial")
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    codes.forEach { code ->
                        CurrentCartRow(mega.copy(title = if (code == "CUSTOM") "Concurso especial" else code,
                            modalityCode = code, selectedNumbers = if (code == "SUPER_7") List(7) { listOf(it) }
                                else mega.selectedNumbers,
                            complementaryLabel = "Complemento $code", gameRows = listOf("Detalhe $code")),
                            Instant.parse("2026-10-14T12:00:00Z"), {}, null)
                    }
                }
            }
        }
        labels.forEach { compose.onNodeWithText(it).performScrollTo().assertIsDisplayed() }
        compose.onNodeWithText("Detalhe CUSTOM").performScrollTo().assertIsDisplayed()
    }

    @Test fun sixNumbersRecurringIndicatorSharesNativePriceLine() {
        show(listOf(mega.copy(badges = setOf(CartItemBadge.RECURRING))))
        val badge = compose.onNodeWithText("Teimosinha").fetchSemanticsNode().boundsInRoot
        val price = compose.onNodeWithText("R$ 6,00").fetchSemanticsNode().boundsInRoot
        assertEquals("CX flow is centered on tvValor for six manual numbers", price.center.y, badge.center.y, 3f)
        assertTrue("Indicator stays left of price", badge.right < price.left)
    }

    @Test fun cloversFollowNumbersAndSharePriceBaseline() {
        show(listOf(mega.copy(modalityCode = "MAIS_MILIONARIA", selectedClovers = listOf(2, 4))))
        val numbers = compose.onNodeWithText("32 - 10 - 43 - 51 - 27 - 18").fetchSemanticsNode()
        val extra = compose.onNodeWithText("trevos: 02 - 04").fetchSemanticsNode()
        val price = compose.onNodeWithText("R$ 6,00").fetchSemanticsNode()
        assertEquals("Native extra starts immediately after numeric content", numbers.boundsInRoot.bottom, extra.boundsInRoot.top, 2f)
        val extraLayout = mutableListOf<TextLayoutResult>()
        val priceLayout = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("trevos: 02 - 04").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(extraLayout) }
        compose.onNodeWithText("R$ 6,00").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(priceLayout) }
        assertEquals("tvValor baselineToBaseline tvExtra", extra.boundsInRoot.top + extraLayout.single().firstBaseline,
            price.boundsInRoot.top + priceLayout.single().firstBaseline, 2f)
    }

    @Test fun guestToolbarDeleteUsesNativeEndInsetWithoutFavoriteSlot() {
        show(listOf(mega), authenticated = false)
        compose.onNodeWithContentDescription("Salvar carrinho favorito").assertDoesNotExist()
        val action = compose.onNodeWithContentDescription("Excluir todas as apostas").fetchSemanticsNode().boundsInRoot
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        assertEquals("CX 44dp action center with 16dp end margin", 38f * compose.density.density, root.right - action.center.x, 2f)
    }

    @Test fun manualMultipleLinesPutIndicatorsImmediatelyAfterNumbers() {
        show(listOf(lotofacil.copy(badges = setOf(CartItemBadge.MIRROR, CartItemBadge.RECURRING))))
        val last = compose.onNodeWithText("19 - 05 - 13").fetchSemanticsNode().boundsInRoot
        val indicator = compose.onNodeWithText("Espelho").fetchSemanticsNode().boundsInRoot
        assertEquals("CX indicators topToBottom containerNumeros without margin", last.bottom, indicator.top, 2f)
        assertEquals("Native 14dp Mirror drawable plus 6dp drawablePadding", 20f * compose.density.density, indicator.left - last.left, 2f)
    }

    @Test fun surpriseManyNumbersKeepsIndicatorsOnPriceLine() {
        show(listOf(lotofacil.copy(badges = setOf(CartItemBadge.SURPRISE, CartItemBadge.RECURRING))))
        val indicator = compose.onNodeWithText("Surpresinha").fetchSemanticsNode().boundsInRoot
        val price = compose.onNodeWithText("R$ 3,50").fetchSemanticsNode().boundsInRoot
        assertEquals("Surprise overrides manual quantity branch", price.center.y, indicator.center.y, 3f)
    }

    @Test fun extraIndicatorsAreBelowExtraAndSuperSevenIndicatorsBelowColumns() {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Column {
                    CurrentCartRow(mega.copy(selectedClovers = listOf(1, 2), modalityCode = "MAIS_MILIONARIA",
                        badges = setOf(CartItemBadge.MIRROR)), Instant.EPOCH, {}, null)
                    CurrentCartRow(mega.copy(modalityCode = "SUPER_7", selectedNumbers = List(7) { listOf(9) },
                        badges = setOf(CartItemBadge.RECURRING)), Instant.EPOCH, {}, null)
                }
            }
        }
        val extra = compose.onNodeWithText("trevos: 01 - 02").fetchSemanticsNode().boundsInRoot
        val mirror = compose.onNodeWithText("Espelho").fetchSemanticsNode().boundsInRoot
        assertEquals("CX indicators directly below extra", extra.bottom, mirror.top, 2f)
        val lastNumber = compose.onAllNodesWithText("9").onLast().fetchSemanticsNode().boundsInRoot
        val recurring = compose.onNodeWithText("Teimosinha").fetchSemanticsNode().boundsInRoot
        assertEquals("CX Super Sete indicators directly below numeric columns", lastNumber.bottom, recurring.top, 2f)
    }

    @Test fun authenticatedToolbarKeepsTwoNativeActions() {
        show(listOf(mega))
        val delete = compose.onNodeWithContentDescription("Excluir todas as apostas").fetchSemanticsNode().boundsInRoot
        val favorite = compose.onNodeWithContentDescription("Salvar carrinho favorito").fetchSemanticsNode().boundsInRoot
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        assertEquals(44f * compose.density.density, favorite.center.x - delete.center.x, 2f)
        assertEquals(38f * compose.density.density, root.right - favorite.center.x, 2f)
    }

    @Test fun nativeExtraUsesGuidelineWidthWithoutNumericEndMargin() {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                CurrentCartRow(mega.copy(modalityCode = "TIMEMANIA",
                    complementaryLabel = "Time do coração: associação esportiva de um nome longo/mg"), Instant.EPOCH, {}, null)
            }
        }
        val extra = compose.onNodeWithText("time do coração: associação esportiva de um nome longo/mg").fetchSemanticsNode().boundsInRoot
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        assertEquals("tvExtra ends at the 75 percent guideline; only containerNumeros has an 8dp end margin",
            root.width * 0.75f, extra.width, 2f)
    }

    @Test fun indicatorsUseNativeActivityFuturaBoldTextMetrics() {
        val labels = listOf("Espelho", "Surpresinha", "Teimosinha")
        val badges = listOf(CartItemBadge.MIRROR, CartItemBadge.SURPRISE, CartItemBadge.RECURRING)
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Column {
                    badges.forEach { badge -> CurrentCartRow(mega.copy(badges = setOf(badge)), Instant.EPOCH, {}, null) }
                }
            }
        }
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val fontResource = br.gov.caixa.loterias.core.designsystem.R.font.futura_std_bold
        val fontHash = context.resources.openRawResource(fontResource).use { stream ->
            java.security.MessageDigest.getInstance("SHA-256").digest(stream.readBytes()).joinToString("") { "%02x".format(it) }
        }
        // Independent CX asset hash, checked against its read-only AppTheme/font_family_futura source.
        assertEquals("215c175ce595d0f42bc1f703ef94ecbf6f45dc875cdbfd7ea59d9d8b28467cf9", fontHash)
        labels.forEach { label ->
            var nativeWidth = 0
            compose.runOnIdle {
                val native = android.widget.TextView(context).apply {
                    text = label
                    typeface = androidx.core.content.res.ResourcesCompat.getFont(context, fontResource)
                    setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f)
                    setPadding(0, 0, 0, 0)
                    measure(android.view.View.MeasureSpec.UNSPECIFIED, android.view.View.MeasureSpec.UNSPECIFIED)
                }
                nativeWidth = native.measuredWidth
            }
            val rendered = compose.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
            assertEquals("$label must inherit CX AppTheme's 700-weight Futura font metrics", nativeWidth.toFloat(), rendered.width, 1f)
        }
    }

    @Test fun recurringIndicatorMatchesNativeConstraintLayoutCenterRounding() {
        show(listOf(mega.copy(badges = setOf(CartItemBadge.RECURRING))))
        val price = compose.onNodeWithText("R$ 6,00").fetchSemanticsNode().boundsInRoot
        val badge = compose.onNodeWithText("Teimosinha").fetchSemanticsNode().boundsInRoot
        var nativeOffset = 0
        compose.runOnIdle {
            val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
            val native = androidx.constraintlayout.widget.ConstraintLayout(context)
            val priceView = android.widget.TextView(context).apply {
                id = android.view.View.generateViewId()
                text = "R$ 6,00"
                typeface = androidx.core.content.res.ResourcesCompat.getFont(context,
                    br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular)
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f)
                includeFontPadding = false
            }
            val badgeView = android.widget.TextView(context).apply {
                text = "Teimosinha"
                typeface = androidx.core.content.res.ResourcesCompat.getFont(context,
                    br.gov.caixa.loterias.core.designsystem.R.font.futura_std_bold)
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14f)
            }
            native.addView(priceView, androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(-2, -2).apply {
                topToTop = 0
                endToEnd = 0
                topMargin = 100
            })
            native.addView(badgeView, androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(-2, -2).apply {
                startToStart = 0
                topToTop = priceView.id
                bottomToBottom = priceView.id
            })
            native.measure(android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(1000, android.view.View.MeasureSpec.EXACTLY))
            native.layout(0, 0, 1000, 1000)
            assertEquals("Price uses the same native font metrics", price.height, priceView.height.toFloat(), 0f)
            assertEquals("Indicator uses the same native font metrics", badge.height, badgeView.height.toFloat(), 0f)
            nativeOffset = badgeView.top - priceView.top
        }
        assertEquals("CX top/bottom constraints center the indicator with native pixel rounding",
            nativeOffset.toFloat(), badge.top - price.top, 0f)
    }

    private fun show(items: List<CartItem>, total: BigDecimal = BigDecimal("39.00"), authenticated: Boolean = true) {
        compose.setContent {
            val model = remember {
                CartViewModel(
                    getCurrentCart = { AppResult.Success(ShoppingCart(1, total, items, totalBetCount = 9)) },
                    removeCartItem = { error("Removal needs explicit confirmation") },
                    clearCart = { error("Unused") },
                    serverTime = ServerTimeSource { Instant.parse("2026-10-14T12:00:00Z") },
                    verifyPaymentAccount = { calls += "account"; AppResult.Success(CartPaymentAccountDecision.Allowed) },
                    validateCart = { calls += "validate"; AppResult.Success(CartValidationResult.Accepted) },
                    hasRecentPurchase = { calls += "recent"; AppResult.Success(false) },
                    checkCheckoutLocation = { calls += "location"; AppResult.Success(CheckoutLocationStatus.IN_BRAZIL) },
                    recordLocationRequest = { error("Unused") },
                    getBettingParameters = GetBettingParametersUseCase { AppResult.Success(BettingParameters(
                        BigDecimal("500"), BigDecimal("30.00"), 50,
                        listOf(LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
                            3070, "14/10/2026", 60, 6, 20, 15, emptyList(), emptyList())))) },
                    authenticated = authenticated,
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                ).also { capturedModel = it }
            }
            CaixaDSTheme(darkTheme = false) {
                CurrentCartRoute(model, onBack = {}, onContinueBetting = { calls += "home" },
                    onPayment = { calls += "payment" }, onLogin = {}, onAccountRequired = {},
                    onLocationBlocked = {}, onValidationRedirect = {})
            }
        }
    }
}
