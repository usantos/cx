package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class GetConfirmedBets(private val repository: TicketsRepository) : GetConfirmedBetsUseCase {
    override suspend fun page(filters: Map<String, String>) = appResultOf { repository.confirmedBetsPage(filters) }
    override suspend fun invoke(filters: Map<String, String>) = appResultOf { repository.confirmedBets(filters) }
}
