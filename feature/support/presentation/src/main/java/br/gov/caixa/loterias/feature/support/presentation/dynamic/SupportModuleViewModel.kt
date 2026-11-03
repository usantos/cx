package br.gov.caixa.loterias.feature.support.presentation.dynamic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleForegroundRefresh
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleViewModel
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportModuleDelivery
import br.gov.caixa.loterias.feature.support.domain.delivery.supportFaqInstallation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Fachada compatível do piloto; admissão, gerações e atualização pertencem ao mecanismo compartilhado. */
internal class SupportModuleViewModel(configuration: AppModulesConfigurationUseCase, delivery: SupportModuleDelivery) : ViewModel() {
    private val owners = ViewModelStore()
    private val refresh = FeatureModuleForegroundRefresh(viewModelScope, configuration)
    private val delegate = ViewModelProvider.create(
        owners,
        viewModelFactory {
            initializer {
                FeatureModuleViewModel(
                    configuration,
                    supportFaqInstallation(delivery),
                    AppModule.SUPPORT_FAQ,
                    FeatureEntryAccess.OPTIONAL,
                    false
                )
            }
        }
    )[FeatureModuleViewModel::class]
    val state = delegate.state.map { it.toSupportDeliveryState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, delegate.state.value.toSupportDeliveryState())

    init {
        addCloseable { owners.clear() }
        addCloseable(refresh)
    }

    fun retry() = delegate.retry()

    fun confirmationHandler(sessionId: Int): (Boolean) -> Unit = delegate.confirmationHandler(sessionId)

    fun confirmationDeclined() = delegate.confirmationDeclined()

    fun entryUnavailable() = delegate.entryUnavailable()

    fun foreground(active: Boolean) = refresh.foreground(active)
}
