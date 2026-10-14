package br.gov.caixa.loterias.feature.account

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.auth.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.EndSessionGateway
import br.gov.caixa.loterias.core.auth.EndSessionLaunch
import br.gov.caixa.loterias.core.auth.EndSessionOutcome
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.ClearLocalSessionDataUseCase
import br.gov.caixa.loterias.core.domain.account.SignOutSessionUseCase
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.core.domain.account.SessionSignOutResult
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.ServiceConnectionFailure
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class LogoutNotice { SERVER_FAILURE, LOCAL_FAILURE, LOCAL_DATA_FAILURE, CANCELLED, UNCONFIRMED, SIMULATED }

data class LogoutUiState(
    val launch: EndSessionLaunch? = null,
    val waitingForBrowser: Boolean = false,
    val notice: LogoutNotice? = null,
    val clearingLocalData: Boolean = false,
    val signingOut: Boolean = false,
    val localSessionEnded: Boolean = false,
    val serverFailure: ServiceFailure? = null,
)

/** One coordinator survives destination replacement; it never authorizes or replays logout. */
class LogoutViewModel(
    private val authentication: AuthenticationGateway,
    private val endSession: EndSessionGateway,
    private val clearLocalSessionData: ClearLocalSessionDataUseCase,
    private val signOutSession: SignOutSessionUseCase,
    private val identities: AuthenticatedSessionIdentityProvider,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(LogoutUiState())
    val state = mutableState.asStateFlow()
    private var activeLaunch: EndSessionLaunch? = null
    private var simulatedLogout = false

    private var serverConfirmed: AuthenticatedSessionIdentity? = null
    private var localSessionEnded = false

    private fun publish(state: LogoutUiState) {
        mutableState.value = state.copy(localSessionEnded = localSessionEnded)
    }

    /** True means the explicit request was accepted; navigate only on localSessionEnded. */
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
        publish(LogoutUiState(signingOut = true))
        viewModelScope.launch {
            if (!simulatedLogout && serverConfirmed == null) {
                val result = try { withContext(dispatchers.io) { signOutSession() } }
                catch (cancelled: CancellationException) {
                    publish(serverFailure(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.CONNECTION)))
                    throw cancelled
                } catch (_: Exception) {
                    SessionSignOutResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE))
                }
                if (result is SessionSignOutResult.Rejected) {
                    publish(serverFailure(result.failure))
                    return@launch
                }
                serverConfirmed = (result as SessionSignOutResult.Confirmed).identity
            }
            if (!simulatedLogout && serverConfirmed?.let(identities::isCurrent) != true) {
                serverConfirmed = null
                publish(serverFailure(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE)))
                return@launch
            }
            try { authentication.logout() } catch (_: Exception) {
                publish(LogoutUiState(notice = LogoutNotice.LOCAL_FAILURE))
                return@launch
            }
            localSessionEnded = true
            serverConfirmed = null
            clearLocalDataAndPrepare()
        }
    }

    private fun serverFailure(failure: ServiceFailure) = LogoutUiState(
        notice = LogoutNotice.SERVER_FAILURE, serverFailure = failure,
    )

    fun retryLocalCleanup() {
        if (state.value.notice == LogoutNotice.LOCAL_DATA_FAILURE) clearLocalDataAndPrepare()
    }

    private fun clearLocalDataAndPrepare() {
        publish(LogoutUiState(clearingLocalData = true))
        viewModelScope.launch {
            try { withContext(dispatchers.io) { clearLocalSessionData() } }
            catch (cancelled: CancellationException) {
                publish(LogoutUiState(notice = LogoutNotice.LOCAL_DATA_FAILURE))
                throw cancelled
            }
            catch (_: Exception) {
                publish(LogoutUiState(notice = LogoutNotice.LOCAL_DATA_FAILURE))
                return@launch
            }
            prepareBrowser()
        }
    }

    private fun prepareBrowser() {
        if (simulatedLogout) {
            publish(LogoutUiState(notice = LogoutNotice.SIMULATED))
            return
        }
        try {
            val launch = endSession.prepare()
            activeLaunch = launch
            publish(LogoutUiState(launch = launch))
        } catch (_: Exception) {
            publish(LogoutUiState(notice = LogoutNotice.UNCONFIRMED))
        }
    }

    fun takeLaunch(launch: EndSessionLaunch): Boolean {
        if (activeLaunch !== launch || state.value.launch !== launch) return false
        publish(LogoutUiState(waitingForBrowser = true))
        return true
    }

    fun launchFailed(launch: EndSessionLaunch) {
        if (activeLaunch !== launch) return
        abandon(launch)
        publish(LogoutUiState(notice = LogoutNotice.UNCONFIRMED))
    }

    fun complete(result: Intent?) {
        val launch = activeLaunch ?: return
        if (!state.value.waitingForBrowser) return
        val outcome = try { endSession.complete(launch, result) }
        catch (_: Exception) { EndSessionOutcome.UNCONFIRMED }
        abandon(launch)
        publish(LogoutUiState(notice = when (outcome) {
            EndSessionOutcome.RETURNED -> null
            EndSessionOutcome.CANCELLED -> LogoutNotice.CANCELLED
            EndSessionOutcome.UNCONFIRMED -> LogoutNotice.UNCONFIRMED
        }))
    }

    fun dismissNotice() {
        if ((state.value.notice != null || (localSessionEnded && activeLaunch == null && !state.value.clearingLocalData)) && state.value.notice != LogoutNotice.LOCAL_DATA_FAILURE) {
            localSessionEnded = false
            publish(LogoutUiState())
        }
    }

    private fun abandon(launch: EndSessionLaunch) {
        activeLaunch = null
        // Cleanup failure cannot restore credentials or claim remote success.
        try { endSession.discard(launch) } catch (_: Exception) { /* Remains locally signed out. */ }
    }

    override fun onCleared() {
        activeLaunch?.let(::abandon)
    }
}
