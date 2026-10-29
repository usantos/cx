package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartRepository

class AddFavoriteCartToCurrent(private val repository: FavoriteCartRepository) : AddFavoriteCartToCurrentUseCase {
    override suspend fun invoke(cartId: Long, specialSelection: Int) = appResultOf {
        require(cartId > 0) { messageText("carrinho_favorito_invalido_32aa99") }
        require(specialSelection in 0..2) { messageText("selecao_de_concurso_invalida_d3c4ad") }
        repository.addToCurrentCart(cartId, specialSelection)
    }
}
