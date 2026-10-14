package br.gov.caixa.loterias.apostas

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import br.gov.caixa.loterias.core.auth.SessionTokenStore
import java.util.Base64
import org.junit.Assert.*
import org.junit.Test

class ForegroundSessionStartupRestartTest {
    private val owner = object : LifecycleOwner {
        override val lifecycle: Lifecycle get() = error("Lifecycle state is supplied by the test")
    }
    private fun token(exp: Long) = "e30.${Base64.getUrlEncoder().withoutPadding().encodeToString("{\"exp\":$exp}".toByteArray())}.c2ln"

    @Test fun currentExpiredAcknowledgmentClearsBeforeLaunchingFreshStartupAndFinishingPreviousHost() {
        val store = SessionTokenStore().apply { replace(token(99)) }
        val operations = mutableListOf<String>()
        val restart = ForegroundSessionStartupRestarter(
            { assertNull("Startup must not inherit expired credentials", store.token()); operations += "startup" },
            { operations += "finish" },
        )
        val notices = ForegroundSessionNoticeController(store, { 100 }, restart::restart)
        val marker = notices.show(store.foregroundCheck(100))!!
        val accept = notices.restore(marker)!!
        assertTrue("Do not restart before Entendi", operations.isEmpty())
        accept()
        assertEquals(listOf("startup", "finish"), operations)
        assertNull(notices.restore(marker))
    }

    @Test fun missingSessionOnEligibleForegroundRestartsStartupWithoutAnExpiryDialog() {
        val store = SessionTokenStore()
        val operations = mutableListOf<String>()
        val restart = ForegroundSessionStartupRestarter({ operations += "startup" }, { operations += "finish" })
        val observer = ForegroundSessionLifecycleObserver(store, { "home" }, { 100 }, restart::restart,
            { error("Missing session must not ask for expiry acknowledgment") })
        observer.onResume(owner)
        assertTrue(operations.isEmpty())
        observer.onStop(owner)
        observer.onResume(owner)
        assertEquals(listOf("startup", "finish"), operations)
        observer.onResume(owner)
        assertEquals("One restart per consumed process background", 2, operations.size)
    }

    @Test fun staleExpiryAcknowledgmentCannotRestartOrFinishReplacementSession() {
        val store = SessionTokenStore().apply { replace(token(99)) }
        val operations = mutableListOf<String>()
        val restart = ForegroundSessionStartupRestarter({ operations += "startup" }, { operations += "finish" })
        val notices = ForegroundSessionNoticeController(store, { 100 }, restart::restart)
        val accept = notices.restore(notices.show(store.foregroundCheck(100))!!)!!
        val replacement = token(300)
        store.replace(replacement)
        accept()
        assertEquals(replacement, store.token())
        assertTrue(operations.isEmpty())
    }

    @Test fun failedStartupLaunchNeverFinishesPreviousHost() {
        var finished = false
        val failure = IllegalStateException("Synthetic launch failure")
        val restart = ForegroundSessionStartupRestarter({ throw failure }, { finished = true })
        assertSame(failure, runCatching { restart.restart() }.exceptionOrNull())
        assertFalse("Never finish the current host if its replacement did not launch", finished)
    }
}
