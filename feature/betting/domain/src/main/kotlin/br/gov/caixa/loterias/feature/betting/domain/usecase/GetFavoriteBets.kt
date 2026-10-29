package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.domain.repository.FavoriteBetRepository

class GetFavoriteBets(private val repository: FavoriteBetRepository) : GetFavoriteBetsUseCase {
    override suspend fun invoke(modalityId: Int?) = appResultOf {
        require(modalityId == null || modalityId > 0) { messageText("modalidade_invalida_4a3e50") }
        repository.favorites(modalityId)
    }
    override suspend fun page(modalityId: Int, offset: Int) = appResultOf {
        require(modalityId > 0 && offset >= 0) { messageText("filtro_de_apostas_favoritas_inva_a43ae4") }
        repository.favorites(modalityId, offset)
    }
}
