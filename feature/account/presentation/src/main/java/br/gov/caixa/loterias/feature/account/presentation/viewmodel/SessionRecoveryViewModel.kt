package br.gov.caixa.loterias.feature.account.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.model.LocalSessionRecovery
import br.gov.caixa.loterias.feature.account.domain.port.SessionAuthentication
import br.gov.caixa.loterias.feature.account.domain.usecase.ResumeLocalSessionCleanupUseCase
import br.gov.caixa.loterias.feature.account.presentation.state.SessionRecoveryAction
import br.gov.caixa.loterias.feature.account.presentation.state.SessionRecoveryState
import br.gov.caixa.loterias.feature.account.presentation.state.reduceSessionRecoveryState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Proteção de entrada para limpeza local interrompida, sem login automático ou repetição de SSO. */
class SessionRecoveryViewModel(
    private val resumeCleanup: ResumeLocalSessionCleanupUseCase,
    private val authentication: SessionAuthentication,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = reduxStateStore<SessionRecoveryState, SessionRecoveryAction>(
        SessionRecoveryState.CHECKING,
        ::reduceSessionRecoveryState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private var recovered = false

    init {
        recover()
    }

    fun retry() {
        if (state.value == SessionRecoveryState.FAILED) recover()
    }

    fun acknowledge() {
        if (state.value == SessionRecoveryState.AWAITING_ACKNOWLEDGMENT) mutableState.dispatch(SessionRecoveryAction.Acknowledged)
    }

    private fun recover() {
        mutableState.dispatch(SessionRecoveryAction.RecoveryStarted)
        viewModelScope.launch {
            try {
                if (withContext(dispatchers.io) { resumeCleanup() } == LocalSessionRecovery.RECOVERED) recovered = true
                // Uma segunda Activity no mesmo processo não deve preservar uma sessão antiga em memória.
                if (recovered) authentication.logout()
                mutableState.dispatch(SessionRecoveryAction.Recovered(recovered))
            } catch (cancelled: CancellationException) {
                mutableState.dispatch(SessionRecoveryAction.Failed)
                throw cancelled
            } catch (_: Exception) {
                mutableState.dispatch(SessionRecoveryAction.Failed)
            }
        }
    }
}
