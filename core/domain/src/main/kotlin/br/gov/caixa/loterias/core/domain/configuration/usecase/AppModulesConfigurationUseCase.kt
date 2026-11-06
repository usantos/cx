package br.gov.caixa.loterias.core.domain.configuration.usecase

import br.gov.caixa.loterias.core.domain.configuration.repository.AppModulesConfigurationRepository

/** Observa a disponibilidade dos módulos e recupera a configuração ativada ao retomar o aplicativo. */
class AppModulesConfigurationUseCase(private val repository: AppModulesConfigurationRepository) {
    val configuration = repository.configuration

    fun isEnabled(module: br.gov.caixa.loterias.core.domain.configuration.model.AppModule): Boolean = configuration.value.isEnabled(module)

    suspend fun refresh() = repository.refresh()
}
