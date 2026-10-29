package br.gov.caixa.loterias.feature.results.domain.usecase

import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

fun interface GetConfirmedBetResultsUseCase {
    suspend operator fun invoke(bet: br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet): br.gov.caixa.loterias.core.domain.service.model.ServiceResult<List<DrawResult>>
}
