package br.gov.caixa.loterias.feature.payment

import androidx.compose.ui.test.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPaymentUseCase
import java.math.BigDecimal
import java.time.Clock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentCardFlowRoute
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentCardViewModel

/** Nenhuma requisição financeira é permitida; asserções exercitam a rota e o ViewModel reais. */
class PaymentCardFlowCoordinationTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<PaymentCardViewModel>()
    @After fun cleanup() { models.forEach { it.viewModelScope.cancel() } }

    @Test fun previewCvvAndConfirmationUseCurrentModelWithoutSubmitting() {
        val first = model()
        val second = model()
        val replace = mutableStateOf(false)
        var completed = 0
        compose.setContent { CaixaDSTheme {
            PaymentCardFlowRoute(if (replace.value) second else first, {}, { completed++ }, {}, {}, {}, {})
        } }
        compose.onNodeWithText("Incluir Código de Verificação").performClick()
        compose.runOnIdle { assertEquals(PaymentCardStage.CVV, first.state.value.stage); replace.value = true }
        compose.onNodeWithText("Incluir Código de Verificação").performClick()
        compose.onNodeWithText("Confirmar").assertIsNotEnabled()
        compose.runOnIdle { second.changeCvv("123") }
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("Confirme os dados digitados").assertIsDisplayed()
        compose.onNodeWithText("Apostar").performScrollTo().assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(PaymentCardStage.CVV, first.state.value.stage)
            assertEquals(PaymentCardStage.CONFIRM, second.state.value.stage)
            assertEquals(0, completed)
        }
    }

    @Test fun cardLoadFailureRetriesToConfirmationAndBackKeepsTheExternalCallback() {
        val model = model(AppResult.Failure(AppError.Network))
        var back = 0
        compose.setContent { CaixaDSTheme { PaymentCardFlowRoute(model, { back++ }, {}, {}, {}, {}, {}) } }
        compose.onNodeWithText("Pagamento não aprovado").assertIsDisplayed()
        compose.onNodeWithText("Não foi possível processar o pagamento. Verifique sua internet.").assertIsDisplayed()
        compose.onNodeWithText("Voltar").performClick()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Confirme os dados digitados").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, back); assertEquals(PaymentCardStage.CONFIRM, model.state.value.stage) }
    }

    private fun model(cards: AppResult<List<SavedPaymentCard>> = AppResult.Success(listOf(
        SavedPaymentCard("fixture", 1, "411111", "1111", 12, 2030, 3, "VISA", "CONTROLLED"),
    )),
    ) = PaymentCardViewModel(
        paymentMethodId = 1, savedCardId = "fixture",
        getCards = GetSavedCardsUseCase { cards },
        submitCardPayment = SubmitCardPaymentUseCase { error("Rendering must not submit a purchase") },
        getPurchaseProgress = GetPurchaseProgressUseCase { error("Rendering must not poll purchases") },
        getCurrentCart = GetCurrentCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList())) },
        recordRecentPurchase = RecordRecentPurchaseUseCase { error("Rendering must not complete a purchase") },
        clock = Clock.systemUTC(), dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate), currentYear = 2026
    ).also(models::add)
}
