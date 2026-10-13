package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.cart.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.domain.payment.DamValidation
import br.gov.caixa.loterias.core.domain.payment.DeleteSavedCardUseCase
import br.gov.caixa.loterias.core.domain.payment.GeneratePixChargeUseCase
import br.gov.caixa.loterias.core.domain.payment.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.core.domain.payment.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.core.domain.payment.GetSavedCardsUseCase
import br.gov.caixa.loterias.core.domain.payment.PaymentMethod
import br.gov.caixa.loterias.core.domain.payment.PixCharge
import br.gov.caixa.loterias.core.domain.payment.PurchaseProgress
import br.gov.caixa.loterias.core.domain.payment.PurchaseRedirect
import br.gov.caixa.loterias.core.domain.payment.PurchaseNotice
import br.gov.caixa.loterias.core.domain.payment.PurchaseNoticeReason
import br.gov.caixa.loterias.core.domain.payment.SavedPaymentCard
import br.gov.caixa.loterias.core.domain.payment.ValidateDamUseCase
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {
    private val methods = listOf(
        PaymentMethod(1, "Mercado Pago"),
        PaymentMethod(5, "RecargaPay"),
        PaymentMethod(6, "Pix"),
    )
    private val card = SavedPaymentCard("card", 1, "411111", "1111", 12, 2030, 3, "Visa", "visa")
    private val pix = PixCharge("code", "tx", "qr", "now", "later", 9, BigDecimal.TEN)
    private var methodResult: AppResult<List<PaymentMethod>> = AppResult.Success(methods)
    private var cardResult: AppResult<List<SavedPaymentCard>> = AppResult.Success(listOf(card))
    private var deleteResult: AppResult<List<SavedPaymentCard>> = AppResult.Success(emptyList())
    private var pixResult: AppResult<PixCharge> = AppResult.Success(pix)
    private var progressResult: AppResult<PurchaseProgress> = AppResult.Success(PurchaseProgress(9, 1))
    private var damResult: AppResult<DamValidation> = AppResult.Success(DamValidation(false, ""))
    private var cartResult: AppResult<ShoppingCart> = AppResult.Success(ShoppingCart(2, BigDecimal("25.50"), emptyList()))
    private val requestedMethods = mutableListOf<Long>()
    private var deleted: Pair<String, Long>? = null
    private var generationOverride: (suspend () -> AppResult<PixCharge>)? = null
    private var generatedPixCount = 0
    private var queriedPixCount = 0

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `methods load cart confirm purchase and validate dam`() = runTest {
        val viewModel = viewModel(PaymentMode.METHODS)
        advanceUntilIdle()
        with(viewModel.state.value) {
            assertFalse(loading)
            assertEquals(methods, this.methods)
            assertEquals(BigDecimal("25.50"), cartTotal)
            assertTrue(showPrePurchaseConfirmation)
        }
        damResult = AppResult.Success(DamValidation(true, "Horário indisponível"))
        viewModel.acceptPrePurchase()
        advanceUntilIdle()
        assertEquals("Horário indisponível", viewModel.state.value.damMessage)
        viewModel.dismissDamMessage()
        assertNull(viewModel.state.value.damMessage)
        viewModel.requestAddCard(5)
        assertEquals(5L, viewModel.state.value.pendingAddMethodId)
        viewModel.cancelAddCard()
        assertNull(viewModel.state.value.pendingAddMethodId)
        damResult = AppResult.Success(DamValidation(false, "ignorada"))
        viewModel.acceptPrePurchase()
        advanceUntilIdle()
        assertNull(viewModel.state.value.damMessage)
    }

    @Test fun `cards load by provider and deletion supports confirm and cancel`() = runTest {
        val viewModel = viewModel(PaymentMode.CARDS)
        advanceUntilIdle()
        assertEquals(listOf(1L, 5L), requestedMethods)
        assertEquals(2, viewModel.state.value.cards.size)
        viewModel.requestDelete(card)
        viewModel.cancelDelete()
        assertNull(viewModel.state.value.deleteCandidate)
        viewModel.requestDelete(card)
        viewModel.confirmDelete()
        advanceUntilIdle()
        assertEquals("card" to 1L, deleted)
        assertTrue(viewModel.state.value.cards[1].isNullOrEmpty())
        viewModel.confirmDelete()
    }

    @Test fun `selected provider scopes cards exactly like the legacy screen`() = runTest {
        val recargaPay = viewModel(PaymentMode.CARDS, selectedPaymentMethodId = 5)
        advanceUntilIdle()
        assertEquals(listOf(5L), requestedMethods)
        assertEquals(listOf(5L), recargaPay.state.value.methods.map(PaymentMethod::id))
        assertEquals(setOf(5L), recargaPay.state.value.cards.keys)

        requestedMethods.clear()
        val unavailable = viewModel(PaymentMode.CARDS, selectedPaymentMethodId = 99)
        advanceUntilIdle()
        assertTrue(requestedMethods.isEmpty())
        assertEquals("Meio de pagamento indisponível", unavailable.state.value.errorMessage)
    }

    @Test fun `RecargaPay deletion uses customer card token`() = runTest {
        val recargaCard = card.copy(paymentMethodId = 5, customerToken = "customer-token")
        val viewModel = viewModel(PaymentMode.CARDS, selectedPaymentMethodId = 5)
        advanceUntilIdle()
        viewModel.requestDelete(recargaCard)
        viewModel.confirmDelete()
        advanceUntilIdle()
        assertEquals("customer-token" to 5L, deleted)
    }

    @Test fun `pix generates and checks all terminal states`() = runTest {
        val viewModel = viewModel(PaymentMode.PIX)
        advanceUntilIdle()
        assertEquals(pix, viewModel.state.value.pix)
        progressResult = AppResult.Success(PurchaseProgress(9, 8))
        viewModel.checkPixStatus()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.pixApproved)
        progressResult = AppResult.Success(PurchaseProgress(9, 3))
        viewModel.checkPixStatus()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.pixApproved)
        assertFalse(viewModel.state.value.pixRejected)
        val rejected = viewModel(PaymentMode.PIX)
        rejected.checkPixStatus()
        advanceUntilIdle()
        assertTrue(rejected.state.value.pixRejected)
        rejected.dismissPixStatus()
        assertFalse(rejected.state.value.pixRejected)
        val generatedBeforeReload = generatedPixCount
        viewModel.load()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.pixApproved)
        assertEquals(generatedBeforeReload, generatedPixCount)
    }

    @Test fun `missing pix makes status check a safe no op`() = runTest {
        pixResult = AppResult.Failure(AppError.Network)
        val viewModel = viewModel(PaymentMode.PIX)
        advanceUntilIdle()
        viewModel.checkPixStatus()
        assertNull(viewModel.state.value.pix)
    }

    @Test fun `method cart dam card delete pix and progress failures map errors`() = runTest {
        methodResult = AppResult.Failure(AppError.Network)
        val methodsVm = viewModel(PaymentMode.METHODS)
        advanceUntilIdle()
        assertTrue(methodsVm.state.value.errorMessage!!.contains("internet"))
        methodsVm.dismissError()
        assertNull(methodsVm.state.value.errorMessage)

        methodResult = AppResult.Success(methods)
        cartResult = AppResult.Failure(AppError.Validation("Carrinho inválido"))
        methodsVm.load()
        advanceUntilIdle()
        assertEquals("Carrinho inválido", methodsVm.state.value.errorMessage)
        cartResult = AppResult.Success(ShoppingCart(1, BigDecimal.ONE, emptyList()))
        methodsVm.load(); advanceUntilIdle()
        damResult = AppResult.Failure(AppError.Unexpected)
        methodsVm.acceptPrePurchase(); advanceUntilIdle()
        assertTrue(methodsVm.state.value.errorMessage!!.contains("inesperado"))

        methodResult = AppResult.Failure(AppError.Validation("Sem meios"))
        val cardsVm = viewModel(PaymentMode.CARDS)
        advanceUntilIdle()
        assertEquals("Sem meios", cardsVm.state.value.errorMessage)
        methodResult = AppResult.Success(methods)
        cardResult = AppResult.Failure(AppError.Unexpected)
        cardsVm.load(); advanceUntilIdle()
        assertTrue(cardsVm.state.value.errorMessage!!.contains("inesperado"))
        cardResult = AppResult.Success(listOf(card))
        cardsVm.load(); advanceUntilIdle()
        deleteResult = AppResult.Failure(AppError.Network)
        cardsVm.requestDelete(card); cardsVm.confirmDelete(); advanceUntilIdle()
        assertTrue(cardsVm.state.value.errorMessage!!.contains("internet"))

        pixResult = AppResult.Failure(AppError.Validation("Pix indisponível"))
        val pixVm = viewModel(PaymentMode.PIX)
        advanceUntilIdle()
        assertEquals("Pix indisponível", pixVm.state.value.errorMessage)
        pixResult = AppResult.Success(pix)
        pixVm.load(); advanceUntilIdle()
        progressResult = AppResult.Failure(AppError.Unexpected)
        pixVm.checkPixStatus(); advanceUntilIdle()
        assertTrue(pixVm.state.value.errorMessage!!.contains("inesperado"))
    }

    @Test fun `Pix records amount only on approval and never offers retry after a local write failure`() = runTest {
        val vm = viewModel(PaymentMode.PIX)
        advanceUntilIdle()
        assertTrue(recordedAmounts.isEmpty())
        progressResult = AppResult.Success(PurchaseProgress(pix.purchaseId, 1))
        vm.checkPixStatus(); advanceUntilIdle()
        assertTrue(recordedAmounts.isEmpty())
        progressResult = AppResult.Success(PurchaseProgress(pix.purchaseId, 3))
        vm.checkPixStatus(); advanceUntilIdle()
        assertTrue(recordedAmounts.isEmpty())
        progressResult = AppResult.Success(PurchaseProgress(pix.purchaseId, 8))
        recordResult = AppResult.Failure(AppError.Unexpected)
        vm.checkPixStatus(); advanceUntilIdle()
        vm.checkPixStatus(); advanceUntilIdle()
        assertTrue(vm.state.value.pixApproved)
        assertFalse(vm.state.value.checkingPix)
        assertNull(vm.state.value.errorMessage)
        assertEquals(listOf(pix.amount), recordedAmounts)
    }

    private val recordedAmounts = mutableListOf<BigDecimal>()

    @Test fun `Pix initiated waiting and unknown statuses do not approve or record payment`() = runTest {
        for (status in listOf(0, 1, 2, 7, 9, 10, 15, -1)) {
            progressResult = AppResult.Success(PurchaseProgress(9, status))
            val vm = viewModel(PaymentMode.PIX)
            vm.checkPixStatus(); advanceUntilIdle()
            assertFalse("Status $status", vm.state.value.pixApproved)
            assertFalse("Status $status", vm.state.value.pixRejected)
            assertFalse(vm.state.value.checkingPix)
        }
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `Pix handles every legacy completed and negative status`() = runTest {
        for (status in listOf(6, 8, 13, 14)) {
            progressResult = AppResult.Success(PurchaseProgress(9, status))
            val vm = viewModel(PaymentMode.PIX)
            vm.checkPixStatus(); advanceUntilIdle()
            assertTrue("Status $status", vm.state.value.pixApproved)
            assertFalse(vm.state.value.pixRejected)
        }
        assertEquals(4, recordedAmounts.size)
        for (status in listOf(3, 4, 5, 11, 12)) {
            progressResult = AppResult.Success(PurchaseProgress(9, status))
            val vm = viewModel(PaymentMode.PIX)
            vm.checkPixStatus(); advanceUntilIdle()
            assertTrue("Status $status", vm.state.value.pixRejected)
            assertFalse(vm.state.value.pixApproved)
        }
        assertEquals(4, recordedAmounts.size)
    }
    @Test fun `Pix redirect pauses polling until destination returns without generating another charge`() = runTest {
        for (destination in PurchaseRedirect.entries) {
            val vm = viewModel(PaymentMode.PIX)
            advanceUntilIdle()
            val generatedBefore = generatedPixCount
            val queriedBefore = queriedPixCount
            assertFalse(vm.consumeRedirect(destination))
            progressResult = AppResult.Success(PurchaseProgress(9, 8, redirect = destination))
            vm.checkPixStatus(); advanceUntilIdle()
            assertEquals(destination, vm.state.value.redirect)
            assertFalse(vm.state.value.pixApproved)
            assertFalse(vm.state.value.pixRejected)
            assertFalse(vm.state.value.checkingPix)
            assertFalse(vm.consumeRedirect(PurchaseRedirect.entries.first { it != destination }))
            vm.checkPixStatus(); vm.load(); advanceUntilIdle()
            assertTrue(vm.consumeRedirect(destination))
            assertFalse(vm.consumeRedirect(destination))
            assertNull(vm.state.value.redirect)
            assertTrue(vm.state.value.redirectPaused)
            vm.checkPixStatus(); vm.load(); advanceUntilIdle()
            assertEquals(queriedBefore + 1, queriedPixCount)
            assertEquals(generatedBefore, generatedPixCount)
            assertEquals(pix, vm.state.value.pix)
            vm.resumeAfterRedirect(); advanceUntilIdle()
            assertFalse(vm.state.value.redirectPaused)
            assertEquals(queriedBefore + 1, queriedPixCount)
            progressResult = AppResult.Success(PurchaseProgress(9, 2))
            vm.checkPixStatus(); advanceUntilIdle()
            assertEquals(queriedBefore + 2, queriedPixCount)
            assertEquals(generatedBefore, generatedPixCount)
        }
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `Pix notices pause polling and generation without marking payment approved or rejected`() = runTest {
        for (reason in PurchaseNoticeReason.entries) {
            val vm = viewModel(PaymentMode.PIX)
            val generatedBefore = generatedPixCount
            val queriedBefore = queriedPixCount
            assertFalse(vm.consumeNotice(PurchaseNoticeUiState("Old", false), false))
            progressResult = AppResult.Success(PurchaseProgress(9, 8, notice = PurchaseNotice(reason, "Mensagem", "E1")))
            vm.checkPixStatus(); advanceUntilIdle()
            val notice = requireNotNull(vm.state.value.notice)
            if (reason == PurchaseNoticeReason.BUSINESS_RULE) assertEquals("Mensagem\n\nE1\n07/09/2026 12:00:00", notice.message)
            assertFalse(vm.state.value.pixApproved); assertFalse(vm.state.value.pixRejected)
            assertFalse(vm.state.value.checkingPix)
            assertFalse(vm.consumeNotice(notice.copy(), false))
            if (!notice.offerMoreBets) assertFalse(vm.consumeNotice(notice, true))
            vm.checkPixStatus(); vm.load(); vm.dismissError(); advanceUntilIdle()
            assertEquals(queriedBefore + 1, queriedPixCount)
            assertEquals(generatedBefore, generatedPixCount)
            assertTrue(vm.consumeNotice(notice, false))
            assertFalse(vm.consumeNotice(notice, false))
            advanceUntilIdle()
            assertEquals(queriedBefore + 1, queriedPixCount)
            vm.checkPixStatus(); advanceUntilIdle()
            assertEquals(queriedBefore + 2, queriedPixCount)
            assertFalse(vm.consumeNotice(notice, false))
            assertTrue(vm.consumeNotice(requireNotNull(vm.state.value.notice), false))
            assertEquals(generatedBefore, generatedPixCount)
        }
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `Pix Apostar mais pauses until navigation resumes and never recreates a charge`() = runTest {
        val vm = viewModel(PaymentMode.PIX)
        progressResult = AppResult.Success(PurchaseProgress(9, 8, notice = PurchaseNotice(PurchaseNoticeReason.MORE_BETS_REQUIRED)))
        vm.checkPixStatus(); advanceUntilIdle()
        val notice = requireNotNull(vm.state.value.notice)
        assertTrue(vm.consumeNotice(notice, true))
        assertFalse(vm.consumeNotice(notice, true))
        vm.checkPixStatus(); vm.load(); advanceUntilIdle()
        assertEquals(1, queriedPixCount)
        vm.resumeAfterRedirect(); advanceUntilIdle()
        assertEquals(1, queriedPixCount)
        assertEquals(1, generatedPixCount)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `Pix registration consent pauses queries and never regenerates the charge`() = runTest {
        val vm = viewModel(PaymentMode.PIX)
        assertFalse(vm.consumeRegistrationNotice(PurchaseNoticeUiState("other", false)))
        progressResult = AppResult.Success(PurchaseProgress(9, 8, notice = PurchaseNotice(PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION)))
        vm.checkPixStatus(); advanceUntilIdle()
        val notice = requireNotNull(vm.state.value.notice)
        assertFalse(vm.consumeRegistrationNotice(notice.copy()))
        assertTrue(vm.consumeRegistrationNotice(notice))
        assertFalse(vm.consumeRegistrationNotice(notice))
        vm.resumeAfterRedirect(); vm.checkPixStatus(); vm.load(); advanceUntilIdle()
        assertTrue(vm.state.value.registrationPending)
        assertEquals(1, queriedPixCount); assertEquals(1, generatedPixCount)
        vm.registrationFinished(); advanceUntilIdle()
        assertFalse(vm.state.value.registrationPending)
        assertEquals(1, queriedPixCount); assertEquals(1, generatedPixCount)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `reload while initial Pix generation is pending does not duplicate the POST`() = runTest {
        val gate = CompletableDeferred<AppResult<PixCharge>>()
        generationOverride = { gate.await() }
        val vm = viewModel(PaymentMode.PIX)
        vm.load(); vm.load()
        assertEquals(1, generatedPixCount)
        gate.complete(AppResult.Success(pix))
        advanceUntilIdle()
        assertEquals(1, generatedPixCount)
        assertEquals(pix, vm.state.value.pix)
    }

    @Test fun `restoring an existing Pix screen never posts another charge`() = runTest {
        val vm = viewModel(PaymentMode.PIX)
        advanceUntilIdle()
        assertEquals(1, generatedPixCount)
        vm.load(); vm.load()
        advanceUntilIdle()
        assertEquals("The same screen must retain the generated purchase", 1, generatedPixCount)
        assertEquals(pix, vm.state.value.pix)
        vm.checkPixStatus()
        advanceUntilIdle()
        assertEquals(1, queriedPixCount)
        assertEquals(1, generatedPixCount)
        assertFalse(vm.state.value.pixApproved)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `returning from a Pix redirect retains the existing purchase on reload`() = runTest {
        val vm = viewModel(PaymentMode.PIX)
        progressResult = AppResult.Success(PurchaseProgress(9, 1, redirect = PurchaseRedirect.CART))
        vm.checkPixStatus()
        advanceUntilIdle()
        assertTrue(vm.consumeRedirect(PurchaseRedirect.CART))
        vm.resumeAfterRedirect()
        vm.load()
        advanceUntilIdle()
        assertEquals(1, generatedPixCount)
        assertEquals(pix, vm.state.value.pix)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `a failed Pix generation can be explicitly retried while no charge exists`() = runTest {
        pixResult = AppResult.Failure(AppError.Network)
        val vm = viewModel(PaymentMode.PIX)
        advanceUntilIdle()
        assertEquals(1, generatedPixCount)
        assertNull(vm.state.value.pix)
        pixResult = AppResult.Success(pix)
        vm.load()
        advanceUntilIdle()
        assertEquals(2, generatedPixCount)
        assertEquals(pix, vm.state.value.pix)
        assertNull(vm.state.value.errorMessage)
    }

    private var recordResult: AppResult<Unit> = AppResult.Success(Unit)

    private fun viewModel(mode: PaymentMode, selectedPaymentMethodId: Long? = null): PaymentViewModel {
        val dispatcher = UnconfinedTestDispatcher()
        return PaymentViewModel(
            clock = java.time.Clock.fixed(java.time.Instant.parse("2026-09-07T15:00:00Z"), java.time.ZoneId.of("America/Sao_Paulo")),
            recordRecentPurchase = br.gov.caixa.loterias.core.domain.cart.RecordRecentPurchaseUseCase {
                recordedAmounts += it
                recordResult
            },
            mode = mode,
            selectedPaymentMethodId = selectedPaymentMethodId,
            getMethods = GetPaymentMethodsUseCase { methodResult },
            getCards = GetSavedCardsUseCase { id -> requestedMethods += id; cardResult },
            deleteCard = DeleteSavedCardUseCase { id, method -> deleted = id to method; deleteResult },
            generatePixCharge = GeneratePixChargeUseCase { generatedPixCount++; generationOverride?.invoke() ?: pixResult },
            getPurchaseProgress = GetPurchaseProgressUseCase { queriedPixCount++; progressResult },
            validateDam = ValidateDamUseCase { damResult },
            getCurrentCart = GetCurrentCartUseCase { cartResult },
            dispatchers = FixedCoroutineDispatchers(dispatcher),
        )
    }
}
