package br.gov.caixa.loterias.apostas.initialization

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.IsVersionSupportedUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import javax.inject.Inject

class AppInitializationDependencies @Inject constructor(
    val networkAvailability: NetworkAvailability,
    val networkSecurity: PrepareNetworkSecurityUseCase,
    val isVersionSupported: IsVersionSupportedUseCase,
    val marketplaceFilterParameters: GetMarketplaceFilterParametersUseCase,
    val dispatchers: CoroutineDispatchers,
)
