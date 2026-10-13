package br.gov.caixa.loterias.apostas

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import br.gov.caixa.loterias.core.auth.ForegroundSessionAccess
import br.gov.caixa.loterias.core.auth.ForegroundSessionCheck
import br.gov.caixa.loterias.core.auth.ForegroundSessionStatus

/** Initial entry is untouched; only a logged context that actually stopped is checked on return. */
internal class ForegroundSessionLifecycleObserver(
    private val tokens: ForegroundSessionAccess,
    private val currentRoute: () -> String?,
    private val hasLoggedContext: () -> Boolean,
    private val nowSeconds: () -> Long,
    private val onLogin: () -> Unit,
    private val onExpired: (ForegroundSessionCheck) -> Unit,
) : DefaultLifecycleObserver {
    private var loggedBackground = false

    override fun onStop(owner: LifecycleOwner) {
        loggedBackground = hasLoggedContext() && eligibleRoute(currentRoute())
    }

    override fun onResume(owner: LifecycleOwner) {
        val returning = loggedBackground
        loggedBackground = false
        if (!returning || !eligibleRoute(currentRoute())) return
        val check = tokens.foregroundCheck(nowSeconds())
        when (check.status) {
            ForegroundSessionStatus.MISSING -> if (tokens.clearForegroundSession(check)) onLogin()
            ForegroundSessionStatus.EXPIRED -> onExpired(check)
            ForegroundSessionStatus.ACTIVE, ForegroundSessionStatus.AUTHORIZATION_PENDING -> Unit
        }
    }

    private fun eligibleRoute(route: String?): Boolean {
        val base = route?.substringBefore('?') ?: return false
        return base != "account/login" && base != "account/token" && !base.startsWith("onboarding/")
    }
}
