package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.tickets.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.domain.repository.TicketsRepository

class SavePurchaseAsFavorite(private val repository: TicketsRepository) : SavePurchaseAsFavoriteUseCase {
    override suspend fun invoke(purchaseId: Long, name: String, keepSurprises: Boolean) = appResultOf {
        require(purchaseId > 0) { messageText("compra_invalida_66f609") }
        require(name.isNotBlank()) { messageText("informe_o_nome_do_carrinho_favor_cbc31f") }
        repository.savePurchaseAsFavorite(purchaseId, name.trim(), keepSurprises)
    }
}
