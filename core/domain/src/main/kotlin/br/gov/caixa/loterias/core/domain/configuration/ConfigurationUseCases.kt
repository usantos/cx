package br.gov.caixa.loterias.core.domain.configuration

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilterParameters

/** Completes security preparation before a dependent business operation is allowed to start. */
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
}

class GetHomeFeatureConfiguration(
    private val repository: AppConfigurationRepository,
) : GetHomeFeatureConfigurationUseCase {
    override suspend fun invoke() = appResultOf { repository.homeFeatures() }
}
