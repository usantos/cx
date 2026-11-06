package br.gov.caixa.loterias.core.auth.foreground

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.gov.caixa.loterias.core.auth.session.ForegroundSessionAccess
import br.gov.caixa.loterias.core.auth.session.ForegroundSessionCheck
import br.gov.caixa.loterias.core.auth.session.ForegroundSessionStatus

/** Reproduz o sinal de segundo plano do processo CX, consumido pelo primeiro hospedeiro Base elegível que for retomado. */
class ForegroundSessionLifecycleObserver(
    private val tokens: ForegroundSessionAccess,
    private val currentRoute: () -> String?,
    private val nowSeconds: () -> Long,
    private val onLogin: () -> Unit,
    private val onExpired: (ForegroundSessionCheck) -> Unit,
    private val historicalBetDetail: () -> Boolean = { false },
    private val backgroundState: ForegroundSessionBackgroundState = ForegroundSessionBackgroundState(),
    private val prizeChannelHost: () -> Boolean = { false },
    private val routeEligible: (String?, Boolean, Boolean) -> Boolean,
) : DefaultLifecycleObserver {
    private var foreground = false

    /** A navegação dentro de uma única Activity Compose também deve representar a retomada de uma Activity legada. */
    fun onRouteEntered() = checkPendingBackground()

    override fun onStop(owner: LifecycleOwner) {
        foreground = false
        val status = tokens.foregroundCheck(nowSeconds()).status
        backgroundState.hadAuthenticatedSessionWhenStopped =
            status == ForegroundSessionStatus.ACTIVE || status == ForegroundSessionStatus.EXPIRED
        // Telas que não são Base adiam o tratamento do ciclo de vida até a retomada de um hospedeiro Base. Visitantes
        // permanecem no fluxo público; ações protegidas controlam sua solicitação de login e continuação.
        backgroundState.pending = true
    }

    override fun onResume(owner: LifecycleOwner) {
        foreground = true
        checkPendingBackground()
    }

    private fun checkPendingBackground() {
        if (!foreground || !backgroundState.pending || !eligibleRoute(currentRoute())) return
        val hadAuthenticatedSession = backgroundState.hadAuthenticatedSessionWhenStopped
        backgroundState.pending = false
        backgroundState.hadAuthenticatedSessionWhenStopped = false
        val check = tokens.foregroundCheck(nowSeconds())
        when (check.status) {
            ForegroundSessionStatus.MISSING -> if (hadAuthenticatedSession && tokens.clearForegroundSession(check)) onLogin()
            ForegroundSessionStatus.EXPIRED -> onExpired(check)
            ForegroundSessionStatus.ACTIVE, ForegroundSessionStatus.AUTHORIZATION_PENDING -> Unit
        }
    }

    private fun eligibleRoute(route: String?): Boolean {
        return routeEligible(route, historicalBetDetail(), prizeChannelHost())
    }
}
