package br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetActiveForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ObserveForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.RefreshForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.state.ForceUpdateAction
import br.gov.caixa.loterias.feature.onboarding.presentation.state.ForceUpdateState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.reduceForceUpdateState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ForceUpdateViewModel(
    private val active: GetActiveForceUpdateUseCase,
    private val refreshUpdate: RefreshForceUpdateUseCase,
    observe: ObserveForceUpdateUseCase,
) : ViewModel() {
    private val mutableState = reduxStateStore<ForceUpdateState, ForceUpdateAction>(
        if (active()) ForceUpdateState.REQUIRED else ForceUpdateState.CHECKING,
        ::reduceForceUpdateState
    ).also { addCloseable(it) }
    val state = mutableState.state
    private var request: Job? = null
    private var foregroundRecovery: Job? = null

    init {
        runCatching { observe { viewModelScope.launch { publish(active()) } } }.getOrNull()?.let { addCloseable(it) }
        refresh()
    }

    fun refresh() {
        if (request?.isActive == true) return
        request = viewModelScope.launch {
            runCatching { refreshUpdate() }.fold(
                onSuccess = { publish(active()) },
                onFailure = { failure ->
                    if (failure !is Exception || failure is CancellationException) throw failure
                    // Uma falha de busca nunca deve limpar uma atualização obrigatória já ativada.
                    publish(active())
                }
            )
        }
    }

    fun startForegroundUpdates() {
        if (foregroundRecovery?.isActive == true) return
        refresh()
        foregroundRecovery = viewModelScope.launch {
            while (isActive) {
                delay(FOREGROUND_RECOVERY_INTERVAL_MILLIS)
                refresh()
            }
        }
    }

    fun stopForegroundUpdates() {
        foregroundRecovery?.cancel()
        foregroundRecovery = null
    }

    private fun publish(required: Boolean) {
        mutableState.dispatch(ForceUpdateAction.ActiveValuePublished(required))
    }
}

private const val FOREGROUND_RECOVERY_INTERVAL_MILLIS = 30_000L
