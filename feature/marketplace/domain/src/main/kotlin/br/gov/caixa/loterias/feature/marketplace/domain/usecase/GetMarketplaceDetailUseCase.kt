package br.gov.caixa.loterias.feature.marketplace.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail

fun interface GetMarketplaceDetailUseCase {
    suspend operator fun invoke(poolCode: String): AppResult<MarketplacePoolDetail>
}
