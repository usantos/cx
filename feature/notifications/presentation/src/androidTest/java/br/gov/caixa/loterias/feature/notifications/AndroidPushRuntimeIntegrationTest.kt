package br.gov.caixa.loterias.feature.notifications

import android.app.Activity
import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.domain.push.repository.PushRegistrationLocalRepository
import br.gov.caixa.loterias.core.domain.push.repository.PushRegistrationRepository
import br.gov.caixa.loterias.core.domain.push.usecase.RegisterPushDevice
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.notifications.data.datasource.push.PushTokenDataSource
import br.gov.caixa.loterias.feature.notifications.data.push.PushFirebaseOptions
import br.gov.caixa.loterias.feature.notifications.di.AndroidPushRuntime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidPushRuntimeIntegrationTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Test fun runtimeInitializesOnceRefreshesOnLoginAndRegistersRotatedTokens() {
        val capturedIdentity = AuthenticatedSessionIdentity("a".repeat(64))
        val session = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = capturedIdentity
            override fun isCurrent(identity: AuthenticatedSessionIdentity) = identity === capturedIdentity
        }
        val registeredTokens = mutableListOf<String>()
        val completed = CountDownLatch(3)
        val register = RegisterPushDevice(
            sessions = session,
            cpfFor = { "12345678901" },
            repository = PushRegistrationRepository { _, _, token ->
                synchronized(registeredTokens) { registeredTokens += token }
                completed.countDown()
                true
            },
            store = object : PushRegistrationLocalRepository {
                override suspend fun contains(identity: AuthenticatedSessionIdentity, marker: String) = false
                override suspend fun remember(identity: AuthenticatedSessionIdentity, marker: String) = Unit
            }
        )
        val initializations = AtomicInteger()
        val source = PushTokenDataSource { _, _ ->
            initializations.incrementAndGet()
            val provider: suspend () -> String? = { "refreshed-token" }
            provider
        }
        val runtime = AndroidPushRuntime.withTokenSource(
            context.applicationContext,
            session,
            MutableStateFlow<AuthenticatedSessionIdentity?>(capturedIdentity),
            register,
            simulated = false,
            options = PushFirebaseOptions("app-id", "api-key", "project-id", "sender-id"),
            tokenSource = source
        )
        val application = context.applicationContext as Application
        try {
            runtime.start(application)
            runtime.start(application)
            instrumentation.runOnMainSync { runtime.onActivityResumed(Activity()) }
            runtime.onNewToken("rotated-token")
            assertTrue("login refresh and token rotation should register", completed.await(5, TimeUnit.SECONDS))
            assertEquals(1, initializations.get())
            assertEquals(setOf("refreshed-token", "rotated-token"), synchronized(registeredTokens) { registeredTokens.toSet() })
        } finally {
            application.unregisterActivityLifecycleCallbacks(runtime)
        }
    }

    @Test fun failedLoginRegistrationRetriesOnTheNextActivityResume() {
        val identity = AuthenticatedSessionIdentity("a".repeat(64))
        val session = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = identity
            override fun isCurrent(candidate: AuthenticatedSessionIdentity) = candidate === identity
        }
        val attempted = CountDownLatch(1)
        val retried = CountDownLatch(1)
        val attempts = AtomicInteger()
        val register = registration(session) {
            if (attempts.incrementAndGet() == 1) {
                attempted.countDown()
                false
            } else {
                retried.countDown()
                true
            }
        }
        val runtime = AndroidPushRuntime.withTokenSource(
            context.applicationContext,
            session,
            MutableStateFlow<AuthenticatedSessionIdentity?>(identity),
            register,
            simulated = false,
            options = PushFirebaseOptions("app-id", "api-key", "project-id", "sender-id"),
            tokenSource = PushTokenDataSource { _, _ -> { "device-token" } }
        )
        val application = context.applicationContext as Application
        try {
            runtime.start(application)
            assertTrue("login must attempt registration", attempted.await(5, TimeUnit.SECONDS))
            instrumentation.runOnMainSync { runtime.onActivityResumed(Activity()) }
            assertTrue("resume must retry a failed registration", retried.await(5, TimeUnit.SECONDS))
            assertEquals(2, attempts.get())
        } finally {
            application.unregisterActivityLifecycleCallbacks(runtime)
        }
    }

    @Test fun identityReplacedDuringTokenRetrievalCannotRegisterTheOldToken() {
        val oldIdentity = AuthenticatedSessionIdentity("a".repeat(64))
        val newIdentity = AuthenticatedSessionIdentity("b".repeat(64))
        val current = AtomicReference<AuthenticatedSessionIdentity?>(oldIdentity)
        val session = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = current.get()
            override fun isCurrent(identity: AuthenticatedSessionIdentity) = identity === current.get()
        }
        val tokenStarted = CountDownLatch(1)
        val tokenReleased = CompletableDeferred<Unit>()
        val registered = CountDownLatch(1)
        val attempts = AtomicInteger()
        val register = registration(session) {
            attempts.incrementAndGet()
            registered.countDown()
            true
        }
        val runtime = AndroidPushRuntime.withTokenSource(
            context.applicationContext,
            session,
            MutableStateFlow<AuthenticatedSessionIdentity?>(oldIdentity),
            register,
            simulated = false,
            options = PushFirebaseOptions("app-id", "api-key", "project-id", "sender-id"),
            tokenSource = PushTokenDataSource { _, _ ->
                {
                    tokenStarted.countDown()
                    tokenReleased.await()
                    "device-token"
                }
            }
        )
        val application = context.applicationContext as Application
        try {
            runtime.start(application)
            assertTrue(tokenStarted.await(5, TimeUnit.SECONDS))
            current.set(newIdentity)
            tokenReleased.complete(Unit)
            assertFalse("stale refresh must not cross sessions", registered.await(1, TimeUnit.SECONDS))
            instrumentation.runOnMainSync { runtime.onActivityResumed(Activity()) }
            assertTrue("current session must still retry", registered.await(5, TimeUnit.SECONDS))
            assertEquals(1, attempts.get())
        } finally {
            tokenReleased.complete(Unit)
            application.unregisterActivityLifecycleCallbacks(runtime)
        }
    }

    private fun registration(session: AuthenticatedSessionIdentityProvider, attempt: () -> Boolean) = RegisterPushDevice(
        sessions = session,
        cpfFor = { "12345678901" },
        repository = PushRegistrationRepository { _, _, _ -> attempt() },
        store = object : PushRegistrationLocalRepository {
            override suspend fun contains(identity: AuthenticatedSessionIdentity, marker: String) = false
            override suspend fun remember(identity: AuthenticatedSessionIdentity, marker: String) = Unit
        }
    )
}
