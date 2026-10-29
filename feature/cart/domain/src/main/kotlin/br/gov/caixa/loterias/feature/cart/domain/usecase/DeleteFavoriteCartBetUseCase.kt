package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet

fun interface DeleteFavoriteCartBetUseCase {
    suspend operator fun invoke(cartId: Long, betId: Long): AppResult<List<FavoriteCartBet>>
}
