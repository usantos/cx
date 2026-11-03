package br.gov.caixa.loterias.core.domain.delivery

import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import kotlinx.coroutines.flow.Flow

sealed interface ModuleDeliveryState {
    data object Disabled : ModuleDeliveryState
    data object Checking : ModuleDeliveryState
    data object Pending : ModuleDeliveryState
    data object Ready : ModuleDeliveryState
    data object Failed : ModuleDeliveryState
    data class Downloading(val bytes: Long, val total: Long) : ModuleDeliveryState
    data class Confirmation(val sessionId: Int) : ModuleDeliveryState
}

interface ModuleInstallationRepository {
    fun install(module: AppModule): Flow<ModuleDeliveryState>
}

/** Solicita/observa a instalação física sem repetir efeitos da funcionalidade. */
class InstallAppModuleUseCase(private val repository: ModuleInstallationRepository) {
    operator fun invoke(module: AppModule): Flow<ModuleDeliveryState> = repository.install(module)
}
