package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository

class ValidateCart(private val repository: CartRepository) : ValidateCartUseCase {
    override suspend fun invoke() = appResultOf { repository.validate() }
}
