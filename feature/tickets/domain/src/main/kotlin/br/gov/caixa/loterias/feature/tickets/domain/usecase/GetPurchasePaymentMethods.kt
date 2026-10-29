package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class GetPurchasePaymentMethods(private val repository: TicketsRepository) : GetPurchasePaymentMethodsUseCase {
    override suspend fun invoke() = appResultOf { repository.purchasePaymentMethods() }
}
