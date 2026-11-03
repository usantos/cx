package br.gov.caixa.loterias.feature.support.domain.delivery

import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.delivery.InstallAppModuleUseCase
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import kotlinx.coroutines.flow.map

/** Adapta a porta legada do FAQ ao caso de uso de entrega compartilhado. */
fun supportFaqInstallation(delivery: SupportModuleDelivery): InstallAppModuleUseCase = InstallAppModuleUseCase(
    object : ModuleInstallationRepository {
        override fun install(module: AppModule) = delivery.install().map { it.toModuleDeliveryState() }
    }
)

private fun SupportDeliveryState.toModuleDeliveryState(): ModuleDeliveryState = when (this) {
    SupportDeliveryState.Disabled -> ModuleDeliveryState.Disabled
    SupportDeliveryState.Checking -> ModuleDeliveryState.Checking
    SupportDeliveryState.Pending -> ModuleDeliveryState.Pending
    SupportDeliveryState.Ready -> ModuleDeliveryState.Ready
    SupportDeliveryState.Failed -> ModuleDeliveryState.Failed
    is SupportDeliveryState.Downloading -> ModuleDeliveryState.Downloading(bytes, total)
    is SupportDeliveryState.Confirmation -> ModuleDeliveryState.Confirmation(sessionId)
}
