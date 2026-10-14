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
    private var logins = 0
    private var accept: (() -> Unit)? = null
    private val observer = ForegroundSessionLifecycleObserver(store, { route }, { 100 },
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

    @Test fun missingReturnGoesDirectlyToLoginOnlyAfterBackground() {
        store.replace(jwt(200))
        observer.onResume(owner)
        assertEquals(0, logins)
        observer.onStop(owner)
        store.clear()
        observer.onResume(owner)
        assertEquals(1, logins)
        assertNull(accept)
    }

    @Test fun providerEntryDefersBackgroundButVisitorBaseStillChecksSession() {
        store.replace(jwt(99))
        for (excluded in listOf("account/login?authorize=true", "account/token", "onboarding/splash", null)) {
            route = excluded
            observer.onStop(owner)
            observer.onResume(owner)
            assertNull(accept)
            assertEquals(0, logins)
        }
        route = "home"
        store.clear()
        val visitorObserver = ForegroundSessionLifecycleObserver(store, { route }, { 100 },
            { logins++ }, { error("Missing visitor session must redirect without an expiry notice") })
        visitorObserver.onStop(owner)
        visitorObserver.onResume(owner)
        assertNull(accept)
        assertEquals("CX Base checks visitors too after background", 1, logins)
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

    @Test fun returningToMyBetsOrCurrentBetDetailLeavesBackgroundPendingUntilCartEntry() {
        store.replace(jwt(99))
        for (excluded in listOf("tickets/my-bets", "tickets/bet-detail?original=false&resgate=true")) {
            route = excluded
            observer.onStop(owner)
            observer.onResume(owner)
            observer.onRouteEntered()
            assertNull("CX non-Base activities must not consume or show expiration", accept)
        }
        route = "cart/current"
        observer.onRouteEntered()
        assertNotNull("First CX Base destination after excluded return must consume pending background", accept)
        accept = null
        observer.onRouteEntered()
        observer.onResume(owner)
        assertNull("A consumed background must not trigger another alert", accept)
    }

    @Test fun internalNavigationWithoutBackgroundDoesNotCheckExpiredToken() {
        store.replace(jwt(99))
        observer.onResume(owner)
        route = "cart/current"
        observer.onRouteEntered()
        assertNull(accept)
        assertEquals(0, logins)
    }

    @Test fun navigationWhileProcessStoppedWaitsUntilEligibleResume() {
        store.replace(jwt(99))
        route = "tickets/my-bets"
        observer.onStop(owner)
        route = "cart/current"
        observer.onRouteEntered()
        assertNull(accept)
        observer.onResume(owner)
        assertNotNull(accept)
    }

    @Test fun unknownAndExcludedRoutesPreservePendingMissingSessionUntilKnownBaseEntry() {
        store.replace(jwt(200))
        route = "tickets/my-bets"
        observer.onStop(owner)
        store.clear()
        observer.onResume(owner)
        route = "unknown/future"
        observer.onRouteEntered()
        assertEquals(0, logins)
        route = "support/terms?tab=FAQ"
        observer.onRouteEntered()
        assertEquals(1, logins)
    }

    @Test fun legacyBaseOnboardingTutorialConsumesPendingBackground() {
        store.replace(jwt(99))
        route = "onboarding/tutorial"
        observer.onStop(owner)
        observer.onResume(owner)
        assertNotNull("CX TutorialApostaShake is Base even though Compose route is onboarding", accept)
    }

    @Test fun onlyExplicitHistoricalDetailOriginEnablesTheOldBaseController() {
        store.replace(jwt(99))
        route = "tickets/bet-detail"
        val historicalObserver = ForegroundSessionLifecycleObserver(store, { route }, { 100 },
            { logins++ }, { check -> accept = { if (store.clearForegroundSession(check)) logins++ } },
            historicalBetDetail = { true })
        historicalObserver.onStop(owner)
        historicalObserver.onResume(owner)
        assertNotNull(accept)
    }

    @Test fun retainedProcessBackgroundSurvivesObserverRecreationOnNonBaseRoute() {
        store.replace(jwt(99))
        route = "tickets/my-bets"
        val processState = ForegroundSessionBackgroundState()
        fun createObserver() = ForegroundSessionLifecycleObserver(store, { route }, { 100 },
            { logins++ }, { check -> accept = { if (store.clearForegroundSession(check)) logins++ } },
            backgroundState = processState)
        val oldObserver = createObserver()
        oldObserver.onResume(owner)
        oldObserver.onStop(owner)
        val recreatedObserver = createObserver()
        recreatedObserver.onResume(owner)
        assertNull(accept)
        route = "cart/current"
        recreatedObserver.onRouteEntered()
        assertNotNull("CX global flag survives recreation until a Base host consumes it", accept)
    }

    @Test fun mixedPrizeOptionsReturnKeepsPendingUntilInternalChannelEntry() {
        store.replace(jwt(99))
        route = "retail/prize/{betId}"
        var channelHost = false
        val prizeObserver = ForegroundSessionLifecycleObserver(store, { route }, { 100 },
            { logins++ }, { check -> accept = { if (store.clearForegroundSession(check)) logins++ } },
            prizeChannelHost = { channelHost })
        prizeObserver.onResume(owner)
        prizeObserver.onStop(owner)
        prizeObserver.onResume(owner)
        prizeObserver.onRouteEntered()
        assertNull("Options remain the non-Base bet-detail host", accept)
        channelHost = true
        prizeObserver.onRouteEntered()
        assertNotNull("Internal channel entry resumes a CX Base controller", accept)
        assertEquals(0, logins)
        assertEquals(jwt(99), store.token())
        accept = null
        prizeObserver.onRouteEntered()
        assertNull("Internal stage entry consumes process background only once", accept)
    }

    @Test fun mixedPrizeChannelResumeChecksExpiredAndMissingSessions() {
        route = "retail/prize/{betId}"
        val prizeObserver = ForegroundSessionLifecycleObserver(store, { route }, { 100 },
            { logins++ }, { check -> accept = { if (store.clearForegroundSession(check)) logins++ } },
            prizeChannelHost = { true })
        store.replace(jwt(99))
        prizeObserver.onStop(owner)
        prizeObserver.onResume(owner)
        assertNotNull(accept)
        accept = null
        store.clear()
        prizeObserver.onStop(owner)
        prizeObserver.onResume(owner)
        assertEquals(1, logins)
        assertNull(accept)
    }

    private fun jwt(exp: Long) = "e30.${Base64.getUrlEncoder().withoutPadding().encodeToString("{\"exp\":$exp}".toByteArray())}.c2ln"
}
