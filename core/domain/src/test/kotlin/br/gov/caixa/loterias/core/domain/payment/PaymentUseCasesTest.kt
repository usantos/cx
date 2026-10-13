package br.gov.caixa.loterias.core.domain.payment

import br.gov.caixa.loterias.core.common.AppResult
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentUseCasesTest {
    @Test fun `business notices survive use cases and suppress terminal payment classifiers`() = runTest {
        val request = CardPaymentRequest(1, PaymentCardSelection.Saved(card()), "123")
        for (reason in PurchaseNoticeReason.entries) {
            val notice = PurchaseNotice(reason, "Mensagem", "E1")
            for (status in listOf(3, 8)) {
                val progress = PurchaseProgress(91, status, notice = notice)
                assertFalse(progress.completed); assertFalse(progress.rejected)
                val repository = object : PaymentRepository by FakeRepository() {
                    override suspend fun submitCardPayment(request: CardPaymentRequest) = CardSubmissionOutcome.NoticeRequired(notice)
                    override suspend fun purchaseProgress(purchaseId: Long) = progress
                }
                assertEquals(AppResult.Success(CardSubmissionOutcome.NoticeRequired(notice)), SubmitCardPayment(repository)(request))
                assertEquals(AppResult.Success(progress), GetPurchaseProgress(repository)(91))
            }
        }
    }
    @Test fun `redirect is never approval or rejection even if payload contains terminal status`() = runTest {
        val request = CardPaymentRequest(1, PaymentCardSelection.Saved(card()), "123")
        for (destination in PurchaseRedirect.entries) {
            for (status in listOf(2, 3, 8)) {
                val progress = PurchaseProgress(91, status, redirect = destination)
                assertFalse(progress.completed)
                assertFalse(progress.rejected)
            }
            val expected = CardSubmissionOutcome.RedirectRequired(destination)
            val repository = object : PaymentRepository by FakeRepository() {
                override suspend fun submitCardPayment(request: CardPaymentRequest) = expected
            }
            assertEquals(AppResult.Success(expected), SubmitCardPayment(repository)(request))
        }
    }
    @Test fun `review requirement survives the use case instead of becoming a retryable failure`() = runTest {
        val request = CardPaymentRequest(1, PaymentCardSelection.Saved(card()), "123")
        for (id in listOf(null, 91L)) {
            val repository = object : PaymentRepository by FakeRepository() {
                override suspend fun submitCardPayment(request: CardPaymentRequest) = CardSubmissionOutcome.ReviewRequired(id)
            }
            val outcome = SubmitCardPayment(repository)(request)
            assertEquals(AppResult.Success(CardSubmissionOutcome.ReviewRequired(id)), outcome)
        }
    }
    @Test fun `models expose legacy payment semantics`() {
        assertTrue(PaymentMethod(PaymentMethod.PIX_ID, "Pix").isPix)
        assertFalse(PaymentMethod(PaymentMethod.MERCADO_PAGO_ID, "Cartão").isPix)
        assertFalse(PurchaseProgress(1, 2).completed)
        assertFalse(PurchaseProgress(1, 2).rejected)
        assertTrue(PurchaseProgress(1, 3).rejected)
        assertEquals("card", card().deletionIdentifier)
        assertEquals(
            "customer-token",
            card().copy(paymentMethodId = PaymentMethod.RECARGA_PAY_ID, customerToken = "customer-token").deletionIdentifier,
        )
    }

    @Test fun `all legacy purchase statuses have the same meaning in submission and polling`() {
        val completed = setOf(6, 8, 13, 14)
        val negative = setOf(3, 4, 5, 11, 12)
        for (status in (-1..15) + listOf(Int.MIN_VALUE, Int.MAX_VALUE)) {
            val progress = PurchaseProgress(1, status)
            val submission = PurchaseSubmission(1, 1000, 3, status)
            assertEquals("Polling status $status", status in completed, progress.completed)
            assertEquals("Submission status $status", status in completed, submission.completed)
            assertEquals("Polling status $status", status in negative, progress.rejected)
            assertEquals("Submission status $status", status in negative, submission.rejected)
        }
    }

    @Test fun `valid operations delegate and preserve values`() = runTest {
        val repository = FakeRepository()
        assertEquals(listOf(1L, 5L), (GetPaymentMethods(repository)() as AppResult.Success).value.map { it.id })
        assertEquals("card", (GetSavedCards(repository)(1) as AppResult.Success).value.single().id)
        assertEquals("card", (DeleteSavedCard(repository)("card", 5) as AppResult.Success).value.single().id)
        assertEquals(9L, (GeneratePixCharge(repository)() as AppResult.Success).value.purchaseId)
        assertEquals(2, (GetPurchaseProgress(repository)(9) as AppResult.Success).value.status)
        assertTrue((ValidateDam(repository)() as AppResult.Success).value.blocked)
        val request = CardPaymentRequest(1, PaymentCardSelection.New(PaymentCardDraft("4111111111111111", "PESSOA", 12, 2030, true)), "123")
        assertEquals(9L, ((SubmitCardPayment(repository)(request) as AppResult.Success).value as CardSubmissionOutcome.Submitted).purchase.purchaseId)
        assertEquals(5L, repository.lastMethod)
        assertEquals("card", repository.deleted)
    }

    @Test fun `invalid identifiers fail before repository`() = runTest {
        val repository = FakeRepository()
        assertTrue(GetSavedCards(repository)(6) is AppResult.Failure)
        assertTrue(DeleteSavedCard(repository)("", 1) is AppResult.Failure)
        assertTrue(DeleteSavedCard(repository)("card", 6) is AppResult.Failure)
        assertTrue(GetPurchaseProgress(repository)(0) is AppResult.Failure)
        assertTrue(SubmitCardPayment(repository)(CardPaymentRequest(6, PaymentCardSelection.New(PaymentCardDraft("4111111111111111", "P", 1, 2030, false)), "123")) is AppResult.Failure)
        assertTrue(SubmitCardPayment(repository)(CardPaymentRequest(1, PaymentCardSelection.New(PaymentCardDraft("1", "", 0, 0, false)), "12")) is AppResult.Failure)
        assertTrue(SubmitCardPayment(repository)(CardPaymentRequest(1, PaymentCardSelection.Saved(card()), "12")) is AppResult.Failure)
    }

    @Test fun `repository exceptions are converted to failures`() = runTest {
        val repository = FakeRepository(fail = true)
        assertTrue(GetPaymentMethods(repository)() is AppResult.Failure)
        assertTrue(GetSavedCards(repository)(1) is AppResult.Failure)
        assertTrue(DeleteSavedCard(repository)("card", 1) is AppResult.Failure)
        assertTrue(GeneratePixCharge(repository)() is AppResult.Failure)
        assertTrue(GetPurchaseProgress(repository)(1) is AppResult.Failure)
        assertTrue(ValidateDam(repository)() is AppResult.Failure)
        assertTrue(SubmitCardPayment(repository)(CardPaymentRequest(1, PaymentCardSelection.Saved(card()), "123")) is AppResult.Failure)
    }

    @Test fun `card brand detection preserves the legacy BIN rules`() {
        assertEquals("ELO", cardBrand("4011781234567890"))
        assertEquals("MASTERCARD", cardBrand("2221001234567890"))
        assertEquals("MASTERCARD", cardBrand("5063121234567890"))
        assertEquals("GRANDCARD", cardBrand("6050321234567890"))
        assertEquals("TROY", cardBrand("9792123412341234"))
        assertEquals("VERVE", cardBrand("5067999912341234"))
        assertEquals("CABAL", cardBrand("6041234512341234"))
        assertEquals("HIPERCARD", cardBrand("6062123412341234"))
        assertEquals("AMERICANEXPRESS", cardBrand("371234567890123"))
        assertEquals("DINERS", cardBrand("36123456789012"))
        assertEquals("JCB", cardBrand("3512345678901234"))
        assertEquals("DISCOVER", cardBrand("6011123412341234"))
        assertEquals("VISA", cardBrand("4111111111111111"))
        assertEquals("VISA", cardBrand("unknown"))
    }

    @Test fun `new card keeps the legacy maximum of sixteen digits`() = runTest {
        val repository = FakeRepository()
        val selection = PaymentCardSelection.New(PaymentCardDraft("41111111111111111", "PESSOA", 12, 2030, false))
        assertTrue(SubmitCardPayment(repository)(CardPaymentRequest(1, selection, "123")) is AppResult.Failure)
    }

    private class FakeRepository(private val fail: Boolean = false) : PaymentRepository {
        var lastMethod = 0L
        var deleted = ""
        private fun check() { if (fail) error("failure") }
        override suspend fun methods() = listOf(
            PaymentMethod(0, "Inválido"),
            PaymentMethod(1, "Mercado Pago"),
            PaymentMethod(5, "RecargaPay"),
        ).also { check() }
        override suspend fun cards(paymentMethodId: Long) = listOf(card()).also { check(); lastMethod = paymentMethodId }
        override suspend fun deleteCard(cardId: String, paymentMethodId: Long) = listOf(card()).also {
            check(); deleted = cardId; lastMethod = paymentMethodId
        }
        override suspend fun generatePix() = pix().also { check() }
        override suspend fun purchaseProgress(purchaseId: Long) = PurchaseProgress(purchaseId, 2).also { check() }
        override suspend fun validateDam() = DamValidation(true, "Indisponível").also { check() }
        override suspend fun submitCardPayment(request: CardPaymentRequest) = CardSubmissionOutcome.Submitted(PurchaseSubmission(9, 1, 1, 2)).also { check() }
        private fun card() = Companion.card()
        private fun pix() = PixCharge("code", "tx", "qr", "created", "expires", 9, BigDecimal.TEN)
    }

    companion object {
        private fun card() = SavedPaymentCard("card", 1, "411111", "1111", 1, 2030, 3, "Visa", "visa")
    }
}
