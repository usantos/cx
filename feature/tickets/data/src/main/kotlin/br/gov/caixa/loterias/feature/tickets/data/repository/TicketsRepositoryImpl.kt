package br.gov.caixa.loterias.feature.tickets.data.repository

import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseDetails
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.model.PreparedTicketScan
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketScanOrientation
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.prepareLegacyTicketPayload
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketQuery
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.ConfirmedBetReceiptLoader
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.RsaTicketCodeEncoder
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketCodeEncoder
import br.gov.caixa.loterias.feature.tickets.data.mapper.ticketFindFirstText
import br.gov.caixa.loterias.feature.tickets.data.mapper.toBets
import br.gov.caixa.loterias.feature.tickets.data.mapper.toCheckedTicketResult
import br.gov.caixa.loterias.feature.tickets.data.mapper.toConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.data.mapper.toFilterOptions
import br.gov.caixa.loterias.feature.tickets.data.mapper.toPurchaseMonths
import br.gov.caixa.loterias.feature.tickets.data.mapper.toPurchases
import br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketsRemoteDataSource
import br.gov.caixa.loterias.core.data.mapper.serviceResultOf

class TicketsRepositoryImpl(
    private val remote: TicketsRemoteDataSource,
    private val ticketCodeEncoder: TicketCodeEncoder = RsaTicketCodeEncoder,
) : TicketsRepository {
    private val receiptLoader = ConfirmedBetReceiptLoader(remote)

    override suspend fun confirmedBets(filters: Map<String, String>): List<ConfirmedBet> =
        remote.confirmedBets(filters).toBets()

    override suspend fun confirmedBetsPage(filters: Map<String, String>): br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsPage {
        val bets = remote.confirmedBets(filters).toBets().map { it.copy(backend = remote.confirmedBetsBackend()) }
        return br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsPage(bets, remote.confirmedBetsBackend())
    }

    override suspend fun recoverConfirmedBet(bet: ConfirmedBet): ConfirmedBet =
        remote.recoverConfirmedBet(bet.id).toBets().firstOrNull() ?: bet

    override suspend fun loadConfirmedBet(bet: ConfirmedBet) = serviceResultOf {
        receiptLoader.load(bet)
    }

    override suspend fun purchases(filters: Map<String, String>): List<Purchase> =
        remote.purchases(filters).toPurchases()

    override suspend fun purchaseBets(purchaseId: Long): List<ConfirmedBet> =
        remote.purchaseBets(purchaseId).toBets()

    override suspend fun purchaseDetails(purchase: Purchase): PurchaseDetails {
        val payload = remote.purchaseBets(purchase.id)
        return PurchaseDetails(
            purchase = payload.toPurchases().firstOrNull { it.id == purchase.id } ?: purchase,
            bets = payload.toBets(),
            serverDateTime = payload.ticketFindFirstText("dataHoraServidor"),
        )
    }

    override suspend fun savePurchaseAsFavorite(purchaseId: Long, name: String, keepSurprises: Boolean) =
        remote.savePurchaseAsFavorite(purchaseId, name, keepSurprises)

    override fun prepareScannedQuery(code: String, orientation: TicketScanOrientation): TicketQuery? {
        if (code.length < 23) return null
        val query = TicketQuery(code, qrCode = true)
        val payload = query.prepareLegacyTicketPayload()
        // O leitor legado retoma a leitura silenciosamente quando o tipo do payload e a orientação divergem.
        if (payload.qrCode != (orientation == TicketScanOrientation.PORTRAIT)) return null
        return query.copy(preparedScan = PreparedTicketScan(payload.value, payload.qrCode))
    }

    override suspend fun checkTicket(query: TicketQuery): List<ConfirmedBet> {
        val encoded = ticketCodeEncoder.encode(query)
        return remote.checkTicket(encoded.value, encoded.qrCode).toBets()
    }

    override suspend fun checkTicketResult(query: TicketQuery): CheckedTicketResult {
        val encoded = ticketCodeEncoder.encode(query)
        return remote.checkTicket(encoded.value, encoded.qrCode).toCheckedTicketResult()
    }

    override suspend fun confirmedBetsConfiguration(): ConfirmedBetsConfiguration =
        remote.confirmedBetsConfiguration().toConfirmedBetsConfiguration()

    override suspend fun purchaseMonths(): List<PurchaseMonth> = remote.purchaseMonths().toPurchaseMonths()

    override suspend fun purchasePaymentMethods(): List<TicketFilterOption> =
        remote.purchasePaymentMethods().toFilterOptions("nomePagamentoPremio", "nomePagamentoCompra")

    override suspend fun purchaseStatuses(paymentMethodId: Long?): List<TicketFilterOption> =
        remote.purchaseStatuses(paymentMethodId).toFilterOptions("descricao")
}
