package br.gov.caixa.loterias.core.domain.marketplace

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart

fun interface SearchMarketplaceUseCase {
    suspend operator fun invoke(filter: MarketplaceFilter, page: Int): AppResult<MarketplacePage>
}

class SearchMarketplace(
    private val repository: MarketplaceRepository,
) : SearchMarketplaceUseCase {
    override suspend fun invoke(filter: MarketplaceFilter, page: Int) = appResultOf {
        repository.search(filter, page)
    }
}

fun interface GetMarketplaceDetailUseCase {
    suspend operator fun invoke(poolCode: String): AppResult<MarketplacePoolDetail>
}

class GetMarketplaceDetail(
    private val repository: MarketplaceDetailRepository,
) : GetMarketplaceDetailUseCase {
    override suspend fun invoke(poolCode: String) = appResultOf {
        repository.detail(poolCode)
    }
}

fun interface AddMarketplacePoolToCartUseCase {
    suspend operator fun invoke(order: MarketplacePoolOrder): AppResult<ShoppingCart>
}

class AddMarketplacePoolToCart(
    private val repository: MarketplacePurchaseRepository,
) : AddMarketplacePoolToCartUseCase {
    override suspend fun invoke(order: MarketplacePoolOrder) = appResultOf {
        require(order.code.isNotBlank()) { "Código do bolão obrigatório" }
        require(order.modality.isNotBlank()) { "Modalidade do bolão obrigatória" }
        require(order.contest > 0) { "Concurso do bolão inválido" }
        require(order.requestedShares in 1..order.availableShares) { "Quantidade de cotas indisponível" }
        require(order.firstSharePriceWithFee.signum() > 0) { "Valor da cota inválido" }
        repository.addToCart(order)
    }
}
