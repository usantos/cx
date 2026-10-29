package br.gov.caixa.loterias.apostas

import android.content.Context
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class PrdEnvironmentChangeBlockedTest {
    @Test fun environmentChangeIsRejectedWithoutChangingSessionPreferencesOrClients() = runBlocking {
        assertEquals("prd", BuildConfig.FLAVOR)
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        val preferences = target.getSharedPreferences("prd-environment-lock-test", Context.MODE_PRIVATE)
        assertTrue(preferences.edit().clear().putString("AMBIENTE_SELECIONADO", "EXTERNO_ESTEIRA").commit())
        try {
            val context = object : ContextWrapper(target) {
                override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
                    assertEquals("AMBIENTE_SELECIONADO", name)
                    return preferences
                }
            }
            val settings = ProcessEnvironmentSettings(context)
            val configuration = settings.effective
            val tokens = SessionTokenStore().apply { replace("retained-prd-access-token") }
            val authentication = object : AuthenticationGateway {
                override fun cancelAuthorization() = error("Blocked environment change must not cancel authorization")
                override fun logout() = error("Blocked environment change must not log out")
                override fun createAuthorizationIntent(): Intent = error("Blocked environment change must not authorize")
                override suspend fun completeAuthorization(result: Intent?) = error("Blocked environment change must not authorize")
            }
            val runtime = AppHostRuntime(
                settings,
                authentication,
                tokens,
                dagger.Lazy { error("Blocked environment change must not initialize clients") },
                FixedCoroutineDispatchers(Dispatchers.IO)
            )

            assertFalse(runtime.environmentChange(TestEnvironment.PILOTO))
            assertFalse(runtime.environmentChange(TestEnvironment.HMP))

            assertFalse(settings.selectorVisible)
            assertEquals(TestEnvironment.EXTERNO_ESTEIRA, settings.selected)
            assertEquals("EXTERNO_ESTEIRA", settings.persistedSelection)
            assertEquals(mapOf("AMBIENTE_SELECIONADO" to "EXTERNO_ESTEIRA"), preferences.all)
            assertEquals("retained-prd-access-token", tokens.token())
            assertSame(configuration, settings.effective)
        } finally {
            assertTrue(preferences.edit().clear().commit())
        }
    }
}
