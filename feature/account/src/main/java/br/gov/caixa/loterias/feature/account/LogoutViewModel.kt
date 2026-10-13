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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class LogoutNotice { LOCAL_FAILURE, LOCAL_DATA_FAILURE, CANCELLED, UNCONFIRMED, SIMULATED }

data class LogoutUiState(
    val launch: EndSessionLaunch? = null,
    val waitingForBrowser: Boolean = false,
    val notice: LogoutNotice? = null,
    val clearingLocalData: Boolean = false,
)

/** One coordinator survives destination replacement; it never authorizes or replays logout. */
class LogoutViewModel(
    private val authentication: AuthenticationGateway,
    private val endSession: EndSessionGateway,
    private val clearLocalSessionData: ClearLocalSessionDataUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(LogoutUiState())
    val state = mutableState.asStateFlow()
    private var activeLaunch: EndSessionLaunch? = null
    private var simulatedLogout = false

    /** True means local credentials were cleared and the authenticated stack must be removed. */
    fun start(simulated: Boolean = false): Boolean {
        if (activeLaunch != null || state.value.notice != null || state.value.clearingLocalData) return false
        try { authentication.logout() } catch (_: Exception) {
            mutableState.value = LogoutUiState(notice = LogoutNotice.LOCAL_FAILURE)
            return false
        }
        simulatedLogout = simulated
        clearLocalDataAndPrepare()
        return true
    }

    fun retryLocalCleanup() {
        if (state.value.notice == LogoutNotice.LOCAL_DATA_FAILURE) clearLocalDataAndPrepare()
    }

    private fun clearLocalDataAndPrepare() {
        mutableState.value = LogoutUiState(clearingLocalData = true)
        viewModelScope.launch {
            try { withContext(dispatchers.io) { clearLocalSessionData() } }
            catch (cancelled: CancellationException) {
                mutableState.value = LogoutUiState(notice = LogoutNotice.LOCAL_DATA_FAILURE)
                throw cancelled
            }
            catch (_: Exception) {
                mutableState.value = LogoutUiState(notice = LogoutNotice.LOCAL_DATA_FAILURE)
                return@launch
            }
            prepareBrowser()
        }
    }

    private fun prepareBrowser() {
        if (simulatedLogout) {
            mutableState.value = LogoutUiState(notice = LogoutNotice.SIMULATED)
            return
        }
        try {
            val launch = endSession.prepare()
            activeLaunch = launch
            mutableState.value = LogoutUiState(launch = launch)
        } catch (_: Exception) {
            mutableState.value = LogoutUiState(notice = LogoutNotice.UNCONFIRMED)
        }
    }

    fun takeLaunch(launch: EndSessionLaunch): Boolean {
        if (activeLaunch !== launch || state.value.launch !== launch) return false
        mutableState.value = LogoutUiState(waitingForBrowser = true)
        return true
    }

    fun launchFailed(launch: EndSessionLaunch) {
        if (activeLaunch !== launch) return
        abandon(launch)
        mutableState.value = LogoutUiState(notice = LogoutNotice.UNCONFIRMED)
    }

    fun complete(result: Intent?) {
        val launch = activeLaunch ?: return
        if (!state.value.waitingForBrowser) return
        val outcome = try { endSession.complete(launch, result) }
        catch (_: Exception) { EndSessionOutcome.UNCONFIRMED }
        abandon(launch)
        mutableState.value = LogoutUiState(notice = when (outcome) {
            EndSessionOutcome.RETURNED -> null
            EndSessionOutcome.CANCELLED -> LogoutNotice.CANCELLED
            EndSessionOutcome.UNCONFIRMED -> LogoutNotice.UNCONFIRMED
        })
    }

    fun dismissNotice() {
        if (state.value.notice != null && state.value.notice != LogoutNotice.LOCAL_DATA_FAILURE) {
            mutableState.value = LogoutUiState()
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
