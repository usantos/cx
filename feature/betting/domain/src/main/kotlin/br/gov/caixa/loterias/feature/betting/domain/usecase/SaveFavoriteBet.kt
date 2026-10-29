package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.validate
import br.gov.caixa.loterias.feature.betting.domain.repository.FavoriteBetRepository

class SaveFavoriteBet(private val repository: FavoriteBetRepository) : SaveFavoriteBetUseCase {
    override suspend fun invoke(draft: FavoriteBetDraft) = appResultOf {
        draft.validate()
        repository.save(draft)
    }
}
