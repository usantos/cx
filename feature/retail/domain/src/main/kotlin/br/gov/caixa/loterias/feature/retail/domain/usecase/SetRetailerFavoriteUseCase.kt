package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface SetRetailerFavoriteUseCase { suspend operator fun invoke(retailerId: Long, favorite: Boolean): AppResult<Unit> }
