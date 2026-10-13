package br.gov.caixa.loterias.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.configuration.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.IsVersionSupportedUseCase
import br.gov.caixa.loterias.core.domain.configuration.PrepareNetworkSecurityUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StartupState { WAITING_QUEUE, CHECKING, READY, UPDATE_REQUIRED, UNAVAILABLE }

class StartupViewModel(
    private val isVersionSupported: IsVersionSupportedUseCase,
    private val getFilterParameters: GetMarketplaceFilterParametersUseCase,
    private val versionName: String,
    private val prepareNetworkSecurity: PrepareNetworkSecurityUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(StartupState.WAITING_QUEUE)
    val state = mutableState.asStateFlow()

    fun queuePassed() = validate()
    fun retry() = validate()

    private fun validate() {
        if (mutableState.value == StartupState.CHECKING) return
        mutableState.value = StartupState.CHECKING
        viewModelScope.launch(dispatchers.io) {
            try {
                if (prepareNetworkSecurity() is AppResult.Failure) {
                    mutableState.value = StartupState.UNAVAILABLE
                    return@launch
                }
                mutableState.value = when (val version = isVersionSupported(versionName)) {
                    is AppResult.Failure -> StartupState.UNAVAILABLE
                    is AppResult.Success -> when {
                        !version.value -> StartupState.UPDATE_REQUIRED
                        getFilterParameters() is AppResult.Success -> StartupState.READY
                        else -> StartupState.UNAVAILABLE
                    }
                }
            } catch (cancelled: CancellationException) {
                mutableState.value = StartupState.UNAVAILABLE
                throw cancelled
            }
        }
    }
}
