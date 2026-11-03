package br.gov.caixa.loterias.core.domain.configuration.repository

import br.gov.caixa.loterias.core.domain.configuration.model.AppModulesConfiguration
import kotlinx.coroutines.flow.StateFlow

interface AppModulesConfigurationRepository {
    val configuration: StateFlow<AppModulesConfiguration>
    suspend fun refresh()
}
