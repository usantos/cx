package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class RecoverConfirmedBet(private val repository: TicketsRepository) : RecoverConfirmedBetUseCase {
    override suspend fun invoke(bet: ConfirmedBet) = appResultOf {
        require(bet.id > 0) { messageText("aposta_invalida_de9ad6") }
        repository.recoverConfirmedBet(bet)
    }
}
