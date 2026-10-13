package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPixPanel
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.cart.*
import br.gov.caixa.loterias.core.domain.payment.*
import java.math.BigDecimal
import java.time.Clock
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Entirely controlled use cases; no network, actual purchase, account or captured payload. */
class ControlledPixInteractionTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var vm: PaymentViewModel
    private val generations = AtomicInteger()
    private val queries = AtomicInteger()
    private val approvals = AtomicInteger()
    private val purchases = AtomicInteger()
    private val records = AtomicInteger()
    private val backs = AtomicInteger()
    private val homes = AtomicInteger()
    private lateinit var backDispatcher: androidx.activity.OnBackPressedDispatcher
    private val redirects = mutableListOf<PurchaseRedirect>()
    private var progress = PurchaseProgress(9001, 10)

    @Test fun singlePaymentFootnoteRetainsPackedAdditionalStartMargin() {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                CaixaDSLegacyPixPanel(9005, "CONTROLLED-MARGIN-DO-NOT-PAY", 900,
                    checking = false, onCheckPayment = { fail("No query expected") },
                    onCopied = { fail("No copy expected") }, onPurchases = { fail("No navigation expected") },
                    showIntro = false, showHeader = false, showRecoveryGuidance = false)
            }
        }
        val title = compose.onNodeWithText("Pix Copia e Cola").fetchSemanticsNode().boundsInRoot
        val footnote = compose.onNodeWithText("*O código poderá ser pago somente 1 vez.").fetchSemanticsNode().boundsInRoot
        val density = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .targetContext.resources.displayMetrics.density
        // Packed fragment_copia_cola_pix: the footnote starts at the code field plus 16dp.
        assertEquals(16f * density, footnote.left - title.left, 1f)
        assertEquals(title.right, footnote.right, 1f)
    }

    @Test fun expiredToolbarBackReturnsHomeWithoutAnotherCharge() {
        show(expired = true)
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, homes.get()); assertEquals(0, backs.get())
            assertEquals(1, generations.get()); assertEquals(0, queries.get())
        }
    }

    @Test fun pendingSystemBackRequiresStayOrHomeChoiceWithoutAnotherCharge() {
        show()
        compose.runOnIdle { backDispatcher.onBackPressed() }
        compose.onNodeWithText(PIX_PENDING_EXIT_MESSAGE).assertIsDisplayed()
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle { assertEquals(0, homes.get()); assertEquals(0, backs.get()) }
        compose.runOnIdle { backDispatcher.onBackPressed() }
        compose.onNodeWithText("Voltar ao início").performClick()
        compose.runOnIdle {
            assertEquals(1, homes.get()); assertEquals(0, backs.get())
            assertEquals(1, generations.get()); assertEquals(0, queries.get())
        }
    }

    @Test fun pendingQueryAndRestorationKeepTheSameChargeWithoutApprovingPayment() {
        show()
        compose.onNodeWithText("Verificar Pagamento").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(1, queries.get()); assertEquals(0, approvals.get()); assertEquals(0, records.get())
            vm.load(); vm.load()
        }
        compose.runOnIdle { assertEquals(1, generations.get()); assertEquals(9001L, vm.state.value.pix?.purchaseId) }
    }

    @Test fun remoteApprovalNavigatesOnceAndCannotResendChargeOnReload() {
        show()
        compose.runOnIdle { progress = PurchaseProgress(9001, 8) }
        compose.onNodeWithText("Verificar Pagamento").performScrollTo().performClick()
        compose.waitUntil { approvals.get() == 1 }
        compose.runOnIdle { vm.load(); vm.checkPixStatus() }
        compose.runOnIdle {
            assertEquals(1, generations.get()); assertEquals(1, queries.get())
            assertEquals(1, approvals.get()); assertEquals(1, records.get())
        }
    }

    @Test fun expiredChargeOffersPurchaseHistoryWithoutCopyOrStatusMutation() {
        show(expired = true)
        compose.onNodeWithText("Código Expirado").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Verificar Pagamento").assertDoesNotExist()
        compose.onNodeWithText("Ir para as\nMinhas Compras").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(1, purchases.get()); assertEquals(1, generations.get())
            assertEquals(0, queries.get()); assertEquals(0, approvals.get()); assertEquals(0, records.get())
        }
    }

    @Test fun purchaseRedirectDispatchesOnceAndReturnNeverPostsAnotherCharge() {
        show()
        compose.runOnIdle { progress = PurchaseProgress(9001, 0, redirect = PurchaseRedirect.CART) }
        compose.onNodeWithText("Verificar Pagamento").performScrollTo().performClick()
        compose.waitUntil { redirects.size == 1 }
        compose.runOnIdle { vm.resumeAfterRedirect(); vm.load() }
        compose.runOnIdle {
            assertEquals(listOf(PurchaseRedirect.CART), redirects)
            assertEquals(1, generations.get()); assertEquals(1, queries.get())
            assertEquals(0, approvals.get()); assertEquals(0, records.get())
        }
    }

    @Test fun copiedPixIsInvalidatedOnExpiryWithoutAnyPaymentOperation() {
        verifyExpiryClipboard(preserveUnrelated = false)
    }

    @Test fun expiryDoesNotEraseClipboardReplacedAfterCopyingPix() {
        verifyExpiryClipboard(preserveUnrelated = true)
    }

    private fun verifyExpiryClipboard(preserveUnrelated: Boolean) {
        val code = "CONTROLLED-CLIPBOARD-DO-NOT-PAY"
        val unrelated = "CONTROLLED-UNRELATED-TEXT"
        val remaining = mutableStateOf(30L)
        lateinit var clipboard: androidx.compose.ui.platform.ClipboardManager
        var copied = 0
        compose.setContent {
            clipboard = LocalClipboardManager.current
            CaixaDSTheme(darkTheme = false) {
                CaixaDSLegacyPixPanel(9004, code, remaining.value, checking = false,
                    onCheckPayment = { queries.incrementAndGet() }, onCopied = { copied++ },
                    onPurchases = { purchases.incrementAndGet() },
                    showIntro = false, showHeader = false, showRecoveryGuidance = false)
            }
        }
        compose.onNodeWithContentDescription("Copiar código Pix").performClick()
        compose.runOnIdle {
            assertEquals(1, copied)
            assertEquals(code, clipboard.getText()?.text)
            if (preserveUnrelated) clipboard.setText(AnnotatedString(unrelated))
            remaining.value = 0
        }
        compose.onNodeWithText("Código Expirado").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(if (preserveUnrelated) unrelated else "Código Expirado", clipboard.getText()?.text)
            assertEquals(0, generations.get()); assertEquals(0, queries.get()); assertEquals(0, records.get())
            assertEquals(0, approvals.get()); assertEquals(0, purchases.get())
        }
    }

    @Test fun capturePendingPixWithoutQueryingOrApproving() {
        show()
        compose.onNodeWithText("Pix Copia e Cola").assertIsDisplayed()
        capture("payment-pix-pending-top")
        compose.onNodeWithText("Verificar Pagamento").performScrollTo()
        capture("payment-pix-pending-actions")
        compose.runOnIdle { assertEquals(0, queries.get()); assertEquals(0, approvals.get()) }
    }

    @Test fun captureExpiredPixWithoutQueryingOrApproving() {
        show(expired = true)
        compose.onNodeWithText("Código Expirado").assertIsDisplayed()
        capture("payment-pix-expired-top")
        compose.onNodeWithText("Ir para as\nMinhas Compras").performScrollTo()
        capture("payment-pix-expired-actions")
        compose.runOnIdle { assertEquals(0, queries.get()); assertEquals(0, approvals.get()) }
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val folder = java.io.File(requireNotNull(instrumentation.targetContext.externalCacheDir), "codex-financial-parity").apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        java.io.File(folder, "$name.png").outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        java.io.File(folder, "$name.txt").writeText("fixture=true\nnetworkSends=0\nqueries=${queries.get()}\ndensityDpi=${instrumentation.targetContext.resources.displayMetrics.densityDpi}\nfontScale=${instrumentation.targetContext.resources.configuration.fontScale}\n")
        assertTrue(java.io.File(folder, "$name.png").length() > 0)
    }

    private fun show(expired: Boolean = false) {
        compose.setContent {
            backDispatcher = requireNotNull(androidx.activity.compose.LocalOnBackPressedDispatcherOwner.current)
                .onBackPressedDispatcher
            val model = remember {
                PaymentViewModel(
                    mode = PaymentMode.PIX,
                    getMethods = GetPaymentMethodsUseCase { AppResult.Success(emptyList()) },
                    getCards = GetSavedCardsUseCase { AppResult.Success(emptyList()) },
                    deleteCard = DeleteSavedCardUseCase { _, _ -> AppResult.Success(emptyList()) },
                    generatePixCharge = GeneratePixChargeUseCase {
                        generations.incrementAndGet()
                        AppResult.Success(PixCharge("CONTROLLED-CODE-9001-DO-NOT-PAY", "CONTROLLED-TX", "",
                            "2000-01-01T00:00:00Z", if (expired) "2000-01-01T00:00:01Z" else java.time.Instant.now().plusSeconds(900).toString(),
                            9001, BigDecimal.TEN))
                    },
                    getPurchaseProgress = GetPurchaseProgressUseCase { queries.incrementAndGet(); AppResult.Success(progress) },
                    validateDam = ValidateDamUseCase { AppResult.Success(DamValidation(false, "")) },
                    getCurrentCart = GetCurrentCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList())) },
                    recordRecentPurchase = RecordRecentPurchaseUseCase { records.incrementAndGet(); AppResult.Success(Unit) },
                    clock = Clock.systemUTC(), dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                ).also { vm = it }
            }
            CaixaDSTheme(darkTheme = false) {
                PaymentDataRoute(PaymentMode.PIX, model,
                    onBack = { backs.incrementAndGet() }, onHelp = {}, onPix = {}, onAddCard = {}, onOpenCard = {},
                    onApproved = { approvals.incrementAndGet() }, onHome = { homes.incrementAndGet() },
                    onPurchases = { purchases.incrementAndGet() }, onRedirect = { redirects.add(it) }, onRegistration = {})
            }
        }
    }
}
