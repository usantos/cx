package br.gov.caixa.loterias.feature.payment

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.payment.domain.model.CardPaymentRequest
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentCardSelection
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.payment.domain.model.PurchasePolling
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNotice
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNoticeReason
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseSubmission
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPaymentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
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
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentCardViewModel
import br.gov.caixa.loterias.feature.payment.presentation.mapper.acceptedBrands
import br.gov.caixa.loterias.feature.payment.presentation.mapper.formatCardNumber
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentCardViewModelTest {
    private val saved = SavedPaymentCard("saved", 1, "411111", "1111", 12, 2030, 3, "Visa", "visa")
    private var cardsResult: AppResult<List<SavedPaymentCard>> = AppResult.Success(listOf(saved))
    private var submitResult: AppResult<PurchaseSubmission> = AppResult.Success(PurchaseSubmission(9, 1, 2, 8))
    private var outcomeOverride: AppResult<CardSubmissionOutcome>? = null
    private var progressCalls = 0
    private var submitCalls = 0
    private val polledIds = mutableListOf<Long>()
    private var progressResults = ArrayDeque<AppResult<PurchaseProgress>>()
    private var submitted: CardPaymentRequest? = null
    private val recordedAmounts = mutableListOf<java.math.BigDecimal>()
    private var recordResult: AppResult<Unit> = AppResult.Success(Unit)
    private var cartResult: AppResult<br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart> = AppResult.Success(
        br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart(1, java.math.BigDecimal("30.50"), emptyList()),
    )

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `new card edits normalize values and validate form`() = runTest {
        val viewModel = viewModel()
        assertEquals(PaymentCardStage.FORM, viewModel.state.value.stage)
        viewModel.changeNumber("37 1234a567890123456789")
        viewModel.changeHolderName("nome muito longo nome muito longo nome muito longo")
        viewModel.changeMonth("1x2")
        viewModel.changeYear("20300")
        viewModel.toggleSave()
        with(viewModel.state.value) {
            assertEquals(16, normalizedNumber.length)
            assertEquals("AMERICANEXPRESS", brand)
            assertEquals(4, cvvLength)
            assertEquals(40, holderName.length)
            assertEquals("12", month)
            assertEquals("2030", year)
            assertTrue(save)
            assertTrue(formValid)
        }
        viewModel.continueFromCard()
        assertEquals(PaymentCardStage.CVV, viewModel.state.value.stage)
        viewModel.changeCvv("12x345")
        assertEquals("1234", viewModel.state.value.cvv)
        viewModel.continueFromCvv()
        assertEquals(PaymentCardStage.CONFIRM, viewModel.state.value.stage)
    }

    @Test fun `provider and expiration options reproduce legacy payment configuration`() = runTest {
        val mercadoPago = viewModel(paymentMethodId = 1, currentYear = 2026)
        assertEquals("Mercado Pago", mercadoPago.state.value.providerName)
        assertEquals("", mercadoPago.state.value.brand)
        assertEquals(listOf("AMERICANEXPRESS", "ELO", "HIPERCARD", "MASTERCARD", "VISA"), mercadoPago.state.value.acceptedBrands)
        assertEquals((2026..2046).toList(), mercadoPago.state.value.expirationYears)
        validVisa(mercadoPago)
        mercadoPago.changeYear("2025")
        assertFalse(mercadoPago.state.value.formValid)

        val recargaPay = viewModel(paymentMethodId = 5, currentYear = 2026)
        assertEquals("RecargaPay", recargaPay.state.value.providerName)
        assertEquals(listOf("ELO", "MASTERCARD", "VISA"), recargaPay.state.value.acceptedBrands)
        assertEquals("4111 1111 1111 1111", formatCardNumber("4111111111111111999"))
    }

    @Test fun `invalid form and cvv expose dismissible messages`() = runTest {
        val viewModel = viewModel()
        viewModel.continueFromCard()
        assertTrue(viewModel.state.value.errorMessage!!.contains("Preencha"))
        viewModel.dismissError()
        assertNull(viewModel.state.value.errorMessage)
        validVisa(viewModel)
        viewModel.continueFromCard()
        viewModel.changeCvv("12")
        viewModel.continueFromCvv()
        assertTrue(viewModel.state.value.errorMessage!!.contains("segurança"))
        viewModel.changeNumber("5555555555554444")
        assertEquals("MASTERCARD", viewModel.state.value.brand)
        assertEquals("", viewModel.state.value.cvv)
    }

    @Test fun `saved card loads preview and missing or failed cards fail`() = runTest {
        val found = viewModel(savedCardId = "saved")
        advanceUntilIdle()
        assertEquals(PaymentCardStage.PREVIEW, found.state.value.stage)
        assertEquals("1111", found.state.value.lastDigits)
        found.continueFromCard()
        assertEquals(PaymentCardStage.CVV, found.state.value.stage)

        val missing = viewModel(savedCardId = "missing")
        advanceUntilIdle()
        assertEquals("Cartão salvo não encontrado", missing.state.value.errorMessage)

        cardsResult = AppResult.Failure(AppError.Network)
        val failed = viewModel(savedCardId = "saved")
        advanceUntilIdle()
        assertEquals(PaymentCardStage.FAILURE, failed.state.value.stage)
        assertTrue(failed.state.value.errorMessage!!.contains("internet"))
        failed.retry()
        assertEquals(PaymentCardStage.CONFIRM, failed.state.value.stage)
    }

    @Test fun `legacy state entry points restore real saved card instead of placeholder data`() = runTest {
        listOf(
            PaymentCardStage.PREVIEW,
            PaymentCardStage.CVV,
            PaymentCardStage.CONFIRM,
        ).forEach { stage ->
            val viewModel = viewModel(initialStage = stage)
            advanceUntilIdle()
            assertEquals(stage, viewModel.state.value.stage)
            assertEquals(saved, viewModel.state.value.savedCard)
            assertEquals("1111", viewModel.state.value.lastDigits)
        }

        cardsResult = AppResult.Success(emptyList())
        val withoutSavedCard = viewModel(initialStage = PaymentCardStage.PREVIEW)
        advanceUntilIdle()
        assertEquals(PaymentCardStage.FORM, withoutSavedCard.state.value.stage)
        assertTrue(withoutSavedCard.state.value.errorMessage!!.contains("Cadastre"))
    }

    @Test fun `new and saved cards preserve submission and business failure`() = runTest {
        val approved = viewModel()
        validVisa(approved)
        approved.continueFromCard(); approved.changeCvv("123"); approved.continueFromCvv(); approved.submit()
        advanceUntilIdle()
        assertEquals(PaymentCardStage.SUCCESS, approved.state.value.stage)
        assertTrue(submitted!!.selection is PaymentCardSelection.New)

        submitResult = AppResult.Failure(AppError.Validation("Pagamento não autorizado."))
        val rejected = viewModel(savedCardId = "saved")
        rejected.continueFromCard(); rejected.changeCvv("123"); rejected.continueFromCvv(); rejected.submit()
        advanceUntilIdle()
        assertEquals(PaymentCardStage.FAILURE, rejected.state.value.stage)
        assertTrue(submitted!!.selection is PaymentCardSelection.Saved)
    }

    @Test fun `card polling handles completion business errors and exhaustion`() = runTest {
        submitResult = AppResult.Success(PurchaseSubmission(10, 1, 2, 1))
        progressResults.add(AppResult.Success(PurchaseProgress(10, 8)))
        val approved = submitReadyViewModel()
        approved.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.SUCCESS, approved.state.value.stage)

        progressResults.add(AppResult.Failure(AppError.Validation("Pagamento não autorizado.")))
        val rejected = submitReadyViewModel()
        rejected.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.FAILURE, rejected.state.value.stage)

        progressResults.add(AppResult.Success(PurchaseProgress(10, 1, PurchasePolling(0, 2))))
        progressResults.add(AppResult.Success(PurchaseProgress(10, 1, PurchasePolling(0, 2))))
        submitResult = AppResult.Success(PurchaseSubmission(10, 0, 2, 1))
        val exhausted = submitReadyViewModel()
        exhausted.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.SUCCESS, exhausted.state.value.stage)
    }

    @Test fun `submission and polling errors map every application error`() = runTest {
        submitResult = AppResult.Failure(AppError.Validation("Cartão recusado"))
        val validation = submitReadyViewModel()
        validation.submit(); advanceUntilIdle()
        assertEquals("Cartão recusado", validation.state.value.errorMessage)

        submitResult = AppResult.Failure(AppError.Validation(""))
        val emptyValidation = submitReadyViewModel()
        emptyValidation.submit(); advanceUntilIdle()
        assertTrue(emptyValidation.state.value.errorMessage!!.contains("processar"))

        submitResult = AppResult.Failure(AppError.Unexpected)
        val unexpected = submitReadyViewModel()
        unexpected.submit(); advanceUntilIdle()
        assertTrue(unexpected.state.value.errorMessage!!.contains("inesperado"))

        submitResult = AppResult.Success(PurchaseSubmission(10, 1, 1, 1))
        progressResults.add(AppResult.Failure(AppError.Network))
        val network = submitReadyViewModel()
        network.submit(); advanceUntilIdle()
        assertTrue(network.state.value.errorMessage!!.contains("internet"))
        assertFalse(network.state.value.formValid.not())
    }

    private fun submitReadyViewModel(): PaymentCardViewModel = viewModel().also {
        validVisa(it)
        it.continueFromCard()
        it.changeCvv("123")
        it.continueFromCvv()
    }

    @Test fun `only completed or processing purchase records original cart amount once`() = runTest {
        val vm = viewModel()
        vm.submit()
        assertNull(submitted)
        validVisa(vm); vm.continueFromCard(); vm.changeCvv("123"); vm.continueFromCvv()
        recordResult = AppResult.Failure(br.gov.caixa.loterias.core.common.result.AppError.Unexpected)
        vm.submit(); vm.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.SUCCESS, vm.state.value.stage)
        assertEquals(listOf(java.math.BigDecimal("30.50")), recordedAmounts)
        vm.submit()
        assertEquals(1, recordedAmounts.size)
    }

    @Test fun `unavailable or nonpositive cart cannot submit or record a card purchase`() = runTest {
        val vm = viewModel()
        validVisa(vm); vm.continueFromCard(); vm.changeCvv("123"); vm.continueFromCvv()
        cartResult = AppResult.Failure(br.gov.caixa.loterias.core.common.result.AppError.Network)
        vm.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.FAILURE, vm.state.value.stage)
        assertNull(submitted)
        cartResult = AppResult.Success(br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart(1, java.math.BigDecimal.ZERO, emptyList()))
        vm.retry(); vm.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.FAILURE, vm.state.value.stage)
        assertNull(submitted)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `rejected card purchase does not create a recent purchase warning`() = runTest {
        submitResult = AppResult.Failure(AppError.Validation("Pagamento não autorizado."))
        val vm = viewModel()
        validVisa(vm); vm.continueFromCard(); vm.changeCvv("123"); vm.continueFromCvv()
        vm.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.FAILURE, vm.state.value.stage)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `debit initiated waits for polling and is not immediate completion`() = runTest {
        submitResult = AppResult.Success(PurchaseSubmission(10, 1000, 3, 2))
        progressResults.add(AppResult.Success(PurchaseProgress(10, 7, PurchasePolling(1000, 3))))
        progressResults.add(AppResult.Success(PurchaseProgress(10, 8)))
        val vm = submitReadyViewModel()
        vm.submit()
        assertEquals(PaymentCardStage.PROCESSING, vm.state.value.stage)
        assertEquals(0, progressCalls)
        assertTrue(recordedAmounts.isEmpty())
        advanceUntilIdle()
        assertEquals(2, progressCalls)
        assertEquals(PaymentCardStage.SUCCESS, vm.state.value.stage)
        assertEquals(1, recordedAmounts.size)
    }

    @Test fun `successful response ends card processing even without final payment status`() = runTest {
        submitResult = AppResult.Success(PurchaseSubmission(10, 1, 3, 2))
        for (status in (0..14)) {
            progressResults.add(AppResult.Success(PurchaseProgress(10, status)))
            val vm = submitReadyViewModel()
            vm.submit(); advanceUntilIdle()
            assertEquals("Protocol completion $status", PaymentCardStage.SUCCESS, vm.state.value.stage)
        }
        assertEquals(15, progressCalls)
        assertEquals(15, recordedAmounts.size)
    }

    @Test fun `submission status does not replace the first protocol query`() = runTest {
        for (status in listOf(6, 8, 13, 14)) {
            submitResult = AppResult.Success(PurchaseSubmission(10, 1000, 3, status))
            val vm = submitReadyViewModel()
            vm.submit(); advanceUntilIdle()
            assertEquals("Status $status", PaymentCardStage.SUCCESS, vm.state.value.stage)
        }
        assertEquals(4, progressCalls)
        assertEquals(4, recordedAmounts.size)
    }

    @Test fun `server updates id delay and attempts without resetting completed count`() = runTest {
        submitResult = AppResult.Success(PurchaseSubmission(10, 1000, 3, 2))
        progressResults.add(AppResult.Success(PurchaseProgress(20, 7, PurchasePolling(4000, 3))))
        progressResults.add(AppResult.Success(PurchaseProgress(30, 7, PurchasePolling(8000, 2))))
        val vm = submitReadyViewModel()
        vm.submit()
        advanceTimeBy(1000); runCurrent()
        assertEquals(listOf(10L), polledIds)
        assertEquals(PaymentCardStage.PROCESSING, vm.state.value.stage)
        advanceTimeBy(3999); runCurrent()
        assertEquals(1, progressCalls)
        advanceTimeBy(1); runCurrent()
        assertEquals(listOf(10L, 20L), polledIds)
        assertEquals(PaymentCardStage.SUCCESS, vm.state.value.stage)
        assertEquals(1, recordedAmounts.size)
        assertEquals(1, submitCalls)
    }

    @Test fun `zero attempts finish without the extra query introduced by coercion`() = runTest {
        for (attempts in listOf(0, -1)) {
            submitResult = AppResult.Success(PurchaseSubmission(10, -1, attempts, 2))
            val vm = submitReadyViewModel()
            vm.submit(); advanceUntilIdle()
            assertEquals(PaymentCardStage.SUCCESS, vm.state.value.stage)
        }
        assertEquals(0, progressCalls)
    }

    @Test fun `retry after a failed query retains purchase and never reissues card submission`() = runTest {
        submitResult = AppResult.Success(PurchaseSubmission(10, 1, 3, 2))
        progressResults.add(AppResult.Success(PurchaseProgress(20, 7, PurchasePolling(2, 3))))
        progressResults.add(AppResult.Failure(AppError.Network))
        val vm = submitReadyViewModel()
        vm.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.FAILURE, vm.state.value.stage)
        assertTrue(vm.state.value.trackingPurchase)
        assertEquals("", vm.state.value.cvv)
        // Um carrinho alterado não deve mudar o valor original da compra ao repetir uma consulta.
        cartResult = AppResult.Success(br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart(1, java.math.BigDecimal.TEN, emptyList()))
        progressResults.add(AppResult.Success(PurchaseProgress(20, 8)))
        vm.retry(); vm.retry(); vm.submit(); advanceUntilIdle()
        assertEquals(listOf(10L, 20L, 20L), polledIds)
        assertEquals(1, submitCalls)
        assertEquals(listOf(java.math.BigDecimal("30.50")), recordedAmounts)
        assertEquals(PaymentCardStage.SUCCESS, vm.state.value.stage)
        vm.retry(); vm.submit()
        assertEquals(1, submitCalls)
    }

    @Test fun `unrecognized query error remains an error and invalid purchase cannot be monitored`() = runTest {
        submitResult = AppResult.Success(PurchaseSubmission(10, 1, 2, 2))
        progressResults.add(AppResult.Failure(AppError.Unexpected))
        val vm = submitReadyViewModel()
        vm.submit(); advanceUntilIdle()
        assertEquals("Não foi possível consultar sua compra.", vm.state.value.errorMessage)
        submitResult = AppResult.Success(PurchaseSubmission(0, 0, 0, 8))
        val invalid = submitReadyViewModel()
        invalid.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.REVIEW_REQUIRED, invalid.state.value.stage)
        assertEquals(CardSubmissionOutcome.ReviewRequired(), invalid.state.value.purchaseReview)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `stale form actions cannot reopen confirmation while a purchase is processing`() = runTest {
        submitResult = AppResult.Success(PurchaseSubmission(10, 1000, 3, 2))
        val vm = submitReadyViewModel()
        vm.submit()
        vm.continueFromCard(); vm.changeCvv("123"); vm.continueFromCvv(); vm.load(); vm.retry(); vm.submit()
        assertEquals(PaymentCardStage.PROCESSING, vm.state.value.stage)
        assertEquals(1, submitCalls)
        advanceUntilIdle()
        vm.load(); vm.continueFromCard(); vm.continueFromCvv(); vm.submit()
        assertEquals(PaymentCardStage.SUCCESS, vm.state.value.stage)
        assertEquals(1, submitCalls)
        assertEquals(1, recordedAmounts.size)
    }

    @Test fun `uncertain submission requires review without retry polling or recording completion`() = runTest {
        for (id in listOf(null, 91L)) {
            outcomeOverride = AppResult.Success(CardSubmissionOutcome.ReviewRequired(id))
            val vm = submitReadyViewModel()
            assertFalse(vm.acknowledgePurchaseReview())
            val callsBefore = submitCalls
            vm.submit(); advanceUntilIdle()
            assertEquals(PaymentCardStage.REVIEW_REQUIRED, vm.state.value.stage)
            assertEquals(id, vm.state.value.purchaseReview?.purchaseId)
            assertEquals("", vm.state.value.cvv)
            assertNull(vm.state.value.errorMessage)
            vm.retry(); vm.load(); vm.continueFromCard(); vm.continueFromCvv(); vm.submit(); advanceUntilIdle()
            assertEquals(callsBefore + 1, submitCalls)
            assertEquals(PaymentCardStage.REVIEW_REQUIRED, vm.state.value.stage)
            assertTrue(vm.acknowledgePurchaseReview())
            assertFalse(vm.acknowledgePurchaseReview())
            vm.dismissError(); vm.retry(); vm.submit()
            assertEquals(callsBefore + 1, submitCalls)
        }
        assertEquals(0, progressCalls)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `preparation failure still allows correction before any purchase outcome`() = runTest {
        submitResult = AppResult.Failure(AppError.Validation("Código de segurança inválido"))
        val vm = submitReadyViewModel()
        vm.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.FAILURE, vm.state.value.stage)
        assertNull(vm.state.value.purchaseReview)
        assertFalse(vm.acknowledgePurchaseReview())
        submitResult = AppResult.Success(PurchaseSubmission(91, 0, 0, 2))
        vm.retry(); vm.submit(); advanceUntilIdle()
        assertEquals(PaymentCardStage.SUCCESS, vm.state.value.stage)
        assertEquals(2, submitCalls)
        assertFalse(vm.acknowledgePurchaseReview())
    }

    @Test fun `submission redirects dispatch once and do not submit again on return`() = runTest {
        for (destination in PurchaseRedirect.entries) {
            for (id in listOf(null, 91L)) {
                outcomeOverride = AppResult.Success(CardSubmissionOutcome.RedirectRequired(destination, id))
                val vm = submitReadyViewModel()
                assertFalse(vm.consumeRedirect(destination))
                val before = submitCalls
                vm.submit(); advanceUntilIdle()
                assertEquals(PaymentCardStage.REDIRECT_REQUIRED, vm.state.value.stage)
                assertEquals(destination, vm.state.value.redirect)
                assertEquals(id, vm.state.value.redirectPurchaseId)
                assertFalse(vm.consumeRedirect(PurchaseRedirect.entries.first { it != destination }))
                vm.retry(); vm.submit(); vm.load(); vm.continueFromCard(); vm.continueFromCvv()
                advanceUntilIdle()
                assertEquals(before + 1, submitCalls)
                assertTrue(vm.consumeRedirect(destination))
                assertFalse(vm.consumeRedirect(destination))
                advanceUntilIdle()
                assertNull(vm.state.value.redirect)
                assertNull(vm.state.value.redirectPurchaseId)
                assertEquals(before + 1, submitCalls)
                assertTrue(vm.state.value.redirectPaused)
                vm.submit(); vm.retry(); vm.load(); advanceUntilIdle()
                assertFalse(vm.acknowledgePurchaseReview())
                assertEquals(before + 1, submitCalls)
                vm.resumeAfterRedirect(); advanceUntilIdle()
                assertFalse(vm.state.value.redirectPaused)
                assertEquals(before + 1, submitCalls)
                if (id == null) {
                    assertEquals(PaymentCardStage.CONFIRM, vm.state.value.stage)
                    // É necessária uma nova ação explícita após satisfazer a pré-condição.
                    vm.submit(); advanceUntilIdle()
                    assertEquals(before + 2, submitCalls)
                } else {
                    assertEquals(PaymentCardStage.REVIEW_REQUIRED, vm.state.value.stage)
                    assertEquals(CardSubmissionOutcome.ReviewRequired(id), vm.state.value.purchaseReview)
                    assertEquals("", vm.state.value.cvv)
                    vm.submit(); vm.retry(); advanceUntilIdle()
                    assertEquals(before + 1, submitCalls)
                }
            }
        }
        assertEquals(0, progressCalls)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `query redirect resumes same purchase with updated id without reposting card`() = runTest {
        for (destination in PurchaseRedirect.entries) {
            submitResult = AppResult.Success(PurchaseSubmission(10, 1, 3, 2))
            progressResults.add(AppResult.Success(PurchaseProgress(20, 8, redirect = destination)))
            val vm = submitReadyViewModel()
            val before = submitCalls
            val recordedBefore = recordedAmounts.size
            val queriesBefore = polledIds.size
            vm.submit(); advanceUntilIdle()
            assertEquals(PaymentCardStage.REDIRECT_REQUIRED, vm.state.value.stage)
            assertTrue(vm.state.value.trackingPurchase)
            assertEquals(recordedBefore, recordedAmounts.size)
            vm.retry(); vm.submit(); vm.load(); advanceUntilIdle()
            assertEquals(queriesBefore + 1, polledIds.size)
            assertTrue(vm.consumeRedirect(destination))
            assertEquals(PaymentCardStage.FAILURE, vm.state.value.stage)
            assertFalse(vm.consumeRedirect(destination))
            vm.submit(); vm.retry(); advanceUntilIdle()
            assertEquals(queriesBefore + 1, polledIds.size)
            vm.resumeAfterRedirect(); advanceUntilIdle()
            assertEquals(queriesBefore + 1, polledIds.size)
            progressResults.add(AppResult.Success(PurchaseProgress(20, 8)))
            vm.retry(); vm.retry(); vm.submit(); advanceUntilIdle()
            assertEquals(listOf(10L, 20L), polledIds.drop(queriesBefore))
            assertEquals(before + 1, submitCalls)
            assertEquals(recordedBefore + 1, recordedAmounts.size)
            assertEquals(PaymentCardStage.SUCCESS, vm.state.value.stage)
        }
    }

    @Test fun `known submission notices block stale actions and Entendi does not repost or record purchase`() = runTest {
        for (reason in PurchaseNoticeReason.entries) {
            outcomeOverride = AppResult.Success(CardSubmissionOutcome.NoticeRequired(PurchaseNotice(reason, "Mensagem", "E1")))
            val vm = submitReadyViewModel()
            val before = submitCalls
            assertFalse(vm.consumeNotice(PurchaseNoticeUiState("Old", false), false))
            vm.submit(); advanceUntilIdle()
            assertEquals(PaymentCardStage.NOTICE_REQUIRED, vm.state.value.stage)
            val notice = requireNotNull(vm.state.value.notice)
            if (reason == PurchaseNoticeReason.BUSINESS_RULE) assertEquals("Mensagem\n\nE1\n07/09/2026 12:00:00", notice.message)
            assertFalse(vm.consumeNotice(notice.copy(), false))
            if (!notice.offerMoreBets) assertFalse(vm.consumeNotice(notice, true))
            vm.submit(); vm.retry(); vm.load(); vm.continueFromCard(); vm.continueFromCvv(); vm.dismissError()
            advanceUntilIdle()
            assertEquals(PaymentCardStage.NOTICE_REQUIRED, vm.state.value.stage)
            assertEquals(before + 1, submitCalls)
            assertTrue(vm.consumeNotice(notice, false))
            assertFalse(vm.consumeNotice(notice, false))
            assertNull(vm.state.value.notice)
            assertEquals(PaymentCardStage.CONFIRM, vm.state.value.stage)
            advanceUntilIdle()
            assertEquals(before + 1, submitCalls)
            vm.submit(); advanceUntilIdle()
            assertEquals(before + 2, submitCalls)
            // Um callback antigo não pode dispensar um aviso idêntico posterior.
            assertFalse(vm.consumeNotice(notice, false))
            assertTrue(vm.consumeNotice(requireNotNull(vm.state.value.notice), false))
        }
        assertEquals(0, progressCalls)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `Apostar mais consumes once and pauses card actions during navigation`() = runTest {
        outcomeOverride = AppResult.Success(CardSubmissionOutcome.NoticeRequired(PurchaseNotice(PurchaseNoticeReason.MORE_BETS_REQUIRED)))
        val vm = submitReadyViewModel()
        vm.submit(); advanceUntilIdle()
        val notice = requireNotNull(vm.state.value.notice)
        assertTrue(vm.consumeNotice(notice, true))
        assertFalse(vm.consumeNotice(notice, true))
        assertTrue(vm.state.value.redirectPaused)
        vm.submit(); vm.retry(); vm.load(); advanceUntilIdle()
        assertEquals(1, submitCalls)
        vm.resumeAfterRedirect(); advanceUntilIdle()
        assertEquals(1, submitCalls)
    }

    @Test fun `query notice does not complete purchase and retry continues its identity without repost`() = runTest {
        for (reason in PurchaseNoticeReason.entries) {
            submitResult = AppResult.Success(PurchaseSubmission(10, 1, 3, 2))
            progressResults.add(AppResult.Success(PurchaseProgress(20, 8, notice = PurchaseNotice(reason))))
            val vm = submitReadyViewModel()
            val before = submitCalls
            val queriesBefore = polledIds.size
            val recordedBefore = recordedAmounts.size
            vm.submit(); advanceUntilIdle()
            val notice = requireNotNull(vm.state.value.notice)
            assertEquals(PaymentCardStage.NOTICE_REQUIRED, vm.state.value.stage)
            assertEquals(recordedBefore, recordedAmounts.size)
            vm.retry(); vm.submit(); advanceUntilIdle()
            assertEquals(queriesBefore + 1, polledIds.size)
            assertTrue(vm.consumeNotice(notice, false))
            assertEquals(PaymentCardStage.FAILURE, vm.state.value.stage)
            assertEquals("Não foi possível consultar sua compra.", vm.state.value.errorMessage)
            advanceUntilIdle()
            assertEquals(queriesBefore + 1, polledIds.size)
            progressResults.add(AppResult.Success(PurchaseProgress(20, 8)))
            vm.retry(); vm.retry(); vm.submit(); advanceUntilIdle()
            assertEquals(before + 1, submitCalls)
            assertEquals(listOf(10L, 20L), polledIds.drop(queriesBefore))
            assertEquals(recordedBefore + 1, recordedAmounts.size)
            assertEquals(PaymentCardStage.SUCCESS, vm.state.value.stage)
        }
    }

    @Test fun `registration consent pauses card even on lifecycle resume and never reposts on browser return`() = runTest {
        outcomeOverride = AppResult.Success(CardSubmissionOutcome.NoticeRequired(PurchaseNotice(PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION)))
        val vm = submitReadyViewModel()
        assertFalse(vm.consumeRegistrationNotice(PurchaseNoticeUiState("other", false)))
        vm.submit(); advanceUntilIdle()
        val declined = requireNotNull(vm.state.value.notice)
        assertTrue(vm.consumeNotice(declined, false))
        assertFalse(vm.state.value.registrationPending)
        assertEquals(1, submitCalls)
        vm.submit(); advanceUntilIdle()
        val consent = requireNotNull(vm.state.value.notice)
        assertFalse(vm.consumeRegistrationNotice(declined))
        assertFalse(vm.consumeRegistrationNotice(consent.copy()))
        assertTrue(vm.consumeRegistrationNotice(consent))
        assertFalse(vm.consumeRegistrationNotice(consent))
        vm.resumeAfterRedirect(); vm.load(); vm.submit(); vm.retry(); advanceUntilIdle()
        assertTrue(vm.state.value.registrationPending)
        assertEquals(2, submitCalls)
        vm.registrationFinished(); advanceUntilIdle()
        assertFalse(vm.state.value.registrationPending)
        assertEquals(2, submitCalls)
        assertTrue(recordedAmounts.isEmpty())
    }

    @Test fun `registration during polling blocks retry until return and then queries only the original purchase`() = runTest {
        submitResult = AppResult.Success(PurchaseSubmission(91, 1, 2, 2))
        progressResults.add(AppResult.Success(PurchaseProgress(91, 0, notice = PurchaseNotice(PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION))))
        val vm = submitReadyViewModel()
        vm.submit(); advanceUntilIdle()
        assertTrue(vm.consumeRegistrationNotice(requireNotNull(vm.state.value.notice)))
        vm.resumeAfterRedirect(); vm.retry(); vm.submit(); advanceUntilIdle()
        assertEquals(listOf(91L), polledIds)
        vm.registrationFinished(); advanceUntilIdle()
        assertEquals(listOf(91L), polledIds)
        progressResults.add(AppResult.Success(PurchaseProgress(91, 8)))
        vm.retry(); advanceUntilIdle()
        assertEquals(listOf(91L, 91L), polledIds)
        assertEquals(1, submitCalls)
    }

    @Test fun `submission keeps the original form snapshot while cart and post are suspended`() = runTest {
        val cart = CompletableDeferred<AppResult<ShoppingCart>>()
        val post = CompletableDeferred<AppResult<CardSubmissionOutcome>>()
        val requests = mutableListOf<CardPaymentRequest>()
        val model = viewModel(cart = GetCurrentCartUseCase { cart.await() },
            submission = SubmitCardPaymentUseCase { requests += it; post.await() })
        validVisa(model); model.toggleSave()
        model.continueFromCard(); model.changeCvv("123"); model.continueFromCvv()
        model.submit(); model.submit()
        assertEquals(PaymentCardStage.PROCESSING, model.state.value.stage)
        model.changeNumber("5555 5555 5555 4444"); model.changeHolderName("Outro nome")
        model.changeMonth("01"); model.changeYear("2031"); model.toggleSave(); model.changeCvv("999")
        val reload: Job = model.load()
        reload.join()
        assertTrue(requests.isEmpty())
        cart.complete(AppResult.Success(ShoppingCart(1, BigDecimal("30.50"), emptyList())))
        runCurrent()
        assertEquals(1, requests.size)
        val request = requests.single()
        assertEquals(1L, request.paymentMethodId)
        assertEquals("123", request.securityCode)
        val draft = (request.selection as PaymentCardSelection.New).draft
        assertEquals("4111111111111111", draft.number)
        assertEquals("PESSOA", draft.holderName)
        assertEquals(12, draft.expirationMonth)
        assertEquals(2030, draft.expirationYear)
        assertTrue(draft.save)
        model.submit()
        assertEquals(1, requests.size)
        post.complete(AppResult.Success(CardSubmissionOutcome.Submitted(PurchaseSubmission(91, 0, 0, 8))))
        advanceUntilIdle()
        assertEquals(PaymentCardStage.SUCCESS, model.state.value.stage)
        assertEquals(listOf(BigDecimal("30.50")), recordedAmounts)
        assertEquals(0, progressCalls)
    }

    @Test fun `clearing viewmodel cancels saved card loads including the public load job`() = runTest {
        val result = CompletableDeferred<AppResult<List<SavedPaymentCard>>>()
        var cancelled = 0
        val model = viewModel(savedCardId = "saved", cards = GetSavedCardsUseCase {
            try { result.await() } finally { cancelled++ }
        })
        val store = ViewModelStore().apply { put("card", model) }
        val loadJob: Job = model.load()
        assertEquals(PaymentCardStage.LOADING, model.state.value.stage)
        assertFalse(loadJob.isCompleted)
        val snapshot = model.state.value
        store.clear(); runCurrent()
        assertEquals(2, cancelled)
        assertTrue(loadJob.isCancelled)
        result.complete(AppResult.Success(listOf(saved))); advanceUntilIdle()
        assertEquals(snapshot, model.state.value)
        assertEquals(0, submitCalls)
    }

    @Test fun `clearing viewmodel cancels preparation or submission without recording a purchase`() = runTest {
        for (waitForCart in listOf(true, false)) {
            val pending = CompletableDeferred<Unit>()
            var cancelled = false
            var posts = 0
            suspend fun waitForResult() { try { pending.await() } finally { cancelled = true } }
            val model = viewModel(cart = GetCurrentCartUseCase {
                if (waitForCart) waitForResult()
                AppResult.Success(ShoppingCart(1, BigDecimal("30.50"), emptyList()))
            }, submission = SubmitCardPaymentUseCase {
                posts++
                if (!waitForCart) waitForResult()
                AppResult.Success(CardSubmissionOutcome.Submitted(PurchaseSubmission(91, 0, 0, 8)))
            })
            val store = ViewModelStore().apply { put("card", model) }
            validVisa(model); model.continueFromCard(); model.changeCvv("123"); model.continueFromCvv(); model.submit()
            assertEquals(PaymentCardStage.PROCESSING, model.state.value.stage)
            val snapshot = model.state.value
            assertEquals(if (waitForCart) 0 else 1, posts)
            store.clear(); runCurrent()
            assertTrue(cancelled)
            pending.complete(Unit); advanceUntilIdle()
            assertEquals(snapshot, model.state.value)
            assertEquals(if (waitForCart) 0 else 1, posts)
            assertTrue(recordedAmounts.isEmpty())
            assertEquals(0, progressCalls)
        }
    }

    @Test fun `clearing viewmodel cancels polling during its delay or pending query`() = runTest {
        for (duringQuery in listOf(false, true)) {
            val pending = CompletableDeferred<AppResult<PurchaseProgress>>()
            val queries = mutableListOf<Long>()
            var queryCancelled = false
            val model = viewModel(
                submission = SubmitCardPaymentUseCase {
                    AppResult.Success(CardSubmissionOutcome.Submitted(PurchaseSubmission(91, 1000, 2, 2)))
                }, progress = GetPurchaseProgressUseCase {
                    queries += it
                    try { pending.await() } finally { queryCancelled = true }
                })
            val store = ViewModelStore().apply { put("card", model) }
            validVisa(model); model.continueFromCard(); model.changeCvv("123"); model.continueFromCvv(); model.submit()
            assertTrue(model.state.value.trackingPurchase)
            assertEquals("", model.state.value.cvv)
            if (duringQuery) { advanceTimeBy(1000); runCurrent() }
            assertEquals(if (duringQuery) listOf(91L) else emptyList<Long>(), queries)
            val snapshot = model.state.value
            store.clear(); runCurrent()
            assertEquals(duringQuery, queryCancelled)
            pending.complete(AppResult.Success(PurchaseProgress(91, 8))); advanceUntilIdle()
            assertEquals(snapshot, model.state.value)
            assertEquals(if (duringQuery) listOf(91L) else emptyList<Long>(), queries)
            assertTrue(recordedAmounts.isEmpty())
        }
    }

    @Test fun `clearing during local purchase recording does not publish a late success`() = runTest {
        val pending = CompletableDeferred<AppResult<Unit>>()
        var cancelled = false
        val amounts = mutableListOf<BigDecimal>()
        val model = viewModel(
            submission = SubmitCardPaymentUseCase {
                AppResult.Success(CardSubmissionOutcome.Submitted(PurchaseSubmission(91, 0, 0, 8)))
            }, record = RecordRecentPurchaseUseCase {
                amounts += it
                try { pending.await() } finally { cancelled = true }
            })
        val store = ViewModelStore().apply { put("card", model) }
        validVisa(model); model.continueFromCard(); model.changeCvv("123"); model.continueFromCvv(); model.submit()
        assertEquals(listOf(BigDecimal("30.50")), amounts)
        assertEquals(PaymentCardStage.PROCESSING, model.state.value.stage)
        val snapshot = model.state.value
        store.clear(); runCurrent()
        assertTrue(cancelled)
        pending.complete(AppResult.Success(Unit)); advanceUntilIdle()
        assertEquals(snapshot, model.state.value)
        assertEquals(listOf(BigDecimal("30.50")), amounts)
    }

    private fun validVisa(viewModel: PaymentCardViewModel) {
        viewModel.changeNumber("4111 1111 1111 1111")
        viewModel.changeHolderName("Pessoa")
        viewModel.changeMonth("12")
        viewModel.changeYear("2030")
    }

    private fun viewModel(
        savedCardId: String? = null,
        paymentMethodId: Long = 1,
        currentYear: Int = 2026,
        initialStage: PaymentCardStage = PaymentCardStage.FORM,
        cards: GetSavedCardsUseCase? = null,
        cart: GetCurrentCartUseCase? = null,
        submission: SubmitCardPaymentUseCase? = null,
        progress: GetPurchaseProgressUseCase? = null,
        record: RecordRecentPurchaseUseCase? = null,
    ): PaymentCardViewModel {
        val dispatcher = UnconfinedTestDispatcher()
        return PaymentCardViewModel(
            clock = java.time.Clock.fixed(java.time.Instant.parse("2026-09-07T15:00:00Z"), java.time.ZoneId.of("America/Sao_Paulo")),
            getCurrentCart = cart ?: br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase { cartResult },
            recordRecentPurchase = record ?: br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase {
                recordedAmounts += it
                recordResult
            },
            paymentMethodId = paymentMethodId,
            savedCardId = savedCardId,
            initialStage = initialStage,
            getCards = cards ?: GetSavedCardsUseCase { cardsResult },
            submitCardPayment = submission ?: SubmitCardPaymentUseCase {
                submitCalls++; submitted = it
                outcomeOverride ?: when (val result = submitResult) {
                    is AppResult.Failure -> result
                    is AppResult.Success -> AppResult.Success(CardSubmissionOutcome.Submitted(result.value))
                }
            },
            getPurchaseProgress = progress ?: GetPurchaseProgressUseCase {
                progressCalls++
                polledIds += it
                progressResults.removeFirstOrNull() ?: AppResult.Success(PurchaseProgress(it, 1))
            },
            dispatchers = FixedCoroutineDispatchers(dispatcher),
            currentYear = currentYear,
        )
    }
}
