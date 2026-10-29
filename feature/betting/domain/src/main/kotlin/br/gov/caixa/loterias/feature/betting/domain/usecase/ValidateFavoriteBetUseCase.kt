package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft

fun interface ValidateFavoriteBetUseCase {
    suspend operator fun invoke(draft: FavoriteBetDraft): AppResult<Unit>
}
