package br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.IsVersionSupportedUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.onboarding.presentation.state.StartupAction
import br.gov.caixa.loterias.feature.onboarding.presentation.state.StartupState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.reduceStartupState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class StartupViewModel(
    private val isVersionSupported: IsVersionSupportedUseCase,
    private val getFilterParameters: GetMarketplaceFilterParametersUseCase,
    private val versionName: String,
    private val prepareNetworkSecurity: PrepareNetworkSecurityUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = reduxStateStore<StartupState, StartupAction>(StartupState.WAITING_QUEUE, ::reduceStartupState).also {
        addCloseable(it)
    }
    val state = mutableState.state

    fun queuePassed() = validate()
    fun retry() = validate()

    private fun validate() {
        if (mutableState.value == StartupState.CHECKING) return
        mutableState.dispatch(StartupAction.ValidationStarted)
        viewModelScope.launch(dispatchers.io) {
            try {
                if (prepareNetworkSecurity() is AppResult.Failure) {
                    mutableState.dispatch(StartupAction.Unavailable)
                    return@launch
                }
                mutableState.dispatch(
                    when (val version = isVersionSupported(versionName)) {
                        is AppResult.Failure -> StartupAction.Unavailable

                        is AppResult.Success -> when {
                            !version.value -> StartupAction.UpdateRequired
                            getFilterParameters() is AppResult.Success -> StartupAction.Ready
                            else -> StartupAction.Unavailable
                        }
                    }
                )
            } catch (cancelled: CancellationException) {
                mutableState.dispatch(StartupAction.Unavailable)
                throw cancelled
            }
        }
    }
}
