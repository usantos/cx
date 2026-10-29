package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.reduceBetDetailState
import br.gov.caixa.loterias.feature.tickets.presentation.state.reduceTicketsState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class TicketsReduxStateReducerTest {
    private val contest = 12
    private val chosenNumbers = listOf(1, 2, 3)

    @Test fun `consuming ticket navigation keeps result data and cannot consume twice`() {
        val bet = ConfirmedBet(1, "Mega-Sena", contest, "date", "paid", chosenNumbers)
        val initial = TicketsUiState(bets = listOf(bet), loaded = true)
        val consumed = reduceTicketsState(initial, TicketsStateAction.TicketNavigationConsumed)
        assertFalse(consumed.loaded)
        assertEquals(initial.bets, consumed.bets)
        assertSame(consumed, reduceTicketsState(consumed, TicketsStateAction.TicketNavigationConsumed))
    }

    @Test fun `pix response from previous purchase cannot clear current check`() {
        val purchase = Purchase(2, "date", "pending", "Pix", BigDecimal.ONE)
        val initial = TicketsUiState(selectedPurchase = purchase, checkingPixPurchase = true)
        assertSame(initial, reduceTicketsState(initial, TicketsStateAction.PixStatusFailed(1, AppError.Network)))
        assertFalse(reduceTicketsState(initial, TicketsStateAction.PixStatusFailed(2, AppError.Network)).checkingPixPurchase)
    }

    @Test fun `paginated purchases are appended once and preserve pix state`() {
        val first = Purchase(1, "date", "paid", "Pix", BigDecimal.ONE)
        val second = first.copy(id = 2)
        val initial = TicketsUiState(purchases = listOf(first), checkingPixPurchase = true)
        val page = reduceTicketsState(initial, TicketsStateAction.PurchasePageReceived(true, listOf(first, second)))
        assertEquals(listOf(first, second), page.purchases)
        assertTrue(page.checkingPixPurchase)
    }

    @Test fun `invalid repeat selection preserves detail state`() {
        val bet = ConfirmedBet(1, "Mega-Sena", contest, "date", "paid", chosenNumbers)
        val initial = BetDetailUiState(bet)
        assertSame(initial, reduceBetDetailState(initial, BetDetailStateAction.RepeatContestSelected(2)))
    }
}
