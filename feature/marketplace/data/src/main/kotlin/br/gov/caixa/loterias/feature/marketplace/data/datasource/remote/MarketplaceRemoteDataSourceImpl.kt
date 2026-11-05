package br.gov.caixa.loterias.feature.marketplace.data.datasource.remote

import br.gov.caixa.loterias.core.network.dto.betting.AddPoolShareRequestDto
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider

class MarketplaceRemoteDataSourceImpl(
    private val apiProvider: LoteriasApiProvider,
) : MarketplaceRemoteDataSource {
    override suspend fun pools(query: Map<String, String>) = apiProvider.current().featureMarketplaceApi().marketplacePools(query).payload

    override suspend fun poolDetail(poolCode: String) = apiProvider.current().featureMarketplaceApi().marketplacePoolDetail(poolCode).payload

    override suspend fun addPoolShare(request: AddPoolShareRequestDto) =
        apiProvider.current().featureMarketplaceApi().addPoolShareToCart(request).payload
}
