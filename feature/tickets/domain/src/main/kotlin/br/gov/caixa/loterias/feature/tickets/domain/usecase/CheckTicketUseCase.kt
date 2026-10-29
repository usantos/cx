package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketScanOrientation
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketQuery

fun interface CheckTicketUseCase {
    fun prepareScannedQuery(code: String, orientation: TicketScanOrientation): TicketQuery? = TicketQuery(code, true)
    suspend operator fun invoke(query: TicketQuery): AppResult<List<ConfirmedBet>>
    suspend fun result(query: TicketQuery): AppResult<CheckedTicketResult> = when (val result = invoke(query)) {
        is AppResult.Success -> AppResult.Success(CheckedTicketResult(bets = result.value))
        is AppResult.Failure -> result
    }
}
