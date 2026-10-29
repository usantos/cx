package br.gov.caixa.loterias.core.domain.configuration.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository

/** Conclui a preparação de segurança antes de permitir o início de uma operação de negócio dependente. */
fun interface PrepareNetworkSecurityUseCase {
    suspend operator fun invoke(): AppResult<Unit>
}

fun interface IsVersionSupportedUseCase {
    suspend operator fun invoke(versionName: String): AppResult<Boolean>
}

class IsVersionSupported(
    private val repository: AppConfigurationRepository,
) : IsVersionSupportedUseCase {
    override suspend fun invoke(versionName: String) = appResultOf {
        repository.isVersionSupported(versionName)
    }
}

fun interface GetMarketplaceFilterParametersUseCase {
    suspend operator fun invoke(): AppResult<MarketplaceFilterParameters>
}

class GetMarketplaceFilterParameters(
    private val repository: AppConfigurationRepository,
) : GetMarketplaceFilterParametersUseCase {
    override suspend fun invoke() = appResultOf {
        repository.marketplaceFilterParameters()
    }
}

fun interface GetHomeFeatureConfigurationUseCase {
    suspend operator fun invoke(): AppResult<HomeFeatureConfiguration>
    suspend fun refresh(): AppResult<HomeFeatureConfiguration> = invoke()
}

class GetHomeFeatureConfiguration(private val repository: AppConfigurationRepository) : GetHomeFeatureConfigurationUseCase {
    override suspend fun invoke() = appResultOf { repository.homeFeatures() }
    override suspend fun refresh() = appResultOf { repository.refreshHomeFeatures() }
}
