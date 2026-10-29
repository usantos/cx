package br.gov.caixa.loterias.feature.tickets

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseDetails
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.route.PurchaseDetailDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Par de exibição só para leitura: sorteio/números públicos coincidem com a tela instalada; todos os IDs são sintéticos. */
class InstalledPurchasePairTest {
    @get:Rule val compose = createComposeRule()
    private val transaction = "CONTROLLED".padEnd(32, '0')
    private val purchase = Purchase(
        9010, "05/10/2026", "Finalizada - Todas as apostas efetivadas", "Pix",
        BigDecimal("77.00"), statusId = 8, paymentMethodId = 6,
        finalizedTime = "19:51:43", endToEndId = transaction
    )
    private val bets = listOf(
        listOf(1, 3, 6, 7, 8, 10, 11, 14, 15, 18, 19, 20, 23, 24, 25),
        listOf(1, 2, 3, 9, 11, 12, 13, 15, 16, 17, 19, 20, 21, 23, 25)
    ).mapIndexed { index, numbers ->
        ConfirmedBet(
            9011L + index,
            "Lotofácil",
            3797,
            "05/10/2026",
            "Efetivada",
            numbers,
            modalityId = 8,
            statusId = 4
        )
    }

    @Test fun finalizedPairCaptureNeverQueriesOrMutates() {
        show()
        compose.onNodeWithText("Situação: ${purchase.status}").assertIsDisplayed()
        // NumberFormat usa um separador de moeda não separável no Android.
        compose.onNodeWithText("77,00", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Verificar Pagamento").assertDoesNotExist()
        capture()
    }

    @Test fun installedHeaderKeepsPaymentIdentifierBeforePaymentMethod() {
        show()
        val identifier = compose.onNodeWithText("ID do Pagamento:\n$transaction").fetchSemanticsNode().boundsInRoot
        val method = compose.onNodeWithText("Meio de pagamento: Pix").fetchSemanticsNode().boundsInRoot
        assertTrue("Installed header places payment ID before payment method", identifier.bottom < method.top)
        compose.onNodeWithText("Total:").assertIsDisplayed()
    }

    @Test fun installedBetRowsRetainSeparateLabelsAndUnpaddedNumberMarks() {
        show()
        compose.onAllNodesWithText("Seus números").onFirst().assertIsDisplayed()
        compose.onAllNodesWithText("Conc. 3797").onFirst().assertIsDisplayed()
        compose.onAllNodesWithText("1").onFirst().assertIsDisplayed()
        compose.onAllNodesWithText("Efetivada").onFirst().assertIsDisplayed()
    }

    @Test fun installedPaymentMethodDoesNotAddAnUnconstrainedHeaderBottomMargin() {
        show()
        val identifier = compose.onNodeWithText("ID do Pagamento:\n$transaction").fetchSemanticsNode().boundsInRoot
        // A semântica de texto exclui o preenchimento. compra_processada_layout contribui com
        // paddingTop8 + marginTop8; o grupo ancorado só no topo não adiciona margem inferior.
        val method = compose.onNodeWithText("Meio de pagamento: Pix").fetchSemanticsNode().boundsInRoot
        val density = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .targetContext.resources.displayMetrics.density
        assertEquals("Only the installed 16dp payment-method spacing", identifier.bottom + 16f * density, method.top, 1f)
    }

    @Test fun installedPhysicalPairStartsFirstPurpleRowAtRecordedScreenCoordinate() {
        show()
        compose.waitForIdle()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val metrics = instrumentation.targetContext.resources.displayMetrics
        // Esta imagem raster de teste medida pertence à captura XCover de produção,
        // registrada de forma independente em 1080x2408 e 450dpi; outros perfis usam a
        // asserção portátil de margem do cabeçalho acima.
        org.junit.Assume.assumeTrue(metrics.densityDpi == 450)
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        try {
            org.junit.Assume.assumeTrue(bitmap.width == 1080 && bitmap.height == 2408)
            val purple = android.graphics.Color.rgb(145, 39, 143)
            val firstPurpleY = (0 until bitmap.height).firstOrNull { bitmap.getPixel(0, it) == purple }
            assertEquals("Installed finalized reference first purple row; payment ID value excluded", 720, firstPurpleY)
        } finally {
            bitmap.recycle()
        }
    }

    @Test fun installedPhysicalPairKeepsFavoriteAtNativeWeightedRowPosition() {
        show()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").assertIsDisplayed()
        compose.waitForIdle()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        org.junit.Assume.assumeTrue(instrumentation.targetContext.resources.displayMetrics.densityDpi == 450)
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        try {
            org.junit.Assume.assumeTrue(bitmap.width == 1080 && bitmap.height == 2408)
            // Aceita tanto a tonalidade instalada quanto a candidata anterior à correção para que
            // esta asserção isole o posicionamento da correção independente de tonalidade.
            val starColors = setOf(android.graphics.Color.rgb(57, 100, 164), android.graphics.Color.rgb(0, 92, 169))
            var left = Int.MAX_VALUE
            var right = Int.MIN_VALUE
            for (y in 250 until 400) {
                for (x in 700 until 950) {
                    if (bitmap.getPixel(x, y) in starColors) {
                        left = minOf(left, x)
                        right = maxOf(right, x)
                    }
                }
            }
            assertTrue("Favorite solid-color pixels exist in the header", left != Int.MAX_VALUE)
            assertEquals("Installed star bbox753..814: native weighted header row center", 783.5f, (left + right) / 2f, 1f)
        } finally {
            bitmap.recycle()
        }
    }

    @Test fun installedAmountUsesNativeWrapContentFontMetrics() {
        show()
        val amount = compose.onNodeWithText("77,00", substring = true).fetchSemanticsNode().boundsInRoot
        var nativeHeight = 0
        compose.runOnIdle {
            val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
            val view = android.widget.TextView(context).apply {
                typeface = androidx.core.content.res.ResourcesCompat.getFont(
                    context,
                    br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular
                )
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14.199982f)
                includeFontPadding = true
                text = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("pt-BR")).format(purchase.total)
            }
            val unspecified = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED)
            view.measure(unspecified, unspecified)
            nativeHeight = view.measuredHeight
        }
        assertEquals("Packed 14.199982sp amount TextView wrapContent height", nativeHeight.toFloat(), amount.height, 0f)
    }

    @Test fun installedDataRowUsesNativeBaselineAlignedLinearLayoutHeight() {
        show()
        val date = compose.onNodeWithText("Data: ${purchase.date}").fetchSemanticsNode().boundsInRoot
        val hour = compose.onNodeWithText("Hora: ${purchase.finalizedTime}").fetchSemanticsNode().boundsInRoot
        var nativeRowHeight = 0
        compose.runOnIdle {
            val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            val context = instrumentation.targetContext
            val originalContext = context.createPackageContext("br.gov.caixa.loterias.apostas.cxv2validation", 0)
            val activityInfo = context.packageManager.getActivityInfo(
                android.content.ComponentName(
                    originalContext.packageName,
                    "br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity"
                ),
                0
            )
            val themed = android.view.ContextThemeWrapper(originalContext, activityInfo.themeResource)
            val font = originalContext.resources.getFont(
                originalContext.resources.getIdentifier("caixa_std_regular", "font", originalContext.packageName)
            )
            val density = context.resources.displayMetrics.density
            val nativeDate = android.widget.TextView(themed).apply {
                typeface = font
                text = "Data: ${purchase.date}"
            }
            val nativeAmount = android.widget.TextView(themed).apply {
                typeface = font
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14.199982f)
                text = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("pt-BR")).format(purchase.total)
            }
            val row = android.widget.LinearLayout(themed).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                addView(nativeDate, android.widget.LinearLayout.LayoutParams(-2, -2, .8f))
                addView(nativeAmount, android.widget.LinearLayout.LayoutParams((80f * density + .5f).toInt(), -2, .2f))
            }
            val outer = android.widget.LinearLayout(themed).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                addView(row, android.widget.LinearLayout.LayoutParams(-1, -1))
            }
            val width = (context.resources.displayMetrics.widthPixels - 8f * density - 25f * density + .5f).toInt()
            outer.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(width, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED)
            )
            outer.layout(0, 0, outer.measuredWidth, outer.measuredHeight)
            nativeRowHeight = row.measuredHeight
            val directory = java.io.File(requireNotNull(context.externalCacheDir), "codex-financial-parity").apply { mkdirs() }
            java.io.File(directory, "purchase-native-data-row-metrics.txt").writeText(
                "nativeRowHeight=$nativeRowHeight\nnativeDateHeight=${nativeDate.measuredHeight}\nnativeDateTop=${nativeDate.top}\n" +
                    "nativeDateTextSizePx=${nativeDate.textSize}\nnativeAmountHeight=${nativeAmount.measuredHeight}\n" +
                    "nativeAmountTop=${nativeAmount.top}\ncomposeDateHeight=${date.height}\ncomposeRowHeight=${hour.top - date.top}\n"
            )
        }
        assertEquals("Packed themed .8/.2 baseline-aligned data row height", nativeRowHeight.toFloat(), hour.top - date.top, 0f)
    }

    @Test fun installedMixedTypefaceStatusUsesNativeMultilineWrapContentHeight() {
        show()
        val node = compose.onNodeWithText("Situação: ${purchase.status}")
        val statusBounds = node.fetchSemanticsNode().boundsInRoot
        val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        node.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult) { it(layouts) }
        var nativeHeight = 0
        compose.runOnIdle {
            val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
            val original = context.createPackageContext("br.gov.caixa.loterias.apostas.cxv2validation", 0)
            val info = context.packageManager.getActivityInfo(
                android.content.ComponentName(original.packageName, "br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity"),
                0
            )
            val themed = android.view.ContextThemeWrapper(original, info.themeResource)
            fun font(name: String) = original.resources.getFont(original.resources.getIdentifier(name, "font", original.packageName))
            val regular = font("caixa_std_regular")
            val bold = font("caixa_std_bold")
            // A string empacotada Situação: _%s_, ViewUtils.textCaixaSTDBold e
            // CustomTypefaceSpan aplicam fontes reais separadas a esses trechos.
            val prefix = "Situação: "
            val text = android.text.SpannableStringBuilder(prefix + purchase.status).apply {
                setSpan(InstalledTypefaceSpan(regular), 0, prefix.length, android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                setSpan(InstalledTypefaceSpan(bold), prefix.length, length, android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            }
            val view = android.widget.TextView(themed).apply {
                typeface = regular
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14.199982f)
                maxWidth = (150f * original.resources.displayMetrics.density + .5f).toInt()
                this.text = text
            }
            view.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(statusBounds.width.toInt(), android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED)
            )
            nativeHeight = view.measuredHeight
            val directory = java.io.File(requireNotNull(context.externalCacheDir), "codex-financial-parity").apply { mkdirs() }
            java.io.File(directory, "purchase-native-status-metrics.txt").writeText(
                "nativeHeight=$nativeHeight\nnativeWidth=${view.measuredWidth}\nnativeLines=${view.lineCount}\n" +
                    "nativeLineHeight=${view.lineHeight}\nnativeBaseline=${view.baseline}\n" +
                    "nativeTextSizePx=${view.textSize}\ncomposeHeight=${statusBounds.height}\n" +
                    "composeLines=${layouts.single().lineCount}\ncomposeLayoutHeight=${layouts.single().size.height}\n"
            )
        }
        assertEquals(
            "Packed regular/bold spans and 14.199982sp multiline status TextView height",
            nativeHeight.toFloat(),
            statusBounds.height,
            0f
        )
    }

    /** Comportamento original de desenho/medição de CustomTypefaceSpan, sem carregar código de produção. */
    private class InstalledTypefaceSpan(private val font: android.graphics.Typeface) : android.text.style.TypefaceSpan("") {
        override fun updateDrawState(paint: android.text.TextPaint) = applyFont(paint)
        override fun updateMeasureState(paint: android.text.TextPaint) = applyFont(paint)
        private fun applyFont(paint: android.text.TextPaint) {
            val fake = (paint.typeface?.style ?: 0) and font.style.inv()
            if (fake and android.graphics.Typeface.BOLD != 0) paint.isFakeBoldText = true
            if (fake and android.graphics.Typeface.ITALIC != 0) paint.textSkewX = -.25f
            paint.typeface = font
        }
    }

    private fun show() {
        compose.setContent {
            val vm = remember {
                TicketsViewModel(
                    getConfirmedBets = GetConfirmedBetsUseCase { AppResult.Success(emptyList()) },
                    getPurchases = GetPurchasesUseCase { AppResult.Success(emptyList()) },
                    getPurchaseBets = GetPurchaseBetsUseCase { AppResult.Success(bets) },
                    checkTicket = CheckTicketUseCase { error("No ticket lookup authorized") },
                    getPurchaseDetails = GetPurchaseDetailsUseCase { requestedPurchase ->
                        check(requestedPurchase.id == purchase.id)
                        AppResult.Success(PurchaseDetails(purchase, bets))
                    },
                    savePurchaseAsFavorite = SavePurchaseAsFavoriteUseCase { _, _, _ -> error("No mutation authorized") },
                    getPixPurchaseStatus = br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase {
                        error("No financial query authorized")
                    },
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
                )
            }
            CaixaDSTheme(darkTheme = false) {
                PurchaseDetailDataRoute(vm, purchase, onBack = {}, onOpen = { error("No navigation requested") })
            }
        }
    }

    private fun capture() {
        compose.waitForIdle()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val directory = java.io.File(requireNotNull(instrumentation.targetContext.externalCacheDir), "codex-financial-parity").apply {
            mkdirs()
        }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        java.io.File(directory, "purchase-installed-finalized.png").outputStream().use {
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
        java.io.File(
            directory,
            "purchase-installed-finalized.txt"
        ).writeText(
            "fixture=true\nnetworkSends=0\nqueries=0\nidentifiers=synthetic\nexcludePaymentIdentifierRegionFromRasterComparison=true\n"
        )
    }
}
