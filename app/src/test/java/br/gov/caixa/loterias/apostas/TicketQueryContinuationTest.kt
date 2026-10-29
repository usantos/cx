package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.navigation.state.authenticatedContinuation
import br.gov.caixa.loterias.core.navigation.state.ticketQueryContinuation
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize
import org.junit.Assert.*
import org.junit.Test

class TicketQueryContinuationTest {
    @Test fun `ticket service authentication resumes a fresh owned list query`() {
        assertEquals("tickets/my-bets", ticketQueryContinuation())
        assertFalse(ticketQueryContinuation().startsWith("retail/prize"))
        assertEquals("tickets/my-bets", authenticatedContinuation(ticketQueryContinuation(), "old-owner", "new-owner"))
    }
}
