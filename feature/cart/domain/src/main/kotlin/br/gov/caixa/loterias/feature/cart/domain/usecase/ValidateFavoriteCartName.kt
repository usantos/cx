package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository

class ValidateFavoriteCartName(private val repository: CartRepository) : ValidateFavoriteCartNameUseCase {
    override suspend fun invoke(name: String) = appResultOf {
        require(name.isNotBlank()) { messageText("informe_o_nome_do_carrinho_favor_cbc31f") }
        repository.validateFavoriteName(name.trim())
    }
}
