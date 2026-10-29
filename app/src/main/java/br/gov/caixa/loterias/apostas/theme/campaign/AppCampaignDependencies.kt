package br.gov.caixa.loterias.apostas.theme.campaign

import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import javax.inject.Inject

class AppCampaignDependencies @Inject constructor(
    val configuration: AppConfigurationRepository,
    val networkSecurity: PrepareNetworkSecurityUseCase,
    val homeFeatureConfiguration: GetHomeFeatureConfigurationUseCase,
    val dispatchers: CoroutineDispatchers,
)
