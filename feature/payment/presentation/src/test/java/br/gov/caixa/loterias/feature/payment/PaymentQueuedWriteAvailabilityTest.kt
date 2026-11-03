package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.model.DamValidation
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.PixCharge
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseSubmission
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.SubmitCardPaymentUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentCardViewModel
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentViewModel
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentQueuedWriteAvailabilityTest {
    private val cart = ShoppingCart(1, BigDecimal.TEN, emptyList())
    private val card = SavedPaymentCard("card", 1, "411111", "1111", 12, EXPIRATION_YEAR, 3, "Visa", "visa")
    private val clock = Clock.fixed(Instant.parse("2026-09-07T15:00:00Z"), ZoneOffset.UTC)
    private val pix = PixCharge("synthetic-code", "synthetic-tx", "", "now", "later", PURCHASE_ID, BigDecimal.TEN)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `disabling before queued card work runs preserves draft and sends no payment`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var submissions = 0
        val model = cardModel(dispatcher, { enabled }, submit = {
            submissions++
            submitted()
        })
        runCurrent()
        fillCard(model)
        val draft = model.state.value
        model.submit()
        enabled = false
        runCurrent()
        assertEquals(0, submissions)
        assertEquals(draft, model.state.value)
    }

    @Test fun `disabling while cart read is suspended blocks payment and preserves draft`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var submissions = 0
        val cartResponse = CompletableDeferred<AppResult<ShoppingCart>>()
        val model = cardModel(dispatcher, { enabled }, getCart = { cartResponse.await() }, submit = {
            submissions++
            submitted()
        })
        runCurrent()
        fillCard(model)
        val draft = model.state.value
        model.submit()
        runCurrent()
        enabled = false
        cartResponse.complete(AppResult.Success(cart))
        runCurrent()
        assertEquals(0, submissions)
        assertEquals(draft, model.state.value)
    }

    @Test fun `disabling after card submission started preserves completion without another payment`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var submissions = 0
        var receipts = 0
        val response = CompletableDeferred<AppResult<CardSubmissionOutcome>>()
        val model = cardModel(dispatcher, { enabled }, submit = {
            submissions++
            response.await()
        }, record = { receipts++ })
        runCurrent()
        fillCard(model)
        model.submit()
        runCurrent()
        assertEquals(1, submissions)
        enabled = false
        response.complete(submitted())
        runCurrent()
        model.submit()
        runCurrent()
        assertEquals(1, submissions)
        assertEquals(1, receipts)
        assertEquals(PaymentCardStage.SUCCESS, model.state.value.stage)
    }

    @Test fun `disabling before queued deletion runs preserves candidate and deletes nothing`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var deletions = 0
        val model =
            paymentModel(dispatcher, PaymentMode.CARDS, { enabled }, delete = { _, _ ->
                deletions++
                AppResult.Success(emptyList())
            })
        model.requestDelete(card)
        val before = model.state.value
        model.confirmDelete()
        enabled = false
        runCurrent()
        assertEquals(0, deletions)
        assertEquals(before, model.state.value)
    }

    @Test fun `ending session before queued deletion runs preserves candidate and deletes nothing`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var currentSession = true
        var deletions = 0
        val model = paymentModel(
            dispatcher,
            PaymentMode.CARDS,
            { true },
            delete = { _, _ ->
                deletions++
                AppResult.Success(emptyList())
            },
            currentSession = { currentSession }
        )
        model.requestDelete(card)
        model.confirmDelete()
        currentSession = false
        runCurrent()
        assertEquals(0, deletions)
        assertEquals(card, model.state.value.deleteCandidate)
    }

    @Test fun `disabling after deletion started applies its result without sending it again`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var deletions = 0
        val response = CompletableDeferred<AppResult<List<SavedPaymentCard>>>()
        val model = paymentModel(dispatcher, PaymentMode.CARDS, { enabled }, delete = { _, _ ->
            deletions++
            response.await()
        })
        model.requestDelete(card)
        model.confirmDelete()
        runCurrent()
        assertEquals(1, deletions)
        enabled = false
        response.complete(AppResult.Success(emptyList()))
        runCurrent()
        model.confirmDelete()
        runCurrent()
        assertEquals(1, deletions)
        assertFalse(model.state.value.loading)
        assertNull(model.state.value.deleteCandidate)
        assertEquals(emptyList<SavedPaymentCard>(), model.state.value.cards[card.paymentMethodId])
    }

    @Test fun `queued pix generation rechecks availability before sending`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var charges = 0
        val model = paymentModel(dispatcher, PaymentMode.PIX, { enabled }, generate = {
            charges++
            AppResult.Success(pix)
        })
        model.load()
        enabled = false
        runCurrent()
        assertEquals(0, charges)
        assertNull(model.state.value.pix)
    }

    @Test fun `an already sent pix completes after disabling and is not sent twice`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        var enabled = true
        var charges = 0
        val response = CompletableDeferred<AppResult<PixCharge>>()
        val model = paymentModel(dispatcher, PaymentMode.PIX, { enabled }, generate = {
            charges++
            response.await()
        })
        model.load()
        runCurrent()
        assertEquals(1, charges)
        enabled = false
        response.complete(AppResult.Success(pix))
        runCurrent()
        model.load()
        runCurrent()
        assertEquals(1, charges)
        assertEquals(pix, model.state.value.pix)
    }

    private fun cardModel(
        dispatcher: CoroutineDispatcher,
        allowed: () -> Boolean,
        getCart: GetCurrentCartUseCase = GetCurrentCartUseCase { AppResult.Success(cart) },
        submit: SubmitCardPaymentUseCase,
        record: () -> Unit = {},
    ) = PaymentCardViewModel(
        paymentMethodId = 1, savedCardId = null,
        getCards = GetSavedCardsUseCase { AppResult.Success(emptyList()) },
        getCurrentCart = getCart, submitCardPayment = submit,
        getPurchaseProgress = GetPurchaseProgressUseCase { error("No extra polling expected") },
        recordRecentPurchase = RecordRecentPurchaseUseCase {
            record()
            AppResult.Success(Unit)
        },
        clock = clock, dispatchers = FixedCoroutineDispatchers(dispatcher), canStartOperation = allowed
    )

    private fun paymentModel(
        dispatcher: CoroutineDispatcher,
        mode: PaymentMode,
        allowed: () -> Boolean,
        delete: DeleteSavedCardUseCase = DeleteSavedCardUseCase { _, _ -> error("No deletion expected") },
        generate: GeneratePixChargeUseCase = GeneratePixChargeUseCase { error("No charge expected") },
        currentSession: () -> Boolean = { true },
    ) = PaymentViewModel(
        mode = mode, autoLoad = false,
        getMethods = GetPaymentMethodsUseCase { AppResult.Success(listOf(PaymentMethod(1, "Mercado Pago"))) },
        getCards = GetSavedCardsUseCase { AppResult.Success(listOf(card)) }, deleteCard = delete,
        generatePixCharge = generate,
        getPurchaseProgress = GetPurchaseProgressUseCase {
            AppResult.Success(PurchaseProgress(PURCHASE_ID, 1))
        },
        validateDam = ValidateDamUseCase { AppResult.Success(DamValidation(false, "")) },
        getCurrentCart = GetCurrentCartUseCase { AppResult.Success(cart) },
        recordRecentPurchase = RecordRecentPurchaseUseCase { AppResult.Success(Unit) },
        clock = clock, dispatchers = FixedCoroutineDispatchers(dispatcher), canStartOperation = allowed, isSessionCurrent = currentSession
    )

    private fun fillCard(model: PaymentCardViewModel) {
        model.changeNumber("4111111111111111")
        model.changeHolderName("Synthetic User")
        model.changeMonth("12")
        model.changeYear(EXPIRATION_YEAR.toString())
        model.continueFromCard()
        model.changeCvv("123")
        model.continueFromCvv()
        assertEquals(PaymentCardStage.CONFIRM, model.state.value.stage)
    }

    private fun submitted(): AppResult<CardSubmissionOutcome> =
        AppResult.Success(CardSubmissionOutcome.Submitted(PurchaseSubmission(PURCHASE_ID, 0, 0, 1)))
}

private const val EXPIRATION_YEAR = 2030
private const val PURCHASE_ID = 9L
