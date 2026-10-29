package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.tickets.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class GetPurchaseBets(private val repository: TicketsRepository) : GetPurchaseBetsUseCase {
    override suspend fun invoke(purchaseId: Long) = appResultOf {
        require(purchaseId > 0) { messageText("compra_invalida_66f609") }
        repository.purchaseBets(purchaseId)
    }
}
