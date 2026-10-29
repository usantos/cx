package br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ShouldShowIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.state.StartupRecoveryNavigationAction
import br.gov.caixa.loterias.feature.onboarding.presentation.state.StartupRecoveryNavigationState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.StartupRecoveryTarget
import br.gov.caixa.loterias.feature.onboarding.presentation.state.reduceStartupRecoveryNavigationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartupRecoveryNavigationViewModel(
    private val shouldShowIntroduction: ShouldShowIntroductionUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = reduxStateStore<StartupRecoveryNavigationState, StartupRecoveryNavigationAction>(
        StartupRecoveryNavigationState(),
        ::reduceStartupRecoveryNavigationState
    ).also { addCloseable(it) }
    val state = mutableState.state
    private var request: Job? = null
    private var generation = 0L
    private var active = true

    fun activate() {
        active = true
    }

    fun deactivate() {
        active = false
        generation++
        request?.cancel()
        mutableState.dispatch(StartupRecoveryNavigationAction.Deactivated(generation))
    }

    fun recover() {
        if (!active || request?.isActive == true || state.value.target != null) return
        val id = ++generation
        mutableState.dispatch(StartupRecoveryNavigationAction.Started(id))
        request = viewModelScope.launch {
            val result = withContext(dispatchers.io) { appResultOf { shouldShowIntroduction() } }
            if (!active || id != generation) return@launch
            mutableState.dispatch(
                when (result) {
                    is AppResult.Success -> StartupRecoveryNavigationAction.Recovered(
                        id,
                        if (result.value) StartupRecoveryTarget.INTRODUCTION else StartupRecoveryTarget.LOGIN
                    )

                    is AppResult.Failure -> StartupRecoveryNavigationAction.Failed(id)
                }
            )
        }
    }

    fun retry() = recover()
    fun acknowledgeTarget() = mutableState.dispatch(StartupRecoveryNavigationAction.TargetAcknowledged)

    override fun onCleared() {
        deactivate()
    }
}
