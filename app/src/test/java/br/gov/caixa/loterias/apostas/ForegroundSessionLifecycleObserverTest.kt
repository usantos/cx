package br.gov.caixa.loterias.apostas

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import br.gov.caixa.loterias.core.auth.SessionTokenStore
import java.util.Base64
import org.junit.Assert.*
import org.junit.Test

class ForegroundSessionLifecycleObserverTest {
    private val owner = object : LifecycleOwner {
        override val lifecycle: Lifecycle get() = error("Observer must not reach into its owner")
    }
    private val store = SessionTokenStore()
    private var route: String? = "account/personal-data"
    private var logged = true
    private var logins = 0
    private var accept: (() -> Unit)? = null
    private val observer = ForegroundSessionLifecycleObserver(store, { route }, { logged }, { 100 },
        { logins++ }, { check ->
            accept = { if (store.clearForegroundSession(check)) logins++ }
        })

    @Test fun expiredReturnWaitsForAcknowledgmentThenClearsWithoutRemoteLogout() {
        val expired = jwt(99)
        store.replace(expired)
        observer.onResume(owner)
        assertNull(accept)
        observer.onStop(owner)
        observer.onResume(owner)
        assertNotNull("Expired logged return must show Entendi", accept)
        assertEquals(0, logins)
        assertEquals(expired, store.token())
        accept!!.invoke()
        assertNull(store.token())
        assertEquals(1, logins)
        observer.onResume(owner)
        assertEquals(1, logins)
    }

    @Test fun missingReturnGoesDirectlyToLoginOnlyAfterLoggedBackground() {
        store.replace(jwt(200))
        observer.onResume(owner)
        assertEquals(0, logins)
        observer.onStop(owner)
        store.clear()
        observer.onResume(owner)
        assertEquals(1, logins)
        assertNull(accept)
    }

    @Test fun visitorLoginTokenAndSplashRoutesDoNotInterruptEntryOrProviderReturn() {
        store.replace(jwt(99))
        for (excluded in listOf("account/login?authorize=true", "account/token", "onboarding/splash", null)) {
            route = excluded
            observer.onStop(owner)
            observer.onResume(owner)
            assertNull(accept)
            assertEquals(0, logins)
        }
        route = "home"
        logged = false
        observer.onStop(owner)
        observer.onResume(owner)
        assertNull(accept)
        assertEquals(0, logins)
    }

    @Test fun oldDialogCannotNavigateOrClearAReplacementSession() {
        store.replace(jwt(99))
        observer.onStop(owner)
        observer.onResume(owner)
        assertNotNull(accept)
        val replacement = jwt(200)
        store.replace(replacement)
        accept!!.invoke()
        assertEquals(replacement, store.token())
        assertEquals(0, logins)
    }

    private fun jwt(exp: Long) = "e30.${Base64.getUrlEncoder().withoutPadding().encodeToString("{\"exp\":$exp}".toByteArray())}.c2ln"
}
