package br.gov.caixa.loterias.feature.marketplace.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolOrder
import br.gov.caixa.loterias.feature.marketplace.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplacePurchaseRepository

class AddMarketplacePoolToCart(private val repository: MarketplacePurchaseRepository) : AddMarketplacePoolToCartUseCase {
    override suspend fun invoke(order: MarketplacePoolOrder) = appResultOf {
        require(order.code.isNotBlank()) { messageText("codigo_do_bolao_obrigatorio_4b3ef2") }
        require(order.modality.isNotBlank()) { messageText("modalidade_do_bolao_obrigatoria_65e63e") }
        require(order.contest > 0) { messageText("concurso_do_bolao_invalido_639982") }
        require(order.requestedShares in 1..order.availableShares) { messageText("quantidade_de_cotas_indisponivel_5f2f0a") }
        require(order.firstSharePriceWithFee.signum() > 0) { messageText("valor_da_cota_invalido_ab7cb7") }
        repository.addToCart(order)
    }
}
