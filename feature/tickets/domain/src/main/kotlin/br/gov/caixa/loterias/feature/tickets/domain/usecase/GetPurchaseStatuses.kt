package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class GetPurchaseStatuses(private val repository: TicketsRepository) : GetPurchaseStatusesUseCase {
    override suspend fun invoke(paymentMethodId: Long?) = appResultOf { repository.purchaseStatuses(paymentMethodId) }
}
