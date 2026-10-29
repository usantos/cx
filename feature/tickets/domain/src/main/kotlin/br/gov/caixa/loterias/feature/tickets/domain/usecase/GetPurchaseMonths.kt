package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class GetPurchaseMonths(private val repository: TicketsRepository) : GetPurchaseMonthsUseCase {
    override suspend fun invoke() = appResultOf { repository.purchaseMonths() }
}
