package br.gov.caixa.loterias.feature.support.data.delivery

import android.content.Context
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.domain.delivery.ModuleInstallationRepository
import br.gov.caixa.loterias.core.moduledelivery.data.PlayModuleInstallationRepositoryImpl
import br.gov.caixa.loterias.core.moduledelivery.di.createModuleInstallationRepository
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportDeliveryState
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportModuleDelivery
import com.google.android.play.core.splitinstall.SplitInstallManager
import kotlinx.coroutines.flow.map

/** Adaptador do contrato original do FAQ para a instalação compartilhada dos módulos. */
class PlaySupportModuleDelivery(private val installation: ModuleInstallationRepository) : SupportModuleDelivery {
    internal constructor(manager: SplitInstallManager) : this(PlayModuleInstallationRepositoryImpl(manager))
    constructor(context: Context) : this(createModuleInstallationRepository(context))

    override fun install() = installation.install(AppModule.SUPPORT_FAQ).map { state ->
        when (state) {
            ModuleDeliveryState.Disabled -> SupportDeliveryState.Disabled
            ModuleDeliveryState.Checking -> SupportDeliveryState.Checking
            ModuleDeliveryState.Pending -> SupportDeliveryState.Pending
            ModuleDeliveryState.Ready -> SupportDeliveryState.Ready
            ModuleDeliveryState.Failed -> SupportDeliveryState.Failed
            is ModuleDeliveryState.Downloading -> SupportDeliveryState.Downloading(state.bytes, state.total)
            is ModuleDeliveryState.Confirmation -> SupportDeliveryState.Confirmation(state.sessionId)
        }
    }
}
