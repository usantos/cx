package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.CheckCheckoutLocationUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.HasRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordCheckoutLocationRequestUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.VerifyCartPaymentAccountUseCase
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartCheckoutSessionContinuationTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test fun `late accepted validation cannot open payment after logout or account replacement`() = runTest(dispatcherRule.dispatcher) {
        for (replacement in listOf(null, AuthenticatedSessionIdentity("b".repeat(64)))) {
            val owner = AuthenticatedSessionIdentity("a".repeat(64))
            val changes = MutableStateFlow<AuthenticatedSessionIdentity?>(owner)
            val sessions = object : AuthenticatedSessionIdentityProvider {
                override fun captureIdentity() = changes.value
                override fun isCurrent(identity: AuthenticatedSessionIdentity) = changes.value === identity
            }
            val snapshot = ShoppingCart(5, BigDecimal("20.00"), listOf(
                CartItem(12, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "01 02 03", BigDecimal("20.00")),
            ))
            val validation = CompletableDeferred<Unit>()
            var validationStarted = false
            val vm = CartViewModel(
                getCurrentCart = GetCurrentCartUseCase { AppResult.Success(snapshot) },
                removeCartItem = RemoveCartItemUseCase { error("No removal in checkout") },
                clearCart = ClearCartUseCase { error("No clearing in checkout") },
                serverTime = ServerTimeSource { Instant.parse("2026-09-04T12:00:00Z") },
                verifyPaymentAccount = VerifyCartPaymentAccountUseCase { AppResult.Success(CartPaymentAccountDecision.Allowed) },
                validateCart = ValidateCartUseCase {
                    validationStarted = true
                    // Modela um callback de transporte concluído após o cancelamento de seu chamador.
                    withContext(NonCancellable) { validation.await() }
                    AppResult.Success(CartValidationResult.Accepted)
                },
                hasRecentPurchase = HasRecentPurchaseUseCase { AppResult.Success(false) },
                checkCheckoutLocation = CheckCheckoutLocationUseCase { AppResult.Success(CheckoutLocationStatus.IN_BRAZIL) },
                recordLocationRequest = RecordCheckoutLocationRequestUseCase { AppResult.Success(Unit) },
                dispatchers = FixedCoroutineDispatchers(dispatcherRule.dispatcher),
                reservationSessions = sessions,
                reservationSessionChanges = changes,
            )
            dispatcherRule.dispatcher.scheduler.runCurrent()
            vm.requestPayment()
            dispatcherRule.dispatcher.scheduler.runCurrent()
            assertTrue(validationStarted)
            assertTrue(vm.state.value.loading)

            changes.value = replacement
            dispatcherRule.dispatcher.scheduler.runCurrent()
            assertNull(vm.state.value.cart)
            validation.complete(Unit)
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()

            assertFalse("An ended session must not navigate to payment", vm.state.value.paymentReady)
            assertNull(vm.state.value.cart)
        }
    }
}
