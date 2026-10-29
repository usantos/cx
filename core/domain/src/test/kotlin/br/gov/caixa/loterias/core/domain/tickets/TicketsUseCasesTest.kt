package br.gov.caixa.loterias.core.domain.tickets

import br.gov.caixa.loterias.core.common.result.AppResult
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicket
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBets
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBets
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetails
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonths
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasePaymentMethods
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseStatuses
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchases
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavorite
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketQuery
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class TicketsUseCasesTest {
    private val bet = ConfirmedBet(
        id = 1,
        modality = "Mega-Sena",
        contest = 2800,
        date = "10/09/2026",
        status = "Premiada",
        numbers = listOf(1, 2, 3, 4, 5, 6),
        prize = BigDecimal.TEN,
        originalRecurringBet = true,
        redeemedPrize = true,
        exchangeBetId = 2,
    )
    private val purchase = Purchase(
        id = 4,
        date = "09/09/2026",
        status = "Confirmada",
        paymentMethod = "PIX",
        total = BigDecimal.TEN,
        pixCopyPaste = "000201",
        paymentExpiresAt = "10/09/2026",
    )

    @Test fun `ticket use cases delegate filters identifiers and trimmed code`() = runTest {
        val repository = FakeRepository(bet, purchase)
        val filters = mapOf("status" to "PREMIADA")

        assertEquals(listOf(bet), (GetConfirmedBets(repository)(filters) as AppResult.Success).value)
        assertEquals(listOf(purchase), (GetPurchases(repository)(filters) as AppResult.Success).value)
        assertEquals(listOf(bet), (GetPurchaseBets(repository)(4) as AppResult.Success).value)
        assertEquals(listOf(bet), (GetPurchaseDetails(repository)(purchase) as AppResult.Success).value.bets)
        assertTrue(SavePurchaseAsFavorite(repository)(4, "  Meu jogo  ", false) is AppResult.Success)
        assertEquals(listOf(bet), (CheckTicket(repository)(TicketQuery(" 123 ", true)) as AppResult.Success).value)
        assertEquals(filters, repository.betFilters)
        assertEquals(filters, repository.purchaseFilters)
        assertEquals(4L, repository.purchaseId)
        assertEquals(Triple(4L, "Meu jogo", false), repository.favoriteRequest)
        assertEquals(TicketQuery("123", true), repository.ticketQuery)
        assertEquals(repository.configuration, (GetConfirmedBetsConfiguration(repository)() as AppResult.Success).value)
        assertEquals(repository.months, (GetPurchaseMonths(repository)() as AppResult.Success).value)
        assertEquals(repository.options, (GetPurchasePaymentMethods(repository)() as AppResult.Success).value)
        assertEquals(repository.options, (GetPurchaseStatuses(repository)(7) as AppResult.Success).value)
        assertEquals(7L, repository.statusPaymentMethod)
    }

    @Test fun `purchase and ticket validation reject invalid input`() = runTest {
        val repository = FakeRepository(bet, purchase)
        assertTrue(GetPurchaseBets(repository)(0) is AppResult.Failure)
        assertTrue(GetPurchaseDetails(repository)(purchase.copy(id = 0)) is AppResult.Failure)
        assertTrue(SavePurchaseAsFavorite(repository)(0, "nome", true) is AppResult.Failure)
        assertTrue(SavePurchaseAsFavorite(repository)(1, " ", true) is AppResult.Failure)
        assertTrue(CheckTicket(repository)(TicketQuery("  ")) is AppResult.Failure)
    }

    @Test fun `repository failures are represented as app failures`() = runTest {
        val repository = FakeRepository(bet, purchase, fail = true)
        assertTrue(GetConfirmedBets(repository)(emptyMap()) is AppResult.Failure)
        assertTrue(GetPurchases(repository)(emptyMap()) is AppResult.Failure)
        assertTrue(GetPurchaseBets(repository)(1) is AppResult.Failure)
        assertTrue(CheckTicket(repository)(TicketQuery("1")) is AppResult.Failure)
        assertTrue(GetConfirmedBetsConfiguration(repository)() is AppResult.Failure)
        assertTrue(GetPurchaseMonths(repository)() is AppResult.Failure)
        assertTrue(GetPurchasePaymentMethods(repository)() is AppResult.Failure)
        assertTrue(GetPurchaseStatuses(repository)(null) is AppResult.Failure)
    }

    private class FakeRepository(
        private val bet: ConfirmedBet,
        private val purchase: Purchase,
        private val fail: Boolean = false,
    ) : TicketsRepository {
        var betFilters: Map<String, String>? = null
        var purchaseFilters: Map<String, String>? = null
        var purchaseId: Long? = null
        var ticketQuery: TicketQuery? = null
        var statusPaymentMethod: Long? = null
        var favoriteRequest: Triple<Long, String, Boolean>? = null
        val configuration = ConfirmedBetsConfiguration(defaultOrdering = 3)
        val months = listOf(PurchaseMonth(9, 2026, "Setembro de 2026"))
        val options = listOf(TicketFilterOption(7, "PIX"))
        private fun verify() { if (fail) error("failure") }
        override suspend fun confirmedBets(filters: Map<String, String>): List<ConfirmedBet> {
            verify()
            betFilters = filters
            return listOf(bet)
        }
        override suspend fun purchases(filters: Map<String, String>): List<Purchase> {
            verify()
            purchaseFilters = filters
            return listOf(purchase)
        }
        override suspend fun purchaseBets(purchaseId: Long): List<ConfirmedBet> {
            verify()
            this.purchaseId = purchaseId
            return listOf(bet)
        }
        override suspend fun savePurchaseAsFavorite(purchaseId: Long, name: String, keepSurprises: Boolean) {
            verify()
            favoriteRequest = Triple(purchaseId, name, keepSurprises)
        }
        override suspend fun checkTicket(query: TicketQuery): List<ConfirmedBet> {
            verify()
            ticketQuery = query
            return listOf(bet)
        }
        override suspend fun confirmedBetsConfiguration() = configuration.also { verify() }
        override suspend fun purchaseMonths() = months.also { verify() }
        override suspend fun purchasePaymentMethods() = options.also { verify() }
        override suspend fun purchaseStatuses(paymentMethodId: Long?) = options.also {
            verify()
            statusPaymentMethod = paymentMethodId
        }
    }
}
