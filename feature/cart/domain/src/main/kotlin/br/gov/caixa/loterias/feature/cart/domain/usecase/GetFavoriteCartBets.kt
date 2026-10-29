package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartRepository

class GetFavoriteCartBets(private val repository: FavoriteCartRepository) : GetFavoriteCartBetsUseCase {
    override suspend fun invoke(cartId: Long) = appResultOf {
        require(cartId > 0) { messageText("carrinho_favorito_invalido_32aa99") }
        repository.bets(cartId)
    }
}
