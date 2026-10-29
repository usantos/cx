package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardAction
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentCardState
import br.gov.caixa.loterias.feature.payment.presentation.state.reducePaymentState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentReducersTest {
    @Test fun `editing a card clears its previous security code deterministically`() {
        val original = PaymentCardUiState(number = "1234", cvv = "123")
        val action = PaymentCardAction.NumberChanged("12-34 5678")
        val result = reducePaymentCardState(original, action)
        assertEquals("12345678", result.number)
        assertEquals("", result.cvv)
        assertEquals("123", original.cvv)
        assertEquals(result, reducePaymentCardState(original, action))
    }

    @Test fun `a redirected submitted purchase requires review and clears its security code`() {
        val original = PaymentCardUiState(redirectPurchaseId = 1, cvv = "123")
        val result = reducePaymentCardState(original, PaymentCardAction.RedirectConsumed(tracking = false))
        assertEquals(PaymentCardStage.REVIEW_REQUIRED, result.stage)
        assertEquals(1L, result.purchaseReview?.purchaseId)
        assertEquals("", result.cvv)
        assertTrue(result.redirectPaused)
    }

    @Test fun `card deletion keeps cards from other methods`() {
        val original = PaymentUiState(cards = mapOf(1L to emptyList(), 2L to emptyList()))
        val result = reducePaymentState(original, PaymentAction.CardDeleted(1, emptyList()))
        assertEquals(original.cards.keys, result.cards.keys)
        assertFalse(result.loading)
    }

    @Test fun `ambiguous pix generation preserves the recovery requirement`() {
        val original = PaymentUiState(pixRecoveryRequired = true)
        val started = reducePaymentState(original, PaymentAction.PixGenerationStarted)
        val checked = reducePaymentState(started, PaymentAction.PixStatusReceived(completed = false, rejected = true))
        assertTrue(checked.pixRecoveryRequired)
        assertTrue(checked.pixRejected)
        assertFalse(checked.pixApproved)
    }
}
