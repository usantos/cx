package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions
import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.domain.repository.FavoriteBetRepository

class AddFavoriteBetToCart(private val repository: FavoriteBetRepository) : AddFavoriteBetToCartUseCase {
    override suspend fun invoke(favoriteBetId: Long, options: FavoriteBetCartOptions) = appResultOf {
        require(favoriteBetId > 0) { messageText("aposta_favorita_invalida_5735e8") }
        require(options.contestType in 1..2) { messageText("tipo_de_concurso_invalido_01ede7") }
        require(options.recurringDraws >= 0) { messageText("quantidade_de_teimosinha_invalid_9d386a") }
        repository.addToCart(favoriteBetId, options)
    }
}
