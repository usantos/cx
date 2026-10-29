package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

fun interface LoadConfirmedBetUseCase {
    suspend operator fun invoke(bet: ConfirmedBet): br.gov.caixa.loterias.core.domain.service.model.ServiceResult<ConfirmedBet>
}
