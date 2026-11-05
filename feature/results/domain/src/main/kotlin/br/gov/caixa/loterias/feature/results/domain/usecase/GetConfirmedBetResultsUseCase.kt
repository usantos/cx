package br.gov.caixa.loterias.feature.results.domain.usecase

import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

fun interface GetConfirmedBetResultsUseCase {
    suspend operator fun invoke(bet: ConfirmedBet): ServiceResult<List<DrawResult>>
}
