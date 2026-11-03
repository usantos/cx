package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.DamValidation
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.PixCharge
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentViewModel
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentModuleAvailabilityTest {
    private val card = SavedPaymentCard("card", 1, "411111", "1111", 12, 2030, 3, "Visa", "visa")
    private val pix = PixCharge("code", "tx", "qr", "now", "later", 9, BigDecimal.TEN)
    private var generatedPixCount = 0
    private var queriedPixCount = 0
    private var deleted: Pair<String, Long>? = null

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `disabled module blocks a new pix charge and card deletion without discarding selection`() = runTest {
        var enabled = true
        val cards = viewModel(PaymentMode.CARDS, canStartOperation = { enabled })
        advanceUntilIdle()
        cards.requestDelete(card)
        enabled = false
        cards.confirmDelete()
        advanceUntilIdle()
        assertNull(deleted)
        assertEquals(card, cards.state.value.deleteCandidate)

        viewModel(PaymentMode.PIX, canStartOperation = { enabled })
        advanceUntilIdle()
        assertEquals(0, generatedPixCount)
    }

    @Test fun `disabling module preserves an existing pix and permits status recovery without another charge`() = runTest {
        var enabled = true
        val vm = viewModel(PaymentMode.PIX, canStartOperation = { enabled })
        advanceUntilIdle()
        assertEquals(1, generatedPixCount)
        enabled = false
        vm.load()
        vm.checkPixStatus()
        advanceUntilIdle()
        assertEquals(1, generatedPixCount)
        assertEquals(1, queriedPixCount)
        assertEquals(pix, vm.state.value.pix)
    }

    private fun viewModel(mode: PaymentMode, canStartOperation: () -> Boolean): PaymentViewModel {
        val dispatcher = UnconfinedTestDispatcher()
        return PaymentViewModel(
            mode = mode,
            canStartOperation = canStartOperation,
            getMethods = GetPaymentMethodsUseCase { AppResult.Success(listOf(PaymentMethod(1, "Mercado Pago"))) },
            getCards = GetSavedCardsUseCase { AppResult.Success(listOf(card)) },
            deleteCard = DeleteSavedCardUseCase { id, method ->
                deleted = id to method
                AppResult.Success(emptyList())
            },
            generatePixCharge = GeneratePixChargeUseCase {
                generatedPixCount++
                AppResult.Success(pix)
            },
            getPurchaseProgress = GetPurchaseProgressUseCase {
                queriedPixCount++
                AppResult.Success(PurchaseProgress(pix.purchaseId, 1))
            },
            validateDam = ValidateDamUseCase { AppResult.Success(DamValidation(false, "")) },
            getCurrentCart = GetCurrentCartUseCase { AppResult.Success(ShoppingCart(2, BigDecimal.TEN, emptyList())) },
            recordRecentPurchase = RecordRecentPurchaseUseCase { AppResult.Success(Unit) },
            clock = Clock.fixed(Instant.parse("2026-09-07T15:00:00Z"), ZoneId.of("America/Sao_Paulo")),
            dispatchers = FixedCoroutineDispatchers(dispatcher)
        )
    }
}
