package br.gov.caixa.loterias.core.domain.configuration.repository

import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaignSettings
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.contract.marketplace.model.MarketplaceFilterParameters

fun interface VersionSupportRepository {
    suspend fun isVersionSupported(versionName: String): Boolean
}

interface AppConfigurationRepository : VersionSupportRepository {
    val campaignSettings: kotlinx.coroutines.flow.Flow<CommemorativeCampaignSettings>
        get() = kotlinx.coroutines.flow.flowOf(CommemorativeCampaignSettings())
    suspend fun marketplaceFilterParameters(): MarketplaceFilterParameters
    suspend fun refreshHomeFeatures(): HomeFeatureConfiguration = homeFeatures()
    suspend fun homeFeatures(): HomeFeatureConfiguration = HomeFeatureConfiguration()
}
