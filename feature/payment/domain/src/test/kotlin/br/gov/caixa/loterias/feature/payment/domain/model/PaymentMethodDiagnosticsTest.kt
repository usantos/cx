package br.gov.caixa.loterias.feature.payment.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentMethodDiagnosticsTest {
    private fun method() = PaymentMethod(
        id = PaymentMethod.MERCADO_PAGO_ID,
        purchaseName = "test-purchase-config",
        prizeName = "test-prize-config",
        default = true,
        publicKey = "test-public-key-config"
    )

    @Test fun `diagnostics omit payment configuration contents`() {
        val method = method()
        val diagnostic = method.toString()
        assertFalse(diagnostic.contains(method.publicKey))
        assertFalse(diagnostic.contains(method.purchaseName))
        assertFalse(diagnostic.contains(method.prizeName))
    }

    @Test fun `payment capabilities and configuration remain intact`() {
        val method = method()
        assertEquals(method, method.copy())
        assertEquals("test-public-key-config", method.publicKey)
        assertTrue(method.isSupportedCard)
        assertTrue(method.default)
        assertFalse(method.isPix)
    }
}
