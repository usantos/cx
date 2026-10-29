package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface DeleteFavoriteBetUseCase {
    suspend operator fun invoke(favoriteBetId: Long): AppResult<Unit>
}
