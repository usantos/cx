package br.gov.caixa.loterias.core.domain.configuration.usecase

import br.gov.caixa.loterias.core.domain.configuration.usecase.CampaignConfigurationUseCases
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase

class CampaignConfigurationUseCasesImpl(private val repository: AppConfigurationRepository,
    private val prepare: PrepareNetworkSecurityUseCase,
    private val refreshHome: GetHomeFeatureConfigurationUseCase,
) : CampaignConfigurationUseCases {
    override val settings get() = repository.campaignSettings
    override suspend fun load() {
        if (prepare() is AppResult.Success) refreshHome()
    }
    override suspend fun refresh() {
        if (prepare() is AppResult.Success) refreshHome.refresh()
    }
}
