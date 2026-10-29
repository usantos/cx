package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class GetPurchaseDetails(private val repository: TicketsRepository) : GetPurchaseDetailsUseCase {
    override suspend fun invoke(purchase: Purchase) = appResultOf {
        require(purchase.id > 0) { messageText("compra_invalida_66f609") }
        repository.purchaseDetails(purchase)
    }
}
