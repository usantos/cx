package br.gov.caixa.loterias.apostas

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.gov.caixa.loterias.core.auth.ForegroundSessionAccess
import br.gov.caixa.loterias.core.auth.ForegroundSessionCheck
import br.gov.caixa.loterias.core.auth.ForegroundSessionStatus

/** Retained by AppContainer for this process; carries no Activity, route, or credentials. */
internal class ForegroundSessionBackgroundState {
    internal var pending = false
}

/** Mirrors CX's process background flag, consumed by the first eligible Base host resumed. */
internal class ForegroundSessionLifecycleObserver(
    private val tokens: ForegroundSessionAccess,
    private val currentRoute: () -> String?,
    private val nowSeconds: () -> Long,
    private val onLogin: () -> Unit,
    private val onExpired: (ForegroundSessionCheck) -> Unit,
    private val historicalBetDetail: () -> Boolean = { false },
    private val backgroundState: ForegroundSessionBackgroundState = ForegroundSessionBackgroundState(),
    private val prizeChannelHost: () -> Boolean = { false },
) : DefaultLifecycleObserver {
    private var foreground = false

    /** Navigation inside one Compose Activity must also model a resumed legacy Activity. */
    fun onRouteEntered() = checkPendingBackground()

    override fun onStop(owner: LifecycleOwner) {
        foreground = false
        // Non-Base screens leave CX's global flag untouched for a later Base screen.
        // CX checks missing access on visitor Base hosts too; only provider/non-Base
        // routes defer consumption. Do not filter this process event by login status.
        backgroundState.pending = true
    }

    override fun onResume(owner: LifecycleOwner) {
        foreground = true
        checkPendingBackground()
    }

    private fun checkPendingBackground() {
        if (!foreground || !backgroundState.pending || !eligibleRoute(currentRoute())) return
        backgroundState.pending = false
        val check = tokens.foregroundCheck(nowSeconds())
        when (check.status) {
            ForegroundSessionStatus.MISSING -> if (tokens.clearForegroundSession(check)) onLogin()
            ForegroundSessionStatus.EXPIRED -> onExpired(check)
            ForegroundSessionStatus.ACTIVE, ForegroundSessionStatus.AUTHORIZATION_PENDING -> Unit
        }
    }

    private fun eligibleRoute(route: String?): Boolean {
        return ForegroundSessionRoutePolicy.eligible(route, historicalBetDetail(), prizeChannelHost())
    }
}
