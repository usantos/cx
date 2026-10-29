package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer

fun interface GetFavoriteRetailersUseCase { suspend operator fun invoke(): AppResult<List<Retailer>> }
