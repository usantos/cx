package br.gov.caixa.loterias.apostas

import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.apostas.environment.runtime.ProcessEnvironmentSettings
import br.gov.caixa.loterias.apostas.runtime.host.AppHostRuntime
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import java.lang.reflect.Proxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Assume.assumeTrue
import org.junit.Test

class AppHostRuntimeEnvironmentTest {
    @Test fun alreadyDurableSelectionAcknowledgesWithoutEndingSessionOrInitializingClients() = runBlocking {
        val selected = TestEnvironment.PILOTO
        val preferences = Proxy.newProxyInstance(
            SharedPreferences::class.java.classLoader, arrayOf(SharedPreferences::class.java),
        ) { _, method, args ->
            when (method.name) {
                "getString" -> {
                    assertEquals("AMBIENTE_SELECIONADO", args[0])
                    selected.name
                }
                else -> error("Durable selection must not write preferences: ${method.name}")
            }
        } as SharedPreferences
        val context = object : ContextWrapper(InstrumentationRegistry.getInstrumentation().targetContext) {
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
                assertEquals("AMBIENTE_SELECIONADO", name)
                return preferences
            }
        }
        var cancellations = 0
        val authentication = object : AuthenticationGateway {
            override fun cancelAuthorization() { cancellations++ }
            override fun logout() = error("Must not log out")
            override fun createAuthorizationIntent(): Intent = error("Must not authorize")
            override suspend fun completeAuthorization(result: Intent?) = error("Must not authorize")
        }
        val tokens = SessionTokenStore().apply { replace("retained-access-token") }
        val runtime = AppHostRuntime(
            ProcessEnvironmentSettings(context), authentication, tokens,
            dagger.Lazy { error("Environment acknowledgement must not initialize clients") },
            FixedCoroutineDispatchers(Dispatchers.IO),
        )

        assertTrue(runtime.environmentChange(selected))
        assertTrue(runtime.environmentChange(selected))
        assertEquals(0, cancellations)
        assertEquals("retained-access-token", tokens.token())
    }

    @Test fun editableSelectionClearsSessionBeforeDurableWriteWithoutReplacingClients() = runBlocking {
        assumeTrue(BuildConfig.FLAVOR != "prd")
        val fixture = EditableEnvironment()
        val configuration = fixture.settings.effective

        assertTrue(fixture.runtime.environmentChange(TestEnvironment.PILOTO))

        assertEquals(listOf("cancel-authorization", "persist-selection"), fixture.events)
        assertNull(fixture.tokens.token())
        assertEquals("PILOTO", fixture.settings.persistedSelection)
        assertSame(configuration, fixture.settings.effective)
        assertTrue(fixture.runtime.environmentChange(TestEnvironment.PILOTO))
        assertEquals(2, fixture.events.size)
    }

    @Test fun failedDurableWritePropagatesAndMemoryUpdateDoesNotPreventRetry() = runBlocking {
        assumeTrue(BuildConfig.FLAVOR != "prd")
        val fixture = EditableEnvironment()
        fixture.commitAllowed = false

        try {
            fixture.runtime.environmentChange(TestEnvironment.PILOTO)
            fail("A failed durable write must propagate")
        } catch (_: IllegalStateException) {
            assertEquals("EXTERNO_ESTEIRA", fixture.settings.persistedSelection)
            assertNull(fixture.tokens.token())
        }
        fixture.commitAllowed = true
        assertTrue(fixture.runtime.environmentChange(TestEnvironment.PILOTO))
        assertEquals(listOf("cancel-authorization", "persist-selection", "cancel-authorization", "persist-selection"), fixture.events)
        assertEquals("PILOTO", fixture.settings.persistedSelection)
    }

    private class EditableEnvironment {
        val events = mutableListOf<String>()
        val tokens = SessionTokenStore().apply { replace("old-environment-access-token") }
        var commitAllowed = true
        private var memory = "EXTERNO_ESTEIRA"
        private val editor: SharedPreferences.Editor = Proxy.newProxyInstance(
            SharedPreferences.Editor::class.java.classLoader, arrayOf(SharedPreferences.Editor::class.java),
        ) { proxy, method, args ->
            when (method.name) {
                "putString" -> {
                    assertEquals("AMBIENTE_SELECIONADO", args[0])
                    memory = args[1] as String
                    proxy
                }
                "commit" -> {
                    assertNull("Token must be cleared before persistence", tokens.token())
                    events += "persist-selection"
                    commitAllowed
                }
                else -> error("Unexpected preference editor operation: ${method.name}")
            }
        } as SharedPreferences.Editor
        private val preferences: SharedPreferences = Proxy.newProxyInstance(
            SharedPreferences::class.java.classLoader, arrayOf(SharedPreferences::class.java),
        ) { _, method, args ->
            when (method.name) {
                "getString" -> {
                    assertEquals("AMBIENTE_SELECIONADO", args[0])
                    memory
                }
                "edit" -> editor
                else -> error("Unexpected preference operation: ${method.name}")
            }
        } as SharedPreferences
        private val context = object : ContextWrapper(InstrumentationRegistry.getInstrumentation().targetContext) {
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
                assertEquals("AMBIENTE_SELECIONADO", name)
                return preferences
            }
        }
        val settings = ProcessEnvironmentSettings(context)
        private val authentication = object : AuthenticationGateway {
            override fun cancelAuthorization() {
                if (events.isEmpty()) assertEquals("old-environment-access-token", tokens.token())
                events += "cancel-authorization"
            }
            override fun logout() = error("Must not log out")
            override fun createAuthorizationIntent(): Intent = error("Must not authorize")
            override suspend fun completeAuthorization(result: Intent?) = error("Must not authorize")
        }
        val runtime = AppHostRuntime(
            settings, authentication, tokens,
            dagger.Lazy { error("Environment selection must not initialize clients") },
            FixedCoroutineDispatchers(Dispatchers.IO),
        )
    }

}
