package br.gov.caixa.loterias.core.presentation.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.usecase.AppModulesConfigurationUseCase
import br.gov.caixa.loterias.core.domain.delivery.InstallAppModuleUseCase
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FeatureModuleViewModel(
    private val configuration: AppModulesConfigurationUseCase,
    private val installation: InstallAppModuleUseCase,
    private val module: AppModule,
    private val access: FeatureEntryAccess,
    private val preserveActive: Boolean,
) : ViewModel() {
    private val store = reduxStateStore<ModuleDeliveryState, Changed>(ModuleDeliveryState.Checking) { _, action -> action.value }
        .also { addCloseable(it) }
    val state = store.state
    private var installationJob: Job? = null
    private var generation = 0L
    private var admitted = false

    init {
        viewModelScope.launch {
            configuration.configuration.map { it.isEnabled(module) }.distinctUntilChanged().collect { enabled ->
                if (enabled || access != FeatureEntryAccess.OPTIONAL) {
                    admitted = true
                    if (state.value != ModuleDeliveryState.Ready) install()
                } else if (!preserveActive || !admitted) {
                    generation++
                    installationJob?.cancel()
                    publish(ModuleDeliveryState.Disabled)
                }
            }
        }
    }

    fun retry() {
        if (state.value == ModuleDeliveryState.Failed &&
            (configuration.isEnabled(module) || (admitted && preserveActive) || access != FeatureEntryAccess.OPTIONAL)
        ) {
            install()
        }
    }

    fun entryUnavailable() {
        if (state.value == ModuleDeliveryState.Ready) publish(ModuleDeliveryState.Failed)
    }

    fun confirmationHandler(sessionId: Int): (Boolean) -> Unit {
        val request = generation
        return { accepted ->
            if (!accepted && request == generation && state.value == ModuleDeliveryState.Confirmation(sessionId)) {
                confirmationDeclined()
            }
        }
    }

    fun confirmationDeclined() {
        if (state.value !is ModuleDeliveryState.Confirmation) return
        generation++
        installationJob?.cancel()
        publish(ModuleDeliveryState.Failed)
    }

    private fun install() {
        installationJob?.cancel()
        val request = ++generation
        publish(ModuleDeliveryState.Checking)
        installationJob = viewModelScope.launch {
            runCatching {
                installation(module).collect { value -> if (request == generation) publish(value) }
            }.onFailure {
                if (it is CancellationException) throw it
                if (request == generation) publish(ModuleDeliveryState.Failed)
            }
        }
    }

    private fun publish(value: ModuleDeliveryState) = store.dispatch(Changed(value))
    private data class Changed(val value: ModuleDeliveryState)
}
