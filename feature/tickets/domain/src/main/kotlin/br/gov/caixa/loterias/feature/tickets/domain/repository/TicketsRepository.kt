package br.gov.caixa.loterias.feature.tickets.domain.repository

import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseDetails
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsPage
import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketScanOrientation
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketQuery

interface TicketsRepository {
    fun prepareScannedQuery(code: String, orientation: TicketScanOrientation): TicketQuery? = TicketQuery(code, true)
    suspend fun confirmedBets(filters: Map<String, String> = emptyMap()): List<ConfirmedBet>
    suspend fun confirmedBetsPage(filters: Map<String, String>): ConfirmedBetsPage = ConfirmedBetsPage(confirmedBets(filters))
    suspend fun recoverConfirmedBet(bet: ConfirmedBet): ConfirmedBet = bet
    suspend fun loadConfirmedBet(bet: ConfirmedBet): br.gov.caixa.loterias.core.domain.service.model.ServiceResult<ConfirmedBet> =
        br.gov.caixa.loterias.core.domain.service.model.ServiceResult.Success(recoverConfirmedBet(bet))
    suspend fun purchases(filters: Map<String, String> = emptyMap()): List<Purchase>
    suspend fun purchaseBets(purchaseId: Long): List<ConfirmedBet>
    suspend fun purchaseDetails(purchase: Purchase): PurchaseDetails =
        PurchaseDetails(purchase, purchaseBets(purchase.id))
    suspend fun savePurchaseAsFavorite(purchaseId: Long, name: String, keepSurprises: Boolean) = Unit
    suspend fun checkTicket(query: TicketQuery): List<ConfirmedBet>
    suspend fun checkTicketResult(query: TicketQuery): CheckedTicketResult = CheckedTicketResult(bets = checkTicket(query))
    suspend fun confirmedBetsConfiguration(): ConfirmedBetsConfiguration
    suspend fun purchaseMonths(): List<PurchaseMonth>
    suspend fun purchasePaymentMethods(): List<TicketFilterOption>
    suspend fun purchaseStatuses(paymentMethodId: Long?): List<TicketFilterOption>
}
