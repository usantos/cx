package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.core.domain.contract.cart.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.model.DamValidation
import br.gov.caixa.loterias.feature.payment.domain.model.PixCharge
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseSubmission
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentCardFlowRoute
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentDataRoute
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentCardViewModel
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentViewModel
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

private const val SYNTHETIC_PURCHASE_ID = 9L
private const val APPROVED_PURCHASE_STATUS = 8

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36])
class PaymentCompletionReentryUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()
    private val store = ViewModelStore()
    private val visible = mutableStateOf(true)
    private val clock = Clock.fixed(Instant.parse("2026-09-07T15:00:00Z"), ZoneOffset.UTC)
    private val dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
    private var completions = 0
    private var posts = 0
    private var records = 0
    private val getCart = GetCurrentCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList())) }
    private val record = RecordRecentPurchaseUseCase {
        records++
        AppResult.Success(Unit)
    }
    private val progress = GetPurchaseProgressUseCase { AppResult.Success(PurchaseProgress(it, APPROVED_PURCHASE_STATUS)) }

    @After fun clearModels() {
        compose.runOnIdle { store.clear() }
    }

    @Test fun cardCompletionIsNotDeliveredAgainWhenTheSameOriginReentersComposition() {
        lateinit var model: PaymentCardViewModel
        compose.runOnIdle {
            model = PaymentCardViewModel(
                paymentMethodId = 1,
                savedCardId = null,
                getCards = GetSavedCardsUseCase { AppResult.Success(emptyList()) },
                submitCardPayment = SubmitCardPaymentUseCase {
                    posts++
                    AppResult.Success(
                        CardSubmissionOutcome.Submitted(PurchaseSubmission(SYNTHETIC_PURCHASE_ID, 0, 0, APPROVED_PURCHASE_STATUS))
                    )
                },
                getPurchaseProgress = progress,
                getCurrentCart = getCart,
                recordRecentPurchase = record,
                clock = clock,
                dispatchers = dispatchers,
                currentYear = 2026
            )
            store.put("card", model)
        }
        show {
            PaymentCardFlowRoute(model, {}, {
                assertEquals(true, model.state.value.successNavigationConsumed)
                assertEquals(false, model.consumeSuccessNavigation())
                complete()
            }, {}, {}, {}, {})
        }
        compose.runOnIdle {
            model.changeNumber("4111111111111111")
            model.changeHolderName("Pessoa")
            model.changeMonth("12")
            model.changeYear("2030")
            model.continueFromCard()
            model.changeCvv("123")
            model.continueFromCvv()
            model.submit()
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(PaymentCardStage.SUCCESS, model.state.value.stage)
            assertEquals(1, completions)
            assertEquals(1, posts)
            assertEquals(1, records)
        }
        reenterTwice()
    }

    @Test fun pixApprovalIsNotDeliveredAgainWhenTheSameOriginReentersComposition() {
        lateinit var model: PaymentViewModel
        compose.runOnIdle {
            model = PaymentViewModel(
                mode = PaymentMode.PIX,
                getMethods = GetPaymentMethodsUseCase { AppResult.Success(emptyList()) },
                getCards = GetSavedCardsUseCase { AppResult.Success(emptyList()) },
                deleteCard = DeleteSavedCardUseCase { _, _ -> AppResult.Success(emptyList()) },
                generatePixCharge = GeneratePixChargeUseCase {
                    posts++
                    AppResult.Success(PixCharge("code", "tx", "qr", "now", "later", SYNTHETIC_PURCHASE_ID, BigDecimal.TEN))
                },
                getPurchaseProgress = progress,
                validateDam = ValidateDamUseCase { AppResult.Success(DamValidation(false, "")) },
                getCurrentCart = getCart,
                recordRecentPurchase = record,
                clock = clock,
                dispatchers = dispatchers
            )
            store.put("pix", model)
        }
        show {
            PaymentDataRoute(
                mode = PaymentMode.PIX, viewModel = model, onBack = {}, onHelp = {}, onPix = {},
                onAddCard = {}, onOpenCard = {}, onApproved = {
                    assertEquals(true, model.state.value.pixApprovalNavigationConsumed)
                    assertEquals(false, model.consumePixApprovalNavigation())
                    complete()
                }, onHome = {}, onPurchases = {},
                onRedirect = {}, onRegistration = {}
            )
        }
        compose.runOnIdle { model.checkPixStatus() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(true, model.state.value.pixApproved)
            assertEquals(1, completions)
            assertEquals(1, posts)
            assertEquals(1, records)
        }
        reenterTwice()
    }

    private fun show(route: @Composable () -> Unit) {
        compose.setFeatureContent { LoteriasCaixaDSTheme { if (visible.value) route() } }
        compose.waitForIdle()
    }

    private fun complete() {
        completions++
        visible.value = false
    }

    private fun reenterTwice() {
        repeat(2) {
            compose.runOnIdle { visible.value = true }
            compose.waitForIdle()
            compose.runOnIdle {
                assertEquals(1, completions)
                assertEquals(1, posts)
                assertEquals(1, records)
                visible.value = false
            }
            compose.waitForIdle()
        }
    }
}
