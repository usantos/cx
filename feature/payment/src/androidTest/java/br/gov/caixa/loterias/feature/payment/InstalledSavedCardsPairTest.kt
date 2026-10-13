package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.cart.*
import br.gov.caixa.loterias.core.domain.payment.*
import java.time.Clock
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** One Mercado Pago card, unknown-brand fallback; all card fields are invented. */
class InstalledSavedCardsPairTest {
    @get:Rule val compose = createComposeRule()
    private val card = SavedPaymentCard("CONTROLLED-SAVED-CARD", 1, "000000", "0000", 3, 2030,
        3, "CONTROLLED-UNKNOWN", "CONTROLLED")

    @Test fun installedSingleCardCaptureNeverDeletesOrSubmits() {
        show()
        compose.onNodeWithText("Cartões cadastrados").assertIsDisplayed()
        compose.onNodeWithText("**** **** ****  0000").assertIsDisplayed()
        compose.onNodeWithText("Venc.:3/2030").assertIsDisplayed()
        compose.onNodeWithContentDescription("CONTROLLED-UNKNOWN").assertIsDisplayed()
        capture()
    }

    @Test fun deleteCanBeCancelledWithoutChangingTheSavedCard() {
        show()
        compose.onNodeWithContentDescription("Remover cartão").performClick()
        compose.onNodeWithText("Excluir cartão?").assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText("Excluir cartão?").assertDoesNotExist()
        compose.onNodeWithText("**** **** ****  0000").assertIsDisplayed()
    }

    @Test fun fallbackBrandRetainsPackedSeventyByFiftySixDpSlot() {
        show()
        val mark = compose.onNodeWithContentDescription("CONTROLLED-UNKNOWN", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val density = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .targetContext.resources.displayMetrics.density
        assertEquals("Packed row_meu_cartao brand width", 70f * density, mark.width, 1f)
        assertEquals("Packed row_meu_cartao brand height", 56f * density, mark.height, 1f)
    }

    private fun show() {
        compose.setContent {
            val model = remember {
                PaymentViewModel(mode = PaymentMode.CARDS,
                    getMethods = GetPaymentMethodsUseCase { AppResult.Success(listOf(PaymentMethod(1, "Mercado Pago"))) },
                    getCards = GetSavedCardsUseCase { check(it == 1L); AppResult.Success(listOf(card)) },
                    deleteCard = DeleteSavedCardUseCase { _, _ -> error("No saved-card deletion authorized") },
                    generatePixCharge = GeneratePixChargeUseCase { error("No financial submission authorized") },
                    getPurchaseProgress = GetPurchaseProgressUseCase { error("No financial polling authorized") },
                    validateDam = ValidateDamUseCase { error("No purchase validation authorized") },
                    getCurrentCart = GetCurrentCartUseCase { error("No cart access required") },
                    recordRecentPurchase = RecordRecentPurchaseUseCase { error("No purchase completion authorized") },
                    clock = Clock.systemUTC(), dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate))
            }
            CaixaDSTheme(darkTheme = false) {
                PaymentDataRoute(PaymentMode.CARDS, model, onBack = {}, onHelp = {}, onPix = {},
                    onAddCard = { error("No card creation authorized") }, onOpenCard = {}, onApproved = {},
                    onHome = {}, onPurchases = {}, onRedirect = {}, onRegistration = {})
            }
        }
    }

    private fun capture() {
        compose.waitForIdle()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val directory = java.io.File(requireNotNull(instrumentation.targetContext.externalCacheDir), "codex-financial-parity").apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        java.io.File(directory, "saved-cards-installed-single.png").outputStream().use {
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
        java.io.File(directory, "saved-cards-installed-single.txt").writeText(
            "fixture=true\nnetworkSends=0\ndeletions=0\nidentifiers=synthetic\nexcludeCardNumberAndExpiryFromRasterComparison=true\n")
    }
}
