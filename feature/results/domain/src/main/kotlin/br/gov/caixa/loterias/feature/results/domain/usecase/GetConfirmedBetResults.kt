package br.gov.caixa.loterias.feature.results.domain.usecase

import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.results.domain.repository.ResultsRepository

class GetConfirmedBetResults(private val repository: ResultsRepository) : GetConfirmedBetResultsUseCase {
    override suspend fun invoke(bet: ConfirmedBet) = repository.forConfirmedBet(bet)
}
