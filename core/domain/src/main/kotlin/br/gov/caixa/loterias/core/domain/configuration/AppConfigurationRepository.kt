package br.gov.caixa.loterias.core.domain.configuration

import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilterParameters

data class HomeFeatureConfiguration(
    val poolSalesEnabled: Boolean = false,
    val comboBettingEnabled: Boolean = false,
    val rapidBettingEnabled: Boolean = false,
    val myBetsVisible: Boolean = true,
)

interface AppConfigurationRepository {
    suspend fun isVersionSupported(versionName: String): Boolean
    suspend fun marketplaceFilterParameters(): MarketplaceFilterParameters
    suspend fun homeFeatures(): HomeFeatureConfiguration = HomeFeatureConfiguration()
}
