package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.feature.tickets.domain.model.localizedMessage as messageText

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketScanOrientation
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketQuery
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class CheckTicket(private val repository: TicketsRepository) : CheckTicketUseCase {
    override fun prepareScannedQuery(code: String, orientation: TicketScanOrientation): TicketQuery? =
        repository.prepareScannedQuery(code, orientation)

    override suspend fun result(query: TicketQuery) = appResultOf {
        require(query.code.isNotBlank()) { messageText("codigo_do_bilhete_obrigatorio_179100") }
        repository.checkTicketResult(query.copy(code = query.code.trim()))
    }
    override suspend fun invoke(query: TicketQuery) = appResultOf {
        require(query.code.isNotBlank()) { messageText("codigo_do_bilhete_obrigatorio_179100") }
        repository.checkTicket(query.copy(code = query.code.trim()))
    }
}
