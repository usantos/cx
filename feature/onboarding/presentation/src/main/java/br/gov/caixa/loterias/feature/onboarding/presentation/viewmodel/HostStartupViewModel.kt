package br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.onboarding.presentation.state.HostStartupAction
import br.gov.caixa.loterias.feature.onboarding.presentation.state.HostStartupState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.reduceHostStartupState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HostStartupViewModel(private val prepare: PrepareNetworkSecurityUseCase) : ViewModel() {
    private val mutableState = reduxStateStore<HostStartupState, HostStartupAction>(HostStartupState(), ::reduceHostStartupState).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private var splash: Job? = null
    private var security: Job? = null
    private var generation = 0L
    fun start() {
        if (splash != null) return
        mutableState.dispatch(HostStartupAction.Reset)
        splash = viewModelScope.launch {
            delay(2_000)
            mutableState.dispatch(HostStartupAction.SplashElapsed)
        }
        prepareSecurity()
    }
    fun retry() {
        if (security?.isActive == true) return
        mutableState.dispatch(HostStartupAction.Retried)
        prepareSecurity()
    }
    private fun prepareSecurity() {
        val current = ++generation
        security = viewModelScope.launch {
            val ready = prepare() is AppResult.Success
            if (current == generation) mutableState.dispatch(HostStartupAction.SecurityPrepared(ready))
        }
    }
    fun stop() {
        generation++
        security?.cancel()
        splash?.cancel()
        security = null
        splash = null
        mutableState.dispatch(HostStartupAction.Reset)
    }
}
