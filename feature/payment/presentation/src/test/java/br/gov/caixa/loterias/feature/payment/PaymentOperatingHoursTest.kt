package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.feature.payment.presentation.mapper.paymentOperatingHours
import org.junit.Assert.assertEquals
import org.junit.Test

class PaymentOperatingHoursTest {
    @Test fun `all operating times are displayed in hours and minutes`() {
        assertEquals(
            "As apostas realizadas entre 22:30 e 05:00 serão processadas após 05:00.",
            paymentOperatingHours("As apostas realizadas entre 22:30:00 e 05:00:00 serão processadas após 05:00:00."),
        )
    }

    @Test fun `already formatted time and surrounding server text are preserved`() {
        assertEquals("Das 22:30 às 05:00.", paymentOperatingHours("Das 22:30 às 05:00."))
        assertEquals("Horário indisponível", paymentOperatingHours("Horário indisponível"))
        assertEquals("", paymentOperatingHours(""))
    }

    @Test fun `nonzero seconds are removed only from valid standalone times`() {
        assertEquals("22:30 / 05:00", paymentOperatingHours("22:30:59 / 05:00:01"))
        assertEquals("25:00:00 / 05:61:00 / 122:30:00", paymentOperatingHours("25:00:00 / 05:61:00 / 122:30:00"))
    }
}
