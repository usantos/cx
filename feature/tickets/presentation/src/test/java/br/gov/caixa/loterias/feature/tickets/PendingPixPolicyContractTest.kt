package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.isPendingPix
import java.math.BigDecimal
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PendingPixPolicyContractTest {
    private val purchase = Purchase(1, "27/10/2026", "Aguardando pagamento", "Pix", BigDecimal.ONE, pixCopyPaste = "synthetic-code")

    @Test fun idsAndLegacyDisplayFallbackKeepCurrentPendingEligibility() {
        assertTrue(purchase.isPendingPix())
        assertTrue(purchase.copy(status = "", paymentMethod = "", statusId = 10, paymentMethodId = 6).isPendingPix())
        assertTrue(purchase.copy(status = "AGUARDANDO", paymentMethod = "PIX").isPendingPix())
    }

    @Test fun missingCodeAndOtherPaymentOrTerminalStatusCannotQueryPix() {
        assertFalse(purchase.copy(pixCopyPaste = " ").isPendingPix())
        assertFalse(purchase.copy(paymentMethod = "Cartão", paymentMethodId = 1).isPendingPix())
        assertFalse(purchase.copy(status = "Processada", statusId = 6).isPendingPix())
    }
}
