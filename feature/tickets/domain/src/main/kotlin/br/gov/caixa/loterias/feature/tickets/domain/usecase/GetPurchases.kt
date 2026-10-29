package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class GetPurchases(private val repository: TicketsRepository) : GetPurchasesUseCase {
    override suspend fun invoke(filters: Map<String, String>) = appResultOf { repository.purchases(filters) }
}
