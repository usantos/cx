package br.gov.caixa.loterias.core.domain.configuration.usecase

import kotlinx.coroutines.flow.Flow
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaignSettings

interface CampaignConfigurationUseCases {
    val settings: Flow<CommemorativeCampaignSettings>
    suspend fun load() = refresh()
    suspend fun refresh()
}
