package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet

fun interface GetFavoriteBetsUseCase {
    suspend operator fun invoke(modalityId: Int?): AppResult<List<FavoriteBet>>
    suspend fun page(modalityId: Int, offset: Int): AppResult<List<FavoriteBet>> =
        if (offset == 0) invoke(modalityId) else AppResult.Success(emptyList())
}
