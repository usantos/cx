package br.gov.caixa.loterias.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.auth.AuthenticationGateway
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.LocalSessionRecovery
import br.gov.caixa.loterias.core.domain.account.ResumeLocalSessionCleanupUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SessionRecoveryState { CHECKING, FAILED, AWAITING_ACKNOWLEDGMENT, READY }

/** Entry guard for interrupted local cleanup, not an automatic login or SSO replay. */
class SessionRecoveryViewModel(
    private val resumeCleanup: ResumeLocalSessionCleanupUseCase,
    private val authentication: AuthenticationGateway,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(SessionRecoveryState.CHECKING)
    val state = mutableState.asStateFlow()
    private var recovered = false

    init { recover() }

    fun retry() {
        if (state.value == SessionRecoveryState.FAILED) recover()
    }

    fun acknowledge() {
        if (state.value == SessionRecoveryState.AWAITING_ACKNOWLEDGMENT) mutableState.value = SessionRecoveryState.READY
    }

    private fun recover() {
        mutableState.value = SessionRecoveryState.CHECKING
        viewModelScope.launch {
            try {
                if (withContext(dispatchers.io) { resumeCleanup() } == LocalSessionRecovery.RECOVERED) recovered = true
                // A second Activity in the same process must not retain an old in-memory session.
                if (recovered) authentication.logout()
                mutableState.value = if (recovered) SessionRecoveryState.AWAITING_ACKNOWLEDGMENT else SessionRecoveryState.READY
            } catch (cancelled: CancellationException) {
                mutableState.value = SessionRecoveryState.FAILED
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = SessionRecoveryState.FAILED
            }
        }
    }
}
