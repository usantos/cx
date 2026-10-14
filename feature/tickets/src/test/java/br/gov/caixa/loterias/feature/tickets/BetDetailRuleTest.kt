package br.gov.caixa.loterias.feature.tickets

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class BetDetailRuleTest {
    @Test fun `notices use real recurring count exchange and paid status with CX branch exclusions`() {
        val bet = br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet(41, "Quina", 7000, "01/10/2026", "Prêmio pago", emptyList(),
            recurringDraws = 3, statusId = 11)
        assertEquals(TeimosinhaNotice.ORIGINAL_PAID, teimosinhaNotice(bet))
        assertEquals(TeimosinhaNotice.FIRST_CONTEST, teimosinhaNotice(bet.copy(exchange = true)))
        assertEquals(null, teimosinhaNotice(bet.copy(recurringDraws = 0)))
        assertEquals(TeimosinhaNotice.FIRST_CONTEST, teimosinhaNotice(bet.copy(statusId = 9, status = "Não premiada")))
        assertEquals(null, teimosinhaNotice(bet.copy(statusId = 4, status = "Concurso não apurado")))
        assertEquals(null, teimosinhaNotice(bet.copy(statusId = 100, status = "Premiada")))
    }

    @Test fun `shows alert only for original teimosinha with redemption`() {
        assertTrue(shouldShowInvalidTeimosinhaAlert(true, true))
        assertFalse(shouldShowInvalidTeimosinhaAlert(true, false))
        assertFalse(shouldShowInvalidTeimosinhaAlert(false, true))
        assertFalse(shouldShowInvalidTeimosinhaAlert(false, false))
    }

    @Test fun `builds typed bet detail route`() {
        assertEquals("tickets/bet-detail?original=true&resgate=false", betDetailRoute(true, false))
    }
}
