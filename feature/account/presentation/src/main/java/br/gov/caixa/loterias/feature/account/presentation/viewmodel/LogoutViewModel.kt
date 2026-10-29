package br.gov.caixa.loterias.feature.account.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome
import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.port.EndSessionLifecycle
import br.gov.caixa.loterias.feature.account.domain.port.SessionAuthentication
import br.gov.caixa.loterias.feature.account.domain.usecase.ClearLocalSessionDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSessionUseCase
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutAction
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutNotice
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutUiState
import br.gov.caixa.loterias.feature.account.presentation.state.reduceLogoutState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Um coordenador sobrevive à substituição do destino; nunca autoriza nem repete o logout. */
class LogoutViewModel(
    private val authentication: SessionAuthentication,
    private val endSession: EndSessionLifecycle,
    private val clearLocalSessionData: ClearLocalSessionDataUseCase,
    private val signOutSession: SignOutSessionUseCase,
    private val identities: AuthenticatedSessionIdentityProvider,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = reduxStateStore<LogoutUiState, LogoutAction>(LogoutUiState(), ::reduceLogoutState).also { addCloseable(it) }
    val state = mutableState.state
    private var activeLaunch: EndSessionLaunch? = null
    private var simulatedLogout = false

    private var serverConfirmed: AuthenticatedSessionIdentity? = null
    private var localSessionEnded = false

    /** True significa que a requisição explícita foi aceita; navega somente em localSessionEnded. */
    fun start(simulated: Boolean = false): Boolean {
        if (activeLaunch != null || state.value.notice != null || state.value.clearingLocalData || state.value.signingOut) return false
        simulatedLogout = simulated
        localSessionEnded = false
        signOutAndClear()
        return true
    }

    fun retryServerLogout() {
        if (state.value.notice == LogoutNotice.SERVER_FAILURE) signOutAndClear()
    }

    private fun signOutAndClear() {
        mutableState.dispatch(LogoutAction.SignOutStarted)
        viewModelScope.launch {
            if (!simulatedLogout && serverConfirmed == null) {
                val result = try {
                    withContext(dispatchers.io) { signOutSession() }
                } catch (cancelled: CancellationException) {
                    mutableState.dispatch(LogoutAction.ServerRejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.CONNECTION)))
                    throw cancelled
                } catch (_: Exception) {
                    SessionSignOutResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE))
                }
                if (result is SessionSignOutResult.Rejected) {
                    mutableState.dispatch(LogoutAction.ServerRejected(result.failure))
                    return@launch
                }
                serverConfirmed = (result as SessionSignOutResult.Confirmed).identity
            }
            if (!simulatedLogout && serverConfirmed?.let(identities::isCurrent) != true) {
                serverConfirmed = null
                mutableState.dispatch(
                    LogoutAction.ServerRejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE))
                )
                return@launch
            }
            try {
                authentication.logout()
            } catch (_: Exception) {
                mutableState.dispatch(LogoutAction.LocalLogoutFailed)
                return@launch
            }
            localSessionEnded = true
            serverConfirmed = null
            clearLocalDataAndPrepare()
        }
    }

    fun retryLocalCleanup() {
        if (state.value.notice == LogoutNotice.LOCAL_DATA_FAILURE) clearLocalDataAndPrepare()
    }

    private fun clearLocalDataAndPrepare() {
        mutableState.dispatch(LogoutAction.LocalCleanupStarted)
        viewModelScope.launch {
            try {
                withContext(dispatchers.io) { clearLocalSessionData() }
            } catch (cancelled: CancellationException) {
                mutableState.dispatch(LogoutAction.LocalCleanupFailed)
                throw cancelled
            } catch (_: Exception) {
                mutableState.dispatch(LogoutAction.LocalCleanupFailed)
                return@launch
            }
            prepareBrowser()
        }
    }

    private fun prepareBrowser() {
        if (simulatedLogout) {
            mutableState.dispatch(LogoutAction.SimulatedLogoutCompleted)
            return
        }
        try {
            val launch = endSession.prepare()
            activeLaunch = launch
            mutableState.dispatch(LogoutAction.BrowserPrepared(launch))
        } catch (_: Exception) {
            mutableState.dispatch(LogoutAction.BrowserUnconfirmed)
        }
    }

    fun takeLaunch(launch: EndSessionLaunch): Boolean {
        if (activeLaunch !== launch || state.value.launch !== launch) return false
        mutableState.dispatch(LogoutAction.BrowserLaunchTaken)
        return true
    }

    fun launchFailed(launch: EndSessionLaunch) {
        if (activeLaunch !== launch) return
        abandon(launch)
        mutableState.dispatch(LogoutAction.BrowserUnconfirmed)
    }

    fun complete(resolve: (EndSessionLaunch) -> EndSessionOutcome) {
        val launch = activeLaunch ?: return
        if (!state.value.waitingForBrowser) return
        val outcome = try {
            resolve(launch)
        } catch (_: Exception) {
            EndSessionOutcome.UNCONFIRMED
        }
        abandon(launch)
        mutableState.dispatch(
            LogoutAction.BrowserCompleted(
                when (outcome) {
                    EndSessionOutcome.RETURNED -> null
                    EndSessionOutcome.CANCELLED -> LogoutNotice.CANCELLED
                    EndSessionOutcome.UNCONFIRMED -> LogoutNotice.UNCONFIRMED
                }
            )
        )
    }

    fun dismissNotice() {
        if ((state.value.notice != null || (localSessionEnded && activeLaunch == null && !state.value.clearingLocalData)) &&
            state.value.notice != LogoutNotice.LOCAL_DATA_FAILURE
        ) {
            localSessionEnded = false
            mutableState.dispatch(LogoutAction.NoticeDismissed)
        }
    }

    private fun abandon(launch: EndSessionLaunch) {
        activeLaunch = null
        // Uma falha na limpeza não pode restaurar credenciais nem declarar sucesso remoto.
        try {
            endSession.discard(launch)
        } catch (_: Exception) { /* Permanece desconectado localmente. */ }
    }

    override fun onCleared() {
        activeLaunch?.let(::abandon)
    }
}
