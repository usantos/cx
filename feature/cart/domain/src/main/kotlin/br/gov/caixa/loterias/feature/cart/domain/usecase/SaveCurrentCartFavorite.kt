package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository

class SaveCurrentCartFavorite(private val repository: CartRepository) : SaveCurrentCartFavoriteUseCase {
    override suspend fun invoke(name: String) = appResultOf {
        require(name.isNotBlank()) { messageText("informe_o_nome_do_carrinho_favor_cbc31f") }
        repository.saveFavorite(name.trim())
    }
}
