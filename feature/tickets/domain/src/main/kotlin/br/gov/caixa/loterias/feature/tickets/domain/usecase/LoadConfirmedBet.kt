package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class LoadConfirmedBet(private val repository: TicketsRepository) : LoadConfirmedBetUseCase {
    override suspend fun invoke(bet: ConfirmedBet) = repository.loadConfirmedBet(bet)
}
