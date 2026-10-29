package br.gov.caixa.loterias.feature.marketplace.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceDetailRepository

class GetMarketplaceDetail(
    private val repository: MarketplaceDetailRepository,
) : GetMarketplaceDetailUseCase {
    override suspend fun invoke(poolCode: String) = appResultOf {
        repository.detail(poolCode)
    }
}
