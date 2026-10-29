package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface ValidateFavoriteCartNameUseCase {
    suspend operator fun invoke(name: String): AppResult<Unit>
}
