package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository

class RemoveCartItem(private val repository: CartRepository) : RemoveCartItemUseCase {
    override suspend fun invoke(item: CartItem) = appResultOf {
        require(item.id > 0) { messageText("item_do_carrinho_invalido_eb5fe0") }
        repository.remove(item)
    }
}
