package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.domain.repository.FavoriteBetRepository

class DeleteFavoriteBet(private val repository: FavoriteBetRepository) : DeleteFavoriteBetUseCase {
    override suspend fun invoke(favoriteBetId: Long) = appResultOf {
        require(favoriteBetId > 0) { messageText("aposta_favorita_invalida_5735e8") }
        repository.delete(favoriteBetId)
    }
}
