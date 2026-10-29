package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartRepository

class DeleteFavoriteCartBet(private val repository: FavoriteCartRepository) : DeleteFavoriteCartBetUseCase {
    override suspend fun invoke(cartId: Long, betId: Long) = appResultOf {
        require(cartId > 0 && betId > 0) { messageText("aposta_favorita_invalida_5735e8") }
        repository.deleteBet(cartId, betId)
    }
}
