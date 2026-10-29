package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentCardFlowRoute
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentTerminalRoute
import br.gov.caixa.loterias.feature.payment.presentation.screen.paymentScreens
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentCardViewModel
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentTerminalViewModel
import java.math.BigDecimal
import java.time.Clock
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Cartão salvo sintético equivalente ao DTO. Enviar um pagamento com cartão faz o teste falhar imediatamente. */
class ControlledCardRenderingTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var vm: PaymentCardViewModel

    @Test fun savedVisaPreviewCvvAndConfirmationNeverSubmit() {
        show()
        compose.onNodeWithText("Confirme os dados do cartão escolhido").assertIsDisplayed()
        capture("card-visa-preview")
        compose.onNodeWithText("Incluir Código de Verificação").performClick()
        compose.onNodeWithText("Insira o código de segurança do seu cartão de crédito").assertIsDisplayed()
        capture("card-visa-cvv-empty")
        compose.runOnIdle { vm.changeCvv("123") }
        capture("card-visa-cvv-filled")
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("Confirme os dados digitados").assertIsDisplayed()
        capture("card-visa-confirmation-top")
        compose.onNodeWithText("Apostar").performScrollTo().assertIsDisplayed()
        capture("card-visa-confirmation-action")
        compose.runOnIdle { assertEquals(PaymentCardStage.CONFIRM, vm.state.value.stage) }
    }

    @Test fun americanExpressHasFourCvvDigitsWithoutSubmitting() {
        show(americanExpress = true)
        compose.onNodeWithText("Incluir Código de Verificação").performClick()
        compose.runOnIdle {
            vm.changeCvv("1234")
            assertEquals(4, vm.state.value.cvvLength)
        }
        compose.onNodeWithText("Confirmar").assertIsEnabled()
        capture("card-amex-cvv-four-digits")
    }

    @Test fun capturePurchaseProcessingWithoutFinancialOperation() {
        compose.setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val model =
                remember { PaymentTerminalViewModel(paymentScreens(context::getString).first { it.route == "payment/confirmation" }) }
            CaixaDSTheme(darkTheme = false) {
                PaymentTerminalRoute(model, onBack = {}, onComplete = {}, onNavigate = {})
            }
        }
        compose.onNodeWithText("Acompanhe suas compras").assertIsDisplayed()
        capture("payment-processing")
    }

    private fun show(americanExpress: Boolean = false) {
        compose.setContent {
            val model = remember {
                PaymentCardViewModel(
                    paymentMethodId = 1, savedCardId = "CONTROLLED-CARD",
                    getCards = GetSavedCardsUseCase {
                        AppResult.Success(
                            listOf(
                                SavedPaymentCard(
                                    "CONTROLLED-CARD", 1, "000000", "0000", 12, 2029,
                                    if (americanExpress) 4 else 3, if (americanExpress) "AMERICANEXPRESS" else "VISA", "CONTROLLED"
                                )
                            )
                        )
                    },
                    submitCardPayment = SubmitCardPaymentUseCase { error("No financial submission is authorized in rendering fixtures") },
                    getPurchaseProgress = GetPurchaseProgressUseCase { error("No purchase polling is authorized in rendering fixtures") },
                    getCurrentCart = GetCurrentCartUseCase { AppResult.Success(ShoppingCart(9005, BigDecimal.TEN, emptyList())) },
                    recordRecentPurchase = RecordRecentPurchaseUseCase {
                        error("No purchase completion is authorized in rendering fixtures")
                    },
                    clock = Clock.systemUTC(), dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate), currentYear = 2026
                ).also { vm = it }
            }
            CaixaDSTheme(darkTheme = false) {
                PaymentCardFlowRoute(model, {}, {}, {}, {}, {}, {})
            }
        }
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val folder = java.io.File(requireNotNull(instrumentation.targetContext.externalCacheDir), "codex-financial-parity").apply {
            mkdirs()
        }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        java.io.File(folder, "$name.png").outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        java.io.File(
            folder,
            "$name.txt"
        ).writeText(
            "fixture=true\nnetworkSends=0\nsubmissions=0\n" +
                "densityDpi=${instrumentation.targetContext.resources.displayMetrics.densityDpi}\n" +
                "fontScale=${instrumentation.targetContext.resources.configuration.fontScale}\n"
        )
        assertTrue(java.io.File(folder, "$name.png").length() > 0)
    }
}
