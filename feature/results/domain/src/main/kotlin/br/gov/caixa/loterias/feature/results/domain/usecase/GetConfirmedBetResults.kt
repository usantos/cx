package br.gov.caixa.loterias.feature.results.domain.usecase

import br.gov.caixa.loterias.feature.results.domain.repository.ResultsRepository

class GetConfirmedBetResults(private val repository: ResultsRepository) : GetConfirmedBetResultsUseCase {
    override suspend fun invoke(bet: br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet) = repository.forConfirmedBet(bet)
}
