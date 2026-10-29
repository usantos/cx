package br.gov.caixa.loterias.core.data.repository

import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.core.network.model.AppParametersDto
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaignSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import br.gov.caixa.loterias.core.data.mapper.toCommemorativeCampaigns
import br.gov.caixa.loterias.core.data.datasource.remote.AppConfigurationRemoteDataSource

class AppConfigurationRepositoryImpl(
    private val remote: AppConfigurationRemoteDataSource,
    private val requestScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) : AppConfigurationRepository {
    private val mutableCampaignSettings = MutableStateFlow(CommemorativeCampaignSettings())
    override val campaignSettings = mutableCampaignSettings.asStateFlow()

    @Volatile private var cachedAppParameters: AppParametersDto? = null
    private val requestLock = Any()
    private var inFlight: Deferred<AppParametersDto?>? = null

    override suspend fun isVersionSupported(versionName: String): Boolean = remote.isVersionSupported(versionName) == true

    override suspend fun marketplaceFilterParameters(): MarketplaceFilterParameters {
        val parameters = appParameters()
        return MarketplaceFilterParameters(
            parameters?.minimumMarketplaceBetQuantity,
            parameters?.maximumMarketplaceBetQuantity
        )
    }

    override suspend fun homeFeatures(): HomeFeatureConfiguration = homeFeatures(appParameters())

    override suspend fun refreshHomeFeatures(): HomeFeatureConfiguration = homeFeatures(appParameters(refresh = true))

    private fun homeFeatures(parameters: AppParametersDto?): HomeFeatureConfiguration = HomeFeatureConfiguration(
        poolSalesEnabled = parameters?.poolSalesEnabled == true,
        comboBettingEnabled = parameters?.comboBettingEnabled == true,
        rapidBettingEnabled = parameters?.rapidBettingEnabled == true,
        myBetsVisible = parameters?.myBetsVisible ?: true,
        specialContests = br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests(
            megaThirtyYearsContest = parameters?.megaThirtyYearsContest,
            lotecaFathersDayContest = parameters?.lotecaFathersDayContest
        ),
        campaigns = parameters.toCommemorativeCampaigns()
    )

    /** Todos os consumidores compartilham a mesma requisição; cancelar uma espera não cancela os demais leitores. */
    private suspend fun appParameters(refresh: Boolean = false): AppParametersDto? {
        val request = synchronized(requestLock) {
            inFlight?.takeUnless { it.isCompleted } ?: run {
                if (!refresh) cachedAppParameters?.let { return it }
                requestScope.async(start = CoroutineStart.LAZY) {
                    try {
                        val parameters = remote.appParameters()?.values
                        synchronized(requestLock) {
                            if (parameters != null) {
                                cachedAppParameters = parameters
                                mutableCampaignSettings.value = parameters.toCommemorativeCampaigns()
                            }
                            cachedAppParameters
                        }
                    } catch (failure: Exception) {
                        if (failure is CancellationException) throw failure
                        cachedAppParameters ?: throw failure
                    }
                }.also { task ->
                    inFlight = task
                    task.invokeOnCompletion {
                        synchronized(requestLock) { if (inFlight === task) inFlight = null }
                    }
                }
            }
        }
        return request.await()
    }
}
