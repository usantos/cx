package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.configuration.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilterParameters
import br.gov.caixa.loterias.core.network.AppParametersDto

class RemoteAppConfigurationRepository(
    private val remote: AppConfigurationRemoteDataSource,
) : AppConfigurationRepository {
    @Volatile private var cachedAppParameters: AppParametersDto? = null

    override suspend fun isVersionSupported(versionName: String): Boolean =
        remote.isVersionSupported(versionName) == true

    override suspend fun marketplaceFilterParameters(): MarketplaceFilterParameters {
        val parameters = appParameters()
        return MarketplaceFilterParameters(
            parameters?.minimumMarketplaceBetQuantity,
            parameters?.maximumMarketplaceBetQuantity,
        )
    }

    override suspend fun homeFeatures(): HomeFeatureConfiguration {
        val parameters = appParameters()
        return HomeFeatureConfiguration(
            poolSalesEnabled = parameters?.poolSalesEnabled == true,
            comboBettingEnabled = parameters?.comboBettingEnabled == true,
            rapidBettingEnabled = parameters?.rapidBettingEnabled == true,
            myBetsVisible = parameters?.myBetsVisible ?: true,
        )
    }

    private suspend fun appParameters(): AppParametersDto? {
        cachedAppParameters?.let { return it }
        return remote.appParameters()?.values?.also { cachedAppParameters = it }
    }
}
