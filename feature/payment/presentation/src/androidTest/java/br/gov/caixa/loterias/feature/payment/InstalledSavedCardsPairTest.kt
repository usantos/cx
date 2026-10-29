package br.gov.caixa.loterias.feature.payment

import androidx.compose.ui.test.*
import org.junit.Assert.*
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import java.time.Clock
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentDataRoute
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentViewModel

/** Um cartão Mercado Pago, com alternativa para bandeira desconhecida; todos os campos do cartão são inventados. */
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

    @Test fun savedCardsInsetAccountsForEightDpProviderStripe() {
        show()
        val subtitle = compose.onNodeWithText("Confira aqui sua lista de cartões salvos no Mercado Pago").fetchSemanticsNode().boundsInRoot
        val number = compose.onNodeWithText("**** **** ****  0000", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val density = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .targetContext.resources.displayMetrics.density
        // fragment_meus_cartoes: subtítulo começa após a faixa de 8dp mais margem de 16dp.
        assertEquals(24f * density, subtitle.left, 1f)
        // Recycler começa após a faixa + margem de 8dp; a linha tem margem externa de 30dp/interna de 17dp.
        assertEquals(63f * density, number.left, 1f)
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
